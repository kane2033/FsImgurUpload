package com.focusstart.imgurupload.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.focusstart.imgurupload.R
import com.focusstart.imgurupload.UriFileResolver
import com.focusstart.imgurupload.network.ApiFactory
import com.focusstart.imgurupload.network.repositories.ImgurRepository
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class MainFragment : Fragment() {

    // Ссылка на выбранную/сделанную картинку
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val takePicture = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccessful ->
            if (isSuccessful) {
                // Если фото сделано, imageUri, переданный в launch
                // имеет ссылку на сделанное фото
                imageView.setImageURI(photoUri)
            }
            else {
                photoUri = null
            }
        }

        val getImageFromGallery = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                photoUri = it
                imageView.setImageURI(photoUri)
            }
        }

        val requestCameraPermission =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Генерируем путь, где будет храниться новое фото
                    photoUri = UriFileResolver.generatePhotoUri(requireContext())
                    // Делаем фото с полученными правами
                    takePicture.launch(photoUri)

                } else {
                    // Разрешение не получено, поясняем, почему фича не работает
                    Toast.makeText(
                        requireContext(),
                        "Невозможно выбрать картинку, " +
                                "т.к. разрешение на использование камеры отсутствует.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        val requestExternalStoragePermission =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Разрешение получено, получаем картинку из галереи
                    getImageFromGallery.launch("image/*")
                } else {
                    // Разрешение не получено, поясняем, почему фича не работает
                    Toast.makeText(
                        requireContext(),
                        "Невозможно выбрать картинку, " +
                                "т.к. разрешение на получение " +
                                "картинки из галереи отсутствует.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        takePictureButton.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA)) {
                // Генерируем путь, где будет храниться новое фото
                photoUri = UriFileResolver.generatePhotoUri(requireContext())
                takePicture.launch(photoUri)
            }
            else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        chooseImageButton.setOnClickListener {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getImageFromGallery.launch("image/*")
            }
            else {
                requestExternalStoragePermission.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        // Загрузка изображения на imgur
        uploadImageButton.setOnClickListener {
            val uri = photoUri
            if (uri != null) {
                uploadImageButton.isEnabled = false
                uploadImage(imageTitleView.text.toString(),
                    imageTitleView.text.toString(), uri)
            }
            else {
                Toast.makeText(
                    requireContext(),
                    "Картинка не выбрана",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    // Загрузка картинки с помощью корутин
    private fun uploadImage(title: String, description: String, imageUri: Uri) {
        val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
        val scope = CoroutineScope(coroutineContext)
        val repository = ImgurRepository(ApiFactory.imgurApi)
        scope.launch {
            // Получение картики по uri
            val imageFile = UriFileResolver.toFile(
                imageUri,
                requireContext(), requireActivity().contentResolver
            )
            // Получение результата запроса
            val response =
                repository.postImage(imageFile!!, title, description)
            // Вставка ссылки в интерфейс, уведомление о результате запроса
            requireActivity().runOnUiThread {
                imageLinkView.text = response?.data?.link
                Toast.makeText(
                    requireContext(),
                    "Статус запроса = ${response?.status}",
                    Toast.LENGTH_SHORT
                ).show()
                uploadImageButton.isEnabled = true
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED
    }
}