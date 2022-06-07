package com.palm.connectapp

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.faceki.connectapp.R
import com.faceki.connectapp.databinding.FragmentFirstBinding
import com.facekikycverification.startup.FacekiVerification
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.palm.connectapp.util.FaceKiPreferences
import com.palm.connectapp.util.Utility
import java.io.FileNotFoundException
import java.util.*
import android.os.AsyncTask
import android.app.ProgressDialog
import android.graphics.Matrix
import android.util.Log
import android.util.Log.INFO
import com.google.zxing.BarcodeFormat

import com.google.zxing.DecodeHintType

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var codeScanner: CodeScanner? = null
    private var statusBarIsVisible : Boolean = false;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnReset.setOnClickListener{
            reset()
        }
        binding.btnGallery.setOnClickListener{
            openGallery()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.statusBarColor = requireView().context!!.getColor(R.color.white)
        } // set status background white
        if (statusBarIsVisible)
        {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//  set status text dark
        }else{
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN;//  set status text dark
        }
        callFaceKiVerification()

    }
    private val checkPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all {
            it.value == true
        }
        if (granted) {
            openGallery()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

//        (activity as AppCompatActivity).supportActionBar?.title = "FACEKI Connect"
        codeScanner?.let {
            codeScanner!!.startPreview()
        }

    }

    override fun onPause() {
        codeScanner?.let {
            codeScanner!!.releaseResources()
        }
        super.onPause()

    }

    /**
     * reset data
     * @return alert
     */
    private fun reset(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to reset all data?")
        builder.setPositiveButton("Yes"){dialogInterface,which->
            FaceKiPreferences.resetPreference(requireActivity())
        }

        builder.setNegativeButton("Cancel"){dialogInterface,which->

        }

        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    /**
     * call faceki based on qrscan or by shared preference
     * @return void
     */
    private fun callFaceKiVerification(){
        val emailid = FaceKiPreferences.getEmailId(requireActivity())

        if(emailid != null && emailid.length != 0){
            val value = FaceKiPreferences.getClientId(requireActivity())  +"|" + FaceKiPreferences.getKeyName(requireActivity())+"|"+ emailid
            goToVerification(value)
        }
//        else
//          {
        startScan()
//          }
    }

    /**
     * start scanner
     * @return void
     */
    private fun startScan(){

        codeScanner = CodeScanner(requireActivity().applicationContext,binding.scannerView )
        //default values
        codeScanner!!.camera = CodeScanner.CAMERA_BACK
        codeScanner!!.formats = CodeScanner.ALL_FORMATS
        codeScanner!!.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner!!.scanMode = ScanMode.SINGLE
        codeScanner!!.isAutoFocusEnabled = true
        codeScanner!!.isFlashEnabled = false

        //callbacks
        codeScanner!!.decodeCallback = DecodeCallback {
            activity?.runOnUiThread{
                try{
                    var decryptValue =   Utility.decrypt(it.text,"ABCDEF1234567890")
                    goToVerification(decryptValue)
                }
                catch (e: Exception){
                    e.printStackTrace()
                    Toast.makeText(
                        requireActivity(),
                        "Invalid QR code",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

        codeScanner!!.errorCallback  = ErrorCallback {
            activity?.runOnUiThread{
//                Toast.makeText(activity,"QR error:${it.message}",Toast.LENGTH_LONG).show()

            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner!!.startPreview()
            statusBarIsVisible = !statusBarIsVisible;
            if (statusBarIsVisible)
            {
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//  set status text dark
            }else{
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN;//  set status text dark
            }
        }



    }

    /***
     * Go to facekiverification library
     * @param decryptValue - decrypt value get from encrypted text from QR
     * @return void
     */
    private fun goToVerification(decryptValue:String?){
        if(decryptValue != null){
            var values = decryptValue.split('|')
            if(values[1] == "$"+"fki$" && values[1].length == 5) {

                FaceKiPreferences.saveClientId(values[0],requireActivity())
                FaceKiPreferences.saveKeyName(values[1],requireActivity())
                FaceKiPreferences.saveEmail(values[2],requireActivity())

                FacekiVerification.initiateSMSDK(requireActivity(),values[0],values[1])
            }
            else{
                Toast.makeText(requireActivity(),"Invalid QR code",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(activity,"Not able to get value",Toast.LENGTH_LONG).show()
        }
    }

    /**
     * go to second fragment of setting
     * @param void
     */
    fun goToSetting(){
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }


    /***
     * Open Gallery
     */
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data

            val uri = data?.data

            if (uri != null) {
                val mainLooper = Looper.getMainLooper()

                binding.progressBarCyclic.visibility=View.VISIBLE

                Thread(Runnable {
                    val bitmap = uriToBitmap(uri)

                    try {
                        var  ratio : Float = 1.0F
                        if( bitmap.width > 0){
                            ratio = (bitmap.height.toFloat()/bitmap.width.toFloat())
                        }
//
                        val resizeBitmap =   Bitmap.createScaledBitmap(bitmap, 300, (ratio*300).toInt(), false)

                        val width = resizeBitmap.width
                        val height = resizeBitmap.height
                        val pixels = IntArray(width * height)
                        resizeBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                        resizeBitmap.recycle()
                        bitmap.recycle()

                        val source = RGBLuminanceSource(width, height, pixels)
                        val bBitmap = BinaryBitmap(HybridBinarizer(source))
                        val reader = MultiFormatReader()
                        val tmpHintsMap: MutableMap<DecodeHintType, Any> = EnumMap(
                            DecodeHintType::class.java
                        )
                        tmpHintsMap[DecodeHintType.TRY_HARDER] = java.lang.Boolean.TRUE
                        tmpHintsMap[DecodeHintType.POSSIBLE_FORMATS] =
                            EnumSet.allOf(BarcodeFormat::class.java)

                        try {
                            val result: Result = reader.decode(bBitmap,tmpHintsMap)
                            Handler(mainLooper).post {
                                requireActivity().runOnUiThread {
                                    binding.progressBarCyclic.visibility=View.GONE
                                    try {
                                        val decryptValue =   Utility.decrypt(result.getText(),"ABCDEF1234567890")
                                        goToVerification(decryptValue)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(
                                            requireActivity(),
                                            "Invalid QR code",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()

                            Handler(mainLooper).post {
                                requireActivity().runOnUiThread {
                                    binding.progressBarCyclic.visibility=View.GONE
                                    Toast.makeText(requireActivity(),"Invalid QR code",Toast.LENGTH_LONG).show()
                                }
                            }

                            //e.printStackTrace()
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()

                        Handler(mainLooper).post {
                            requireActivity().runOnUiThread {
                                binding.progressBarCyclic.visibility=View.GONE
                                Toast.makeText(requireActivity(),"Invalid QR code",Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }).start()
            }

        }
    }


    private fun openGallery(){

        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        if(hasPermissions(requireContext(), permissions =PERMISSIONS ))     {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

//            intent.putExtra("crop", "true")
//            intent.putExtra("scale", true)
//            intent.putExtra("aspectX", 16)
//            intent.putExtra("aspectY", 9)
            resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))



        } else {

            checkPermission.launch(PERMISSIONS)
        }
    }
    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    // util method
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun uriToBitmap(uri: Uri): Bitmap {
        if(Build.VERSION.SDK_INT < 28) {
            return MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
            return ImageDecoder.decodeBitmap(
                source,
                ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                })
        }
    }


}