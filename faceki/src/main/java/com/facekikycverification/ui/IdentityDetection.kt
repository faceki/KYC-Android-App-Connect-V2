package com.facekikycverification.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.facekikycverification.R
import com.facekikycverification.databinding.ActivityIdentityDetectionBinding
import com.facekikycverification.model.IdsModel
import com.facekikycverification.response.SdkSettingResponse
import com.facekikycverification.utils.MyApplication
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class IdentityDetection : AppCompatActivity() {
    lateinit var binding: ActivityIdentityDetectionBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var items: ArrayList<IdsModel> = ArrayList()
    private var position = 0

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var cameraPosition = 0

    var ssr: SdkSettingResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_identity_detection)

        // Get data
        getIntentData()
        setUpCamera()
        setUpLayout()
    }

    private fun getIntentData() {
        val args = intent.getBundleExtra("BUNDLE")
        items.clear()
        items = args!!.getSerializable("IDM") as ArrayList<IdsModel>
        ssr = intent.getSerializableExtra("SSR") as SdkSettingResponse
    }

    private fun setUpCamera() {
        if (allPermissionsGranted())
            startCamera()
        else
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpLayout() {
        val ids = items[position]

//        binding.title.text = if (ids.idName.lowercase().contains("passport")) ids.idName else ids.idName + " " + ids.side
        binding.title.text = ids.idName
        binding.sideImage.setImageResource(ids.sideImageWhite)
        binding.sideText.text = if (ids.idName.lowercase().contains("passport")) "" else ids.side.uppercase()
        binding.description.text = ids.desc
    }

    override fun onStart() {
        super.onStart()

        binding.captureImage.setOnClickListener {
            MyApplication.spinnerStart(this)
            binding.captureImage.isEnabled = false
            takePhoto()
        }

        binding.flipIcon.setOnClickListener {
            if (cameraPosition == 0) {
                cameraPosition = 1
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                cameraPosition = 0
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }
            setUpCamera()
        }

    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    MyApplication.spinnerStop()
                    binding.captureImage.isEnabled = true
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    try {
                        getBitmapFromUri(savedUri)
                    } catch (e: IOException) {
                        Log.d("", e.printStackTrace().toString())
                    }
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d("", msg)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, getString(R.string.permission_not_granted_by_user), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun rotateBitmap(bitmapToRotate: Bitmap, rotationInDegrees: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(rotationInDegrees.toFloat())
        return Bitmap.createBitmap(
            bitmapToRotate, 0, 0,
            bitmapToRotate.width, bitmapToRotate.height, matrix,
            true
        )
    }

    private fun exifToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri) {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor

        val exif = ExifInterface(uri.path!!)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees = exifToDegrees(rotation)

        // create bitmap
        val myBitmap = rotateBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor), rotationInDegrees)
//        val myBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        if (myBitmap != null) {
            val x = (myBitmap.width / 10)
            val y = (myBitmap.height / 5)
            val w = (myBitmap.width / 10) * 8
            val h = (myBitmap.height / 5) * 2
            val tb = Bitmap.createBitmap(myBitmap, x, y, w, h)
//        val tb = Bitmap.createBitmap(myBitmap, 0, myBitmap.height / 4, myBitmap.width, myBitmap.height / 2)
            // save image
            items[position].imagePath = saveImage(tb)
            movePreviewScreen()
        }
    }

    private fun saveImage(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val wallpaperDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/Images/")
        val wallpaperDirectory = File(this.filesDir.path)

        try {
            val f = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun movePreviewScreen() {
        MyApplication.spinnerStop()
        binding.captureImage.isEnabled = true
        val intent = Intent(this, ShowIdentity::class.java)
        intent.putExtra("IDModel", items[position])
        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            position++
            if (items.size - 1 == position)
                moveFaceScreen()
            else
                setUpLayout()
        }
    }

    private fun moveFaceScreen() {
        val intent = Intent(this, FaceDetection::class.java)
        val args = Bundle()
        args.putSerializable("IDM", items as Serializable)
        intent.putExtra("BUNDLE", args)
        intent.putExtra("SSR", ssr)
        startActivity(intent)
        finish()
//        startForGetImagePathResult.launch(intent)
    }

    /* private val startForGetImagePathResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
     { result: ActivityResult ->
         if (result.resultCode == Activity.RESULT_OK) {
             val data = result.data
             if (data != null) {
                 items[position].imagePath = data.getStringExtra("path").toString()
                 moveSdkSetting()
             }
         }
     }

     private fun moveSdkSetting() {
         val intent = Intent()
         val args = Bundle()
         args.putSerializable("IDM", items as Serializable)
         intent.putExtra("BUNDLE", args)
         setResult(Activity.RESULT_OK, intent)
         finish()
     }*/
}

