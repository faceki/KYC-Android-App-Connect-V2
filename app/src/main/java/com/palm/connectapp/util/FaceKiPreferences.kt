package com.palm.connectapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity

class FaceKiPreferences {
    companion object {
        /**
         * get shared preference instance for faceki app
         * @param activity : activity context
         * @return sharedpreference : instance of shared preference
         */
        fun getPreference(activity: FragmentActivity) : SharedPreferences {
            val sharedPreferences: SharedPreferences = activity.getSharedPreferences("FaceKI",
                Context.MODE_PRIVATE)

            return sharedPreferences
        }

        /**
         * save email received from scanning bar code
         * @param emailId - email id to save
         * @param activity : activity context
         * @return save email id in preference
         *
         */
        fun saveEmail(emailId:String,activity: FragmentActivity) {
            val editor =  getPreference(activity).edit()
            editor.putString("emailid",emailId)
editor.commit()
        }

        /**
         * get email id from preference
         * @param activity : activity context
         * @return email id from preference
         */
        fun getEmailId(activity: FragmentActivity):String{
            val preferences = getPreference(activity)
            val emailid = preferences.getString("emailid","") as String
            return emailid
        }

        /**
         * save clientid received from scanning bar code
         * @param clientId - client id to save
         * @param activity : activity context
         * @return save client id in preference
         *
         */
        fun saveClientId(clientId:String,activity: FragmentActivity) {
            val editor =  getPreference(activity).edit()
            editor.putString("clientid",clientId)
editor.commit()
        }


        /**
         * get client id from preference
         * @param activity : activity context
         * @return client id from preference
         */
        fun getClientId(activity: FragmentActivity):String{
            val preferences = getPreference(activity)
            val clientid = preferences.getString("clientid","") as String
            return clientid
        }

        /**
         * save clientid received from scanning bar code
         * @param keyname - keyname to save
         * @param activity : activity context
         * @return save keyname in preference
         *
         */
        fun saveKeyName(keyname:String,activity: FragmentActivity) {
            val editor =  getPreference(activity).edit()
            editor.putString("keyname",keyname)
            editor.commit()
        }


        /**
         * get keyname from preference
         * @param activity : activity context
         * @return keyname from preference
         */
        fun getKeyName(activity: FragmentActivity):String{
            val preferences = getPreference(activity)
            val keyname = preferences.getString("keyname","") as String
            return keyname
        }

        /**
         * reset shared prefernce of faceki
         * @param activity : Fragment activity instance
         * @return void
         */
        fun resetPreference(activity: FragmentActivity){
            val editor = getPreference(activity).edit()
            editor.clear()
            editor.remove("emailid")
            editor.remove("clientid")
             editor.commit()
        }


    }
}