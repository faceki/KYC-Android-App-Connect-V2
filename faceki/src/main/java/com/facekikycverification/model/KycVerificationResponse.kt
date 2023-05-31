package com.facekikycverification.model

data class KycVerificationResponse(
    val responseCode: Int?,
    val data: Data?
){
    data class Data(
        val error: Error?,
        val responseID: String?,
        val images: Images?
    )
    data class Error(
        val message: String?,
        val code: Int?
    )
    data class Images(
        val doc_back_image: String?,
        val doc_front_image: String?,
        val selfie_image: String?,
    )
}
