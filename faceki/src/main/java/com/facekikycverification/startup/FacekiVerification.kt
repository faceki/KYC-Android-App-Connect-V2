package com.facekikycverification.startup

import android.content.Context
import android.content.Intent
import com.facekikycverification.ui.SdkSetting

class FacekiVerification {

    companion object {

        fun initiateSMSDK(context: Context, clientId: String, email: String) {
            context.startActivity(
                Intent(context, SdkSetting::class.java)
                    .putExtra("ClientId", clientId)
                    .putExtra("Email", email)
            )
        }

        fun restartKYCProcess(context: Context) {
            context.startActivity(
                Intent(context, SdkSetting::class.java)
                    .putExtra("ClientId", SdkSetting.sdkSettingInstance.clientId)
                    .putExtra("Email", SdkSetting.sdkSettingInstance.email)
            )
        }
    }
}