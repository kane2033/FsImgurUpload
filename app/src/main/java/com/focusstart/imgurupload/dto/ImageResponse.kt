package com.focusstart.imgurupload.dto

data class ImageResponse(
    val success: Boolean,
    val status: Int,
    val data: UploadedImage
)