package com.facekikycverification.custom

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facekikycverification.R
import com.facekikycverification.utils.ExceptionHandler
import com.facekikycverification.utils.MyApplication
import com.facekikycverification.utils.TouchEffect
import com.google.android.material.snackbar.Snackbar
import java.util.*

open class CustomActivity : AppCompatActivity(), View.OnClickListener {
    @JvmField
    var THIS: CustomActivity? = null
    var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StaticData.init(this)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        THIS = this
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        // changeLanguage();
        //make full transparent statusBar
        /*	if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
			setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
		}
		if (Build.VERSION.SDK_INT >= 19) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		if (Build.VERSION.SDK_INT >= 21) {
			setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}*/
        val crash = intent.getStringExtra(ExceptionHandler.CRASH_REPORT)
        if (crash != null) {
            try {
                showCrashDialog(crash)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        if (!MyApplication.isConnectingToInternet(THIS!!)) {
            MyApplication.popErrorMsg(getString(R.string.error), getString(R.string.check_internet), THIS)
        }
    }

    private fun changeLanguage() {
        ///String lng = StaticData.pref.getString(Const.LANGUAGE, Const.LANG_CH);
        /* String lng = MyApplication.getSharedPrefString(StaticData.appLang);
        if(lng.equals(""))
        {   lng="ENGLISH";
        MyApplication.setSharedPrefString(StaticData.appLang,StaticData.ENGLISH);
        }
        Locale locale = Locale.ENGLISH;
        if (lng.equals(StaticData.SVENSKA))
            locale = new Locale("es", "ES");
        Utils.changeLocale(this, locale);*/
    }

    fun setTouchNClick(id: Int): View? {
        val v = setClick(id)
        v?.setOnTouchListener(TOUCH)
        return v
    }

    fun isLogin(): Boolean {
        return MyApplication.getStatus("isLogin")
    }

    fun logout() {
        MyApplication.setStatus("isLogin", false)
        MyApplication.setSharedPrefInteger("user_id", 0)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun showCrashDialog(report: String) {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName //Version Name
        val verCode = pInfo.versionCode //Version Code
        val b = AlertDialog.Builder(this)
        b.setTitle("App Crashed")
        b.setMessage(
            """
                    Oops! The app crashed in 
                    
                    Model:${Build.MODEL}
                    VERSION NAME:$version
                    VERSION CODE:$verCode
                    Manufacturer: ${Build.MANUFACTURER}
                    Product: ${Build.PRODUCT}
                    Version:${Build.VERSION.SDK_INT}
                    
                    due to below reason:
                    
                    $report
                    """.trimIndent()
        )
        val ocl = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/html"
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("info@logictrixtech.com"))
                i.putExtra(
                    Intent.EXTRA_TEXT, """
     Oops! The app crashed in 
     
     
     Model:${Build.MODEL}
     VERSION NAME:$version
     VERSION CODE:$verCode
     Manufacturer: ${Build.MANUFACTURER}
     Product: ${Build.PRODUCT}
     Version:${Build.VERSION.SDK_INT}
     
     due to below reason:
     
     $report
     """.trimIndent()
                )
                i.putExtra(Intent.EXTRA_SUBJECT, "App Crashed")
                startActivity(Intent.createChooser(i, "Send Mail via:"))
                finish()
            }
            dialog.dismiss()
        }
        b.setCancelable(false)
        b.setPositiveButton("Send Report", ocl)
        b.setNegativeButton("Restart", ocl)
        runOnUiThread { b.create().show() }
    }

    fun setClick(id: Int): View? {
        val v = findViewById<View>(id)
        v?.setOnClickListener(this)
        return v
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    override fun onClick(v: View) {}

    companion object {
        @JvmField
        val TOUCH = TouchEffect()
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }

}