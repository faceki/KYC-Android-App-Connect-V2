package com.facekikycverification.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.facekikycverification.R
import com.facekikycverification.databinding.ActivitySuccessfulBinding
import com.facekikycverification.model.SuccessPageModel
import com.facekikycverification.startup.FacekiVerification
import java.util.*

class Successful : AppCompatActivity() {
    lateinit var binding: ActivitySuccessfulBinding
    private var successPageModel = SuccessPageModel()
//    private var activityStatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_successful)

        successPageModel = intent.getSerializableExtra("SuccessPageModel") as SuccessPageModel

        /* Handler(Looper.getMainLooper()).postDelayed({
             if (activityStatus)
                 openLink()
         }, 5000)*/
    }

    override fun onStart() {
        super.onStart()

        successPageModel.image?.let {
            binding.image.setImageResource(it)
        }
        successPageModel.title.let {
            binding.title.text = it ?: ""
        }
        successPageModel.errorMessage.let {
            binding.errorMessage.text = it ?: ""
        }

        /*binding.link.paint.isUnderlineText = true

        binding.link.setOnClickListener {
            openLink()
        }*/

        binding.back.setOnClickListener {
            FacekiVerification.restartKYCProcess(this);
            finish()
        }
    }

    /*override fun onPause() {
        super.onPause()

        activityStatus = false
    }

    override fun onRestart() {
        super.onRestart()

        startActivity(Intent(this, SdkSetting::class.java))
        finish()
    }*/

    /*private fun openLink() {
        var link = successPageModel.link
        if (link != null) {
            if (link.isNotEmpty()) {
                if (!link.startsWith("https://"))
                    link = "https://$link"
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    ContextCompat.startActivity(this, browserIntent, null)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.wrong_url), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/
}