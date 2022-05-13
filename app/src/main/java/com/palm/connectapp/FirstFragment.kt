package com.palm.connectapp

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.faceki.connectapp.R
import com.faceki.connectapp.databinding.FragmentFirstBinding
import com.facekikycverification.startup.FacekiVerification
import com.palm.connectapp.util.FaceKiPreferences
import com.palm.connectapp.util.Utility


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
               var decryptValue =   Utility.decrypt(it.text,"ABCDEF1234567890")

                goToVerification(decryptValue)
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

}