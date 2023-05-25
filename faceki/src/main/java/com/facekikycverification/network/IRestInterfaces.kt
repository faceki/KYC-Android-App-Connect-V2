package com.facekikycverification.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface IRestInterfaces {

    @GET("auth/api/access-token")
    fun getToken(@Query("clientId") clientId: String, @Query("clientSecret") clientSecret: String): Call<Any>

    @GET("https://apidev.facechain.org/kycrules/api/kycrules")
    fun sdkSetting(@Header("Authorization") token: String): Call<Any>

    @Multipart
    @POST("kycverify/api/kycverify/kyc-verification")
    fun uploadFiles(
        @Header("Authorization") token: String,
        @Part imageFile: List<MultipartBody.Part>
    ): Call<Any>
}
