package com.facekikycverification.response

import java.io.Serializable

data class SdkSettingResponse(
    val data: Response,
    val responseCode: Int
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
        val success_redirect_url: String,
        val _id: String?,
        val companyId: String?,
        val allowedCountries: List<String>?,
        val allowedKycDocuments: List<String>?,
        val allowedNationalities: List<String>?,
        val invalidDocuments: List<String>?,
        val createdAt: String?,
        val dataStorageTime: String?,
        val declined: RedirectData?,
        val invalid: RedirectData?,
        val success: RedirectData?,
        val isRecapturedDocumentInvalid: Boolean?,
        val multiKYCEnabled: Boolean?,
        val storedData: Boolean?,
        val link: String?,
        val livenessCheckType: String?,
        val status: String?,
        val updatedAt: String?,
        val validAge: Int?,
    ) : Serializable
    data class RedirectData(
        val redirect_url: String?,
        val message: String?
    ) : Serializable
}
