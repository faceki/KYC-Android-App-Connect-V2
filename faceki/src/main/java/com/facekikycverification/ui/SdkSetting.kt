package com.facekikycverification.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.whenCreated
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
import com.facekikycverification.startup.FacekiVerification
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

    internal lateinit var clientId:String
    internal lateinit var email:String

    companion object {
        lateinit  var sdkSettingInstance: SdkSetting
    }
    init {
        sdkSettingInstance=this
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
        email = intent.getStringExtra("Email").toString()

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
        val hashMap = HashMap<String, String>()
        hashMap["client_id"] = clientId
        hashMap["email"] = email

        MyApplication.spinnerStart(this)
        ApiCall.instance?.getToken(hashMap, this)
        ApiCall.instance?.sdkSetting(clientId, this)
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
//                    startActivityForResult(intent, 123)
                }
                /*getString(R.string.upload) -> {
                    uploadFiles()
                }
                getString(R.string.finish) -> {
                    val intent = Intent(this, Successful::class.java)
                    intent.putExtra("SuccessPageModel", successPageModel)
                    startActivity(intent)
                    finish()
                }*/
            }
        }
    }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (data != null && resultCode == Activity.RESULT_OK) {
             if (requestCode == 123) {
                 val args = data.getBundleExtra("BUNDLE")
                 items.clear()
                 items = (args!!.getSerializable("IDM") as ArrayList<IdsModel>?)!!

                 binding.start.text = getString(R.string.upload)
                 setRV()
             }
         }
     }*/

    /* private fun uploadFiles() {
         val parts: ArrayList<MultipartBody.Part> = ArrayList()
         startPosition = position
         for (i in 0..1) {
             val name = if (i == 0) "doc_front_image" else "doc_back_image"
             if (items[position].imagePath.isNotEmpty()) {
                 parts.add(
                     RetrofitUtils.createFilePart(
                         name,
                         items[position].imagePath,
                         RetrofitUtils.MEDIA_TYPE_IMAGE_ALL
                     )
                 )
             }
             if (items[position].idName.lowercase().contains("passport")) {
                 if (i == 1)
                     position++
             } else {
                 position++
             }
         }
         if (items[items.size - 1].imagePath.isNotEmpty()) {
             parts.add(
                 RetrofitUtils.createFilePart(
                     "selfie_image",
                     items[items.size - 1].imagePath,
                     RetrofitUtils.MEDIA_TYPE_IMAGE_ALL
                 )
             )
         }

         MyApplication.spinnerStart(this)
         ApiCall.instance?.uploadFiles(MyApplication.getSharedPrefString("token"), parts, this)
     }*/

    override fun onSuccess(type: String, data: Any?) {
        MyApplication.spinnerStop()
        if (type == "SdkSetting") {
            val responseGet: Response<Any> = data as Response<Any>
            if (responseGet.isSuccessful) {
                val gson = Gson()
                val jsonFavorites = gson.toJson(responseGet.body())

                val tempData = object : TypeToken<SdkSettingResponse>() {}.type
                ssr = gson.fromJson(jsonFavorites, tempData)
                ssr?.let { setArrayList(it.response) }
            } else
                MyApplication.showMassage(this, getString(R.string.error))
        } else if (type == "GetToken") {
            val responseGet: Response<Any> = data as Response<Any>
            if (responseGet.isSuccessful) {
                val objectType = object : TypeToken<GetTokenResponse>() {}.type
                val getTokenResponse: GetTokenResponse = Gson().fromJson(Gson().toJson(responseGet.body()), objectType)
                MyApplication.setSharedPrefString("token", "Bearer " + getTokenResponse.token)
            } else
                MyApplication.showMassage(this, getString(R.string.error))
        }
        /*else if (type == "UploadFiles") {
            val responseGet: Response<Any> = data as Response<Any>
            if (responseGet.isSuccessful) {
                setResponse("success", "")
                if (items.size - 1 != position)
                    uploadFiles()
            } else {
                responseGet.errorBody()?.string()?.let {
                    var errorMessage = it.substring(it.lastIndexOf(":"))
                    errorMessage = errorMessage.replace(":", "")
                        .replace("}", "")
                        .replace("\"", "")
                    setResponse("fail", errorMessage)
                }
            }
        }*/
    }

    override fun onFailure(data: Any?) {
        MyApplication.spinnerStop()
        MyApplication.showMassage(this, getString(R.string.something_went_wrong))
    }

    /*private fun setResponse(type: String, error: String) {
        if (items.size - 1 == position || type == "fail")
            binding.start.text = getString(R.string.finish)
        for (i in startPosition until position) {
            items[i].uploadingStatus = type
        }
        items[items.size - 1].uploadingStatus = type
        setRV()

        successPageModel = SuccessPageModel()
        when (type) {
            "fail" -> {
                successPageModel.image = R.drawable.fail_gif
                successPageModel.title = "Invalid"
                successPageModel.errorMessage = error
                successPageModel.link = ssr?.response?.invalid_redirect_url
            }
            "success" -> {
                successPageModel.image = R.drawable.success_gif
                successPageModel.title = "Successful"
                successPageModel.errorMessage = ssr?.response?.success_meaasge
                successPageModel.link = ssr?.response?.success_redirect_url
            }
        }
    }*/

    // set up Array List
    private var ids: IdsModel? = null

    private fun setArrayList(ssr: SdkSettingResponse.Response) {
        items.clear()
        for (i in 0 until ssr.number_of_doc) {
            ids = IdsModel()
            when (i) {
                0 -> setIdsModel(ssr.doc_type_one.trim())
                1 -> setIdsModel(ssr.doc_type_two.trim())
                2 -> setIdsModel(ssr.doc_type_three.trim())
            }
        }
        addImageItem()
        setRV()
    }

    private fun setIdsModel(name: String) {
        val text1 = getString(R.string.place_your)
        val text2 = getString(R.string.within_the_frame_nand_make_sure_it_s_clear_with_no_reflections)

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
            ids?.idName = getString(R.string.scan)+" $name"
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