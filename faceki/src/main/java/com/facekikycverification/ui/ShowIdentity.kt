package com.facekikycverification.ui

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.facekikycverification.R
import com.facekikycverification.databinding.ActivityShowIdentityBinding
import com.facekikycverification.model.IdsModel
import java.util.*

class ShowIdentity : AppCompatActivity() {
    lateinit var binding: ActivityShowIdentityBinding

    var ids: IdsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_identity)

        ids = intent.getSerializableExtra("IDModel") as IdsModel
        setLayout()
    }

    private fun setLayout() {
        ids?.imagePath.let {
            if (it != null)
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(it))
        }
    }

    override fun onStart() {
        super.onStart()

        binding.confirm.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.retake.setOnClickListener {
            finish()
        }
    }

}

