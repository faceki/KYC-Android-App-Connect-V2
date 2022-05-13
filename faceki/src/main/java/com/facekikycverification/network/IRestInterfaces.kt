package com.facekikycverification.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*


interface IRestInterfaces {

    @FormUrlEncoded
    @POST("getToken")
    fun getToken(@FieldMap fields: HashMap<String, String>): Call<Any>

    @GET("https://faceki.com/backend/api/sdk-settings")
    fun sdkSetting(@Query("client_id") device_id: String): Call<Any>

    @Multipart
    @POST("kyc-verification")
    fun uploadFiles(
        @Header("Authorization") token: String,
        @Part imageFile: List<MultipartBody.Part>
    ): Call<Any>
}