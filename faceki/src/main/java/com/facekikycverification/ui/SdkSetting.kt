package com.facekikycverification.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facekikycverification.R
import com.facekikycverification.adapter.SdkSettingRvAdapter
import com.facekikycverification.databinding.ActivitySdkSettingBinding
import com.facekikycverification.model.IdsModel
import com.facekikycverification.network.ApiCall
import com.facekikycverification.network.IApiCallback
import com.facekikycverification.response.GetTokenResponse
import com.facekikycverification.response.SdkSettingResponse
import com.facekikycverification.utils.MyApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class SdkSetting : AppCompatActivity(), IApiCallback {
    lateinit var binding: ActivitySdkSettingBinding

    var ssr: SdkSettingResponse? = null
    private var items: ArrayList<IdsModel> = ArrayList()

    internal lateinit var clientId: String
    internal lateinit var clientSecret: String

    companion object {
        lateinit var sdkSettingInstance: SdkSetting
    }

    init {
        sdkSettingInstance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sdk_setting)

        clientId = intent.getStringExtra("ClientId").toString()
        clientSecret = intent.getStringExtra("clientSecret").toString()

        setAppLanguage()
        apiCall()
    }

    private fun setAppLanguage() {
        when (Locale.getDefault().displayLanguage) {
            "العربية" -> changeLanguage("ar")
            "English" -> changeLanguage("en")
        }
    }

    private fun changeLanguage(language: String?) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
    }

    private fun apiCall() {
        MyApplication.spinnerStart(this)
        ApiCall.instance?.getToken(clientId, clientSecret, this)
    }

    override fun onStart() {
        super.onStart()
        binding.goBackToRoot.setOnClickListener {
            finish()
        }

        binding.start.setOnClickListener {
            when (binding.start.text) {
                getString(R.string.start) -> {
                    val intent = Intent(this, IdentityDetection::class.java)
                    val args = Bundle()
                    args.putSerializable("IDM", items as Serializable)
                    intent.putExtra("BUNDLE", args)
                    intent.putExtra("SSR", ssr)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onSuccess(type: String, data: Any?) {
        MyApplication.spinnerStop()
        if (type == "SdkSetting") {
            val responseGet: Response<Any> = data as Response<Any>
            if (responseGet.isSuccessful) {
                val gson = Gson()
                val jsonFavorites = gson.toJson(responseGet.body())

                val tempData = object : TypeToken<SdkSettingResponse>() {}.type
                ssr = gson.fromJson(jsonFavorites, tempData)
                ssr?.let { setArrayList(it.data) }
            } else
                MyApplication.showMassage(this, getString(R.string.error))
        } else if (type == "GetToken") {
            val responseGet: Response<Any> = data as Response<Any>
            if (responseGet.isSuccessful) {
                val objectType = object : TypeToken<GetTokenResponse>() {}.type
                val getTokenResponse: GetTokenResponse =
                    Gson().fromJson(Gson().toJson(responseGet.body()), objectType)
                MyApplication.setSharedPrefString(
                    "token",
                    "Bearer " + getTokenResponse.data?.access_token
                )
                getTokenResponse.data?.access_token?.let {
                    ApiCall.instance?.sdkSetting("Bearer " + it, this)
                }
            } else
                MyApplication.showMassage(this, getString(R.string.error))
        }
    }

    override fun onFailure(data: Any?) {
        MyApplication.spinnerStop()
        MyApplication.showMassage(this, getString(R.string.something_went_wrong))
    }

    // set up Array List
    private var ids: IdsModel? = null

    private fun setArrayList(ssr: SdkSettingResponse.Response) {
        items.clear()
        ssr.allowedKycDocuments?.forEachIndexed { index, idType ->
            ids = IdsModel()
            when (index) {
                0 -> setIdsModel(idType.trim())
                1 -> setIdsModel(idType.trim())
                2 -> setIdsModel(idType.trim())
            }
        }
        addImageItem()
        setRV()
    }

    private fun setIdsModel(name: String) {
        val text1 = getString(R.string.place_your)
        val text2 =
            getString(R.string.within_the_frame_nand_make_sure_it_s_clear_with_no_reflections)

        val count = if (name.lowercase() == "passport") 0 else 1
        var frontImageDark = 0
        var backImageDark = 0
        var frontImageWhite = 0
        var backImageWhite = 0
        when {
            name.lowercase().contains("id") -> {
                frontImageDark = R.drawable.id_front
                backImageDark = R.drawable.id_back
                frontImageWhite = R.drawable.id_front_white
                backImageWhite = R.drawable.id_back_white
            }

            name.lowercase().contains("license") -> {
                frontImageDark = R.drawable.driving_front
                backImageDark = R.drawable.driving_back
                frontImageWhite = R.drawable.driving_front_white
                backImageWhite = R.drawable.driving_back_white
            }

            name.lowercase().contains("passport") -> {
                frontImageDark = R.drawable.passport
                frontImageWhite = R.drawable.passport_white
            }
        }
        for (i in 0..count) {
            ids?.idName = getString(R.string.scan) + " $name"
            ids?.desc = text1 + name + text2
            if (i == 0) {
                ids?.sideImageDark = frontImageDark
                ids?.sideImageWhite = frontImageWhite
                ids?.side = getString(R.string.front_side)
            } else {
                ids?.sideImageDark = backImageDark
                ids?.sideImageWhite = backImageWhite
                ids?.side = getString(R.string.back_side)
            }
            items.add(ids!!)
            ids = IdsModel()
        }
    }

    private fun addImageItem() {
        ids?.idName = getString(R.string.take_a_selfie_picture)
        ids?.sideImageDark = R.drawable.take_a_selfie_picture
        items.add(ids!!)
        ids = IdsModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRV() {
        val sdkSettingRvAdapter = SdkSettingRvAdapter(
            items,
            this,
            object : SdkSettingRvAdapter.OnClickListener {
                override fun onCLick(
                    strings: ArrayList<IdsModel>?,
                    position: Int
                ) {

                }
            })
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = sdkSettingRvAdapter
        sdkSettingRvAdapter.notifyDataSetChanged()
    }
}
