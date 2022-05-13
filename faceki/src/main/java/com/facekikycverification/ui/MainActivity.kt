package com.facekikycverification.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.facekikycverification.R
import com.facekikycverification.network.ApiCall
import com.facekikycverification.network.IApiCallback
import com.facekikycverification.response.GetTokenResponse
import com.facekikycverification.utils.MyApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, SdkSetting::class.java))
        finish()
    }

  /*  private fun apiCall() {
        val hashMap = HashMap<String, String>()
        hashMap["client_id"] = getString(R.string.client_id)
        hashMap["email"] = getString(R.string.email)

        ApiCall.instance?.getToken(hashMap, this)
    }

    override fun onSuccess(type: String, data: Any?) {
        MyApplication.spinnerStop()
        val responseGet: Response<Any> = data as Response<Any>
        if (responseGet.isSuccessful) {
            val objectType = object : TypeToken<GetTokenResponse>() {}.type
            val getTokenResponse: GetTokenResponse = Gson().fromJson(Gson().toJson(responseGet.body()), objectType)
            MyApplication.setSharedPrefString("token", "Bearer " + getTokenResponse.token)
            startActivity(Intent(this, SdkSetting::class.java))
            finish()
        } else
            MyApplication.showMassage(this, getString(R.string.error))
    }

    override fun onFailure(data: Any?) {
        MyApplication.spinnerStop()
        MyApplication.showMassage(this, data.toString())
    }*/

}