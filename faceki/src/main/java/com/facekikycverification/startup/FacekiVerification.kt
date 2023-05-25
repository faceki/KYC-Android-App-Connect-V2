package com.facekikycverification.startup

import android.content.Context
import android.content.Intent
import com.facekikycverification.ui.SdkSetting

class FacekiVerification {

    companion object {

        fun initiateSMSDK(context: Context, clientId: String, clientSecret: String) {
            context.startActivity(
                Intent(context, SdkSetting::class.java)
                    .putExtra("ClientId", clientId)
                    .putExtra("clientSecret", clientSecret)
            )
        }

        fun restartKYCProcess(context: Context) {
            context.startActivity(
                Intent(context, SdkSetting::class.java)
                    .putExtra("ClientId", SdkSetting.sdkSettingInstance.clientId)
                    .putExtra("clientSecret", SdkSetting.sdkSettingInstance.clientSecret)
            )
        }
    }
}
