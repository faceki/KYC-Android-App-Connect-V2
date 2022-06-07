package com.facekikycverification.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
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
import com.facekikycverification.databinding.ActivityFaceDetectionBinding
import com.facekikycverification.model.IdsModel
import com.facekikycverification.model.SuccessPageModel
import com.facekikycverification.network.ApiCall
import com.facekikycverification.network.IApiCallback
import com.facekikycverification.network.RetrofitUtils
import com.facekikycverification.response.SdkSettingResponse
import com.facekikycverification.utils.MyApplication
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import okhttp3.MultipartBody
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetection : AppCompatActivity(), IApiCallback {
    lateinit var binding: ActivityFaceDetectionBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var path: String? = ""

    private var items: ArrayList<IdsModel> = ArrayList()
    var startPosition = 0
    var position = 0
    var successPageModel = SuccessPageModel()

    var ssr: SdkSettingResponse? = null

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(supportActionBar)!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_face_detection)

        getIntentData()
        if (allPermissionsGranted())
            startCamera()
        else
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun getIntentData() {
        val args = intent.getBundleExtra("BUNDLE")
        items.clear()
        items = args!!.getSerializable("IDM") as ArrayList<IdsModel>
        ssr = intent.getSerializableExtra("SSR") as SdkSettingResponse
    }

    override fun onStart() {
        super.onStart()

        binding.clickImage.setOnClickListener {
            MyApplication.loaderStart(this)
            takePhoto()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
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
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        val metadata = ImageCapture.Metadata()
        metadata.isReversedHorizontal = true // This method will fix mirror issue

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    try {
                        getBitmapFromUri(savedUri)
                    } catch (e: IOException) {
                        Log.d("", e.printStackTrace().toString())
                    }
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri) {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        showImage(fileDescriptor,uri)
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

    private fun showImage(fileDescriptor: FileDescriptor?, uri: Uri) {
        //Load the Image
        val options = BitmapFactory.Options()
        options.inMutable = true

        val exif = ExifInterface(uri.path!!)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees = exifToDegrees(rotation)

        //create paint object to draw square
        val myBitmap = rotateBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor), rotationInDegrees)
        /*val myBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        val myRectPaint = Paint()
        myRectPaint.strokeWidth = 5f
        myRectPaint.color = Color.RED
        myRectPaint.style = Paint.Style.STROKE

        //create canvas to draw on
        val tempBitmap = Bitmap.createBitmap(myBitmap.width, myBitmap.height, Bitmap.Config.RGB_565)
        val tempCanvas = Canvas(tempBitmap)
        tempCanvas.drawBitmap(myBitmap, 0f, 0f, null)*/

        //create face detector
        val faceDetector = FaceDetector.Builder(
            applicationContext
        ).setTrackingEnabled(false)
            .build()
        if (!faceDetector.isOperational) {
            AlertDialog.Builder(applicationContext)
                .setMessage(getString(R.string.could_not_set_up_the_face_detector)).show()
            return
        }

        //detect faces
        val frame = Frame.Builder().setBitmap(myBitmap).build()
        val faces = faceDetector.detect(frame)
        if (faces.size() == 0) {
            MyApplication.spinnerStop()
            Toast.makeText(this, getString(R.string.no_face_found), Toast.LENGTH_SHORT).show()
        }
        for (i in 0 until faces.size()) {
            /*val thisFace = faces.valueAt(i)
            val x1 = thisFace.position.x
            val y1 = thisFace.position.y
            val x2 = x1 + thisFace.width
            val y2 = y1 + thisFace.height
            tempCanvas.drawRoundRect(RectF(x1, y1, x2, y2), 2f, 2f, myRectPaint)*/
            // use this bitmap only for face image
//            val tb = Bitmap.createBitmap(myBitmap, x1.roundToInt(), y1.roundToInt(), thisFace.width.roundToInt(), thisFace.height.roundToInt())
            showCaptureImage(true)

            Handler(Looper.getMainLooper()).postDelayed({
                //Do something after 100ms
                path = saveImage(myBitmap!!)
                items[items.size - 1].imagePath = path.toString()
                uploadFiles()
            }, 600)


//            MyApplication.spinnerStop()
//            val intent = Intent()
//            intent.putExtra("path", path)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
        }
    }

    private fun saveImage(myBitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
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

    private fun showCaptureImage(status: Boolean) {
        if (status)
            binding.capturedImage.visibility = View.VISIBLE
        else
            binding.capturedImage.visibility = View.GONE
    }

    private fun uploadFiles() {
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

        ApiCall.instance?.uploadFiles(MyApplication.getSharedPrefString("token"), parts, this)
    }

    override fun onSuccess(type: String, data: Any?) {
//        MyApplication.spinnerStop()
        if (type == "UploadFiles") {
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
        }
    }

    override fun onFailure(data: Any?) {
        MyApplication.spinnerStop()
        setResponse("fail", getString(R.string.something_went_wrong))
    }

    private fun setResponse(type: String, error: String) {
        successPageModel = SuccessPageModel()
        when (type) {
            "fail" -> {
                successPageModel.image = R.drawable.fail_gif
                successPageModel.title = getString(R.string.invalid)
                successPageModel.errorMessage = error
                successPageModel.link = ssr?.response?.invalid_redirect_url
            }
            "success" -> {
                successPageModel.image = R.drawable.success_gif
                successPageModel.title = getString(R.string.successful)
                successPageModel.errorMessage = ssr?.response?.success_meaasge
                successPageModel.link = ssr?.response?.success_redirect_url
            }
        }

        if (type == "fail" || items.size - 1 == position) {
            MyApplication.spinnerStop()
            val intent = Intent(this, Successful::class.java)
            intent.putExtra("SuccessPageModel", successPageModel)
            startActivity(intent)
            finish()
        }
    }
}