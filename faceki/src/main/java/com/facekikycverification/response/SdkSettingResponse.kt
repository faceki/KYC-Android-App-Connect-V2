package com.facekikycverification.response

import java.io.Serializable

data class SdkSettingResponse(
    val response: Response,
    val success: Boolean
) : Serializable {
    data class Response(
        val declined_meaasge: String,
        val declined_redirect_url: String,
        val doc_type_one: String,
        val doc_type_three: String,
        val doc_type_two: String,
        val invalid_meaasge: String,
        val invalid_redirect_url: String,
        val number_of_doc: Int,
        val success_meaasge: String,
        val success_redirect_url: String
    ) : Serializable
}