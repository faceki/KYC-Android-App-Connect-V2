package com.facekikycverification.response

data class GetTokenResponse(
    val responseCode: Int?,
    val data: Data?
){
    data class Data(
        val access_token: String?,
        val expires_in: Long?,
        val token_type: String?,

    )
}
