package com.facekikycverification.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.facekikycverification.BuildConfig
import com.facekikycverification.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    //public ImageLoaderConfiguration config;
    //public static ImageLoader loader;
    override fun onCreate() {
        super.onCreate()
        application = this
        ctx = applicationContext
        //        setImageLoaderConfig();
//        loader = ImageLoader.getInstance();
//        loader.init(ImageLoaderConfiguration.createDefault(this));
    }

    companion object {
        private lateinit var dialog: Dialog

        //private static SpotsDialog spotsDialog;
        var HEIGHT = "HEIGHT"
        var WIDTH = "WIDTH"
        var SHARED_PREF_NAME = "HOOT_PREF"
        const val FIRST = "first"
        private const val TOKEN = "token"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val KEYSERVERID = "keyserverid"
        private const val DEVICE_ID = "deviceid"
        private var ctx: Context? = null
        var application: MyApplication? = null
            private set
        var isFacebookLogin = false

        fun spinnerStart(context: Context?) {
            dialog = Dialog(context!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_progress_dialog)

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }

        fun spinnerStop() {
            try {
                if (dialog.isShowing)
                    dialog.dismiss()
            } catch (e: Exception) {

            }
        }

        fun loaderStart(context: Context?) {
            dialog = Dialog(context!!)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_loader_dialog)

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }

        /*fun setLoginData(
            country: LoginResponse,
            context: Context
        ) {
            val settings: SharedPreferences =
                context.getSharedPreferences("LoginData", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = settings.edit()
            val gson = Gson()
            val jsonFavorites = gson.toJson(country)
            editor.putString("LoginData", jsonFavorites)
            editor.apply()
        }

        fun getLoginData(context: Context): LoginResponse? {
            val settings: SharedPreferences =
                context.getSharedPreferences("LoginData", Context.MODE_PRIVATE)
            return if (settings.contains("LoginData")) {
                val jsonObj = settings.getString("LoginData", null)
                val gson = Gson()
                val type = object : TypeToken<LoginResponse>() {

                }.type
                gson.fromJson(jsonObj, type)

            } else
                null
        }*/


        fun isConnectingToInternet(context: Context): Boolean {
            var connected = false
            val connectivity = context
                .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            connected = info != null && info.isConnected && info.isAvailable
            return connected
        }

        fun myEmail(): String? {
            var userEmail = ""
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            userEmail = sp.getString(EMAIL, "").toString()
            return userEmail
        }

        fun myPassword(): String? {
            var userPassword = ""
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            userPassword = sp.getString(PASSWORD, "").toString()
            return userPassword
        }

        fun savePassword(pwd: String?) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            val e = sp.getString(PASSWORD, null)
            if (e != null && e.equals(pwd, ignoreCase = true)) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putString(PASSWORD, pwd)

            // Commit the edits!
            editor.commit()
        }

        fun myDeviceIdFlag(): Boolean {
            var deviceId = false
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            deviceId = sp.getBoolean(KEYSERVERID, false)
            return deviceId
        }

        fun saveDeviceIdFlag(flag: Boolean) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)

            // String e = sp.getString(PASSWORD, null);
            val settoserverflag = sp.getBoolean(KEYSERVERID, false)
            if (settoserverflag == true) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putBoolean(KEYSERVERID, flag)
            editor.commit()
        }

        fun saveCrediential(email: String, password: String) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            val e = sp.getString(EMAIL, null)
            val p = sp.getString(PASSWORD, null)
            if (e != null && e == email && p != null && p == password) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putString(EMAIL, email)
            editor.putString(PASSWORD, password)

            // Commit the edits!
            editor.commit()
        }

        fun myUserId(): String? {
            var userId = ""
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            userId = sp.getString(TOKEN, "").toString()
            return userId
        }

        fun saveUserId(userId: String?) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            val e = sp.getString(TOKEN, null)
            if (e != null && e.equals(userId, ignoreCase = true)) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putString(TOKEN, userId)

            // Commit the edits!
            editor.commit()
        }

        fun myDeviceId(): String? {
            var userId = ""
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            userId = sp.getString(DEVICE_ID, "").toString()
            return userId
        }

        fun saveDeviceId(deviceId: String?) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            val e = sp.getString(DEVICE_ID, null)
            if (e != null && e.equals(DEVICE_ID, ignoreCase = true)) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putString(DEVICE_ID, deviceId)

            // Commit the edits!
            editor.commit()
        }

        fun printValue(text: String?) {
            println(text)
        }

        fun popErrorMsg(
            titleMsg: String?, errorMsg: String?,
            context: Context?
        ) {
            // pop error message
            val builder = AlertDialog.Builder(context)
            builder.setTitle(titleMsg).setMessage(errorMsg)
                .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            val alert = builder.create()
            alert.show()
        }

        fun showMassage(ctx: Context?, msg: String?) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
        }

        const val DISPLAY_MESSAGE_ACTION = "pushnotifications.DISPLAY_MESSAGE"
        const val EXTRA_MESSAGE = "message"
        fun displayMessage(context: Context, message: String?) {
            val intent = Intent(DISPLAY_MESSAGE_ACTION)
            intent.putExtra(EXTRA_MESSAGE, message)
            context.sendBroadcast(intent)
        }

        fun saveDeviceDimentions(height: Int, width: Int) {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            val h = sp.getInt(HEIGHT, 0)
            val w = sp.getInt(WIDTH, 0)
            if (h != 0 && w != 0) {
                // Do not save, data already in preference
                return
            }
            val editor = sp.edit()
            editor.putInt(HEIGHT, height)
            editor.putInt(WIDTH, width)

            // Commit the edits!
            editor.commit()
        }

        fun getSharedPrefLong(preffConstant: String?): Long {
            var longValue: Long = 0
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            longValue = sp.getLong(preffConstant, 0)
            return longValue
        }

        fun setSharedPrefLong(preffConstant: String?, longValue: Long) {
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putLong(preffConstant, longValue)
            editor.commit()
        }

        fun getSharedPrefString(preffConstant: String?): String {
            var stringValue: String
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            stringValue = sp.getString(preffConstant, "").toString()
            return stringValue
        }

        fun setSharedPrefString(
            preffConstant: String?,
            stringValue: String?
        ) {
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putString(preffConstant, stringValue)
            editor.commit()
        }

        fun getSharedPrefInteger(preffConstant: String?): Int {
            var intValue = 0
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            intValue = sp.getInt(preffConstant, 0)
            return intValue
        }

        fun setSharedPrefInteger(preffConstant: String?, value: Int) {
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putInt(preffConstant, value)
            editor.commit()
        }

        fun setSharedPrefBoolean(preffConstant: String, value: Boolean) {

            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            val editor = sp.edit()
            editor.putBoolean(preffConstant, value)

            editor.commit()
        }


        fun getSharedPrefBoolean(preffConstant: String): Boolean {
            var booleanValue = false
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            booleanValue = sp.getBoolean(preffConstant, false)
            return booleanValue
        }


        fun getSharedPrefFloat(preffConstant: String?): Float {
            var floatValue = 0f
            val sp = application!!.getSharedPreferences(
                preffConstant, 0
            )
            floatValue = sp.getFloat(preffConstant, 0f)
            return floatValue
        }

        fun setSharedPrefFloat(preffConstant: String?, floatValue: Float) {
            val sp = application!!.getSharedPreferences(
                preffConstant, 0
            )
            val editor = sp.edit()
            editor.putFloat(preffConstant, floatValue)
            editor.commit()
        }

        fun setCurrentLanguage(preffConstant: String?, value: Boolean) {
            val sharedPreferences = application!!.getSharedPreferences(preffConstant, 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean(preffConstant, value)
            editor.commit()
        }

        fun getCurrentLanguage(preffConstant: String?): Boolean {
            var b = false
            val sharedPreferences = application!!.getSharedPreferences(preffConstant, 0)
            b = sharedPreferences.getBoolean(preffConstant, true)
            return b
        }

        fun calculateWidth(w: Int): Int {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            return w * sp.getInt(WIDTH, 0) / 480
        }

        fun calculateHeight(h: Int): Int {
            val sp = ctx!!.getSharedPreferences(SHARED_PREF_NAME, 0)
            return h * sp.getInt(HEIGHT, 0) / 854
        }

        fun getStatus(name: String?): Boolean {
            val status: Boolean
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            status = sp.getBoolean(name, false)
            return status
        }

        fun setStatus(name: String?, istrue: Boolean) {
            val sp = application!!.getSharedPreferences(
                SHARED_PREF_NAME, 0
            )
            // String e = sp.getString(Constants.STATUS, null);
            val editor = sp.edit()
            editor.putBoolean(name, istrue)
            editor.commit()
        }

        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager = activity
                .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }

        fun getimagebitmap(imagepath: String?): Bitmap? {
            var bitmap = decodeFile(File(imagepath))

            // rotate bitmap
            val matrix = Matrix()
            // matrix.postRotate(MyApplication.getExifOrientation(imagepath));
            // create new rotated bitmap
            bitmap = Bitmap.createBitmap(
                bitmap!!, 0, 0, bitmap.width,
                bitmap.height, matrix, true
            )
            return bitmap
        }

        private fun decodeFile(F: File): Bitmap? {
            try {
                // Decode image size
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(F), null, o)

                // The new size we want to scale to
                val REQUIRED_SIZE = 204

                // Find the correct scale value. It should be the power of 2.
                var scale = 1
                while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE
                ) scale *= 2

                // Decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                return BitmapFactory.decodeStream(FileInputStream(F), null, o2)
            } catch (e: FileNotFoundException) {
            }
            return null
        }

        //    public static DisplayImageOptions OPTIONS_List_Profile = new DisplayImageOptions.Builder()
        //            .showImageForEmptyUri(R.drawable.profile_pic_invite_blank).cacheInMemory(true).showImageOnFail(R.drawable.profile_pic_invite_blank)
        //            .cacheOnDisc(true).considerExifParams(true)
        //            .imageScaleType(ImageScaleType.EXACTLY)
        //            .bitmapConfig(Bitmap.Config.RGB_565).build();
        /*
              public static DisplayImageOptions OPTIONS = new DisplayImageOptions.Builder()
                      .showImageForEmptyUri(0).cacheInMemory(true).showImageOnFail(R.drawable.no_image)
                      .cacheOnDisc(true).considerExifParams(true)
                      .imageScaleType(ImageScaleType.EXACTLY)
                      .bitmapConfig(Bitmap.Config.RGB_565).build();
          */
        /* public static DisplayImageOptions OPTIONS_ListViewPlayer = new DisplayImageOptions.Builder()
                      .showImageForEmptyUri(R.drawable.no_img2).cacheInMemory(true).showImageOnFail(R.drawable.no_img2)
                      .cacheOnDisc(true).considerExifParams(true)
                      .imageScaleType(ImageScaleType.EXACTLY)
                      .bitmapConfig(Bitmap.Config.RGB_565).build();*/
        /*public static DisplayImageOptions OPTIONS_ListView = new DisplayImageOptions.Builder()
                      .showImageForEmptyUri(R.drawable.profle_pic_blank).cacheInMemory(true).showImageOnFail(R.drawable.profle_pic_blank)
                      .cacheOnDisc(true).considerExifParams(true)
                      .imageScaleType(ImageScaleType.EXACTLY)
                      .bitmapConfig(Bitmap.Config.RGB_565).build();*/
        /* public static DisplayImageOptions OPTIONS_teamImg = new DisplayImageOptions.Builder()
                      .showImageForEmptyUri(R.drawable.ic_team).cacheInMemory(true).showImageOnFail(R.drawable.ic_team)
                      .cacheOnDisc(true).considerExifParams(true)
                      .imageScaleType(ImageScaleType.EXACTLY)
                      .bitmapConfig(Bitmap.Config.RGB_565).build();*/
        val displayWidth: Int
            get() {
                val wm = ctx
                    ?.getSystemService(WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width = size.x
                val height = size.y
                return width
            }

        /*public static String timeAgo(long time) {

		long timeToCalculate = System.currentTimeMillis() - time;

		long hours = TimeUnit.MILLISECONDS.toHours(timeToCalculate);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeToCalculate)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(timeToCalculate));
		if (hours > 24) {
						 * days = (int)
						 * (TimeUnit.MILLISECONDS.toHours(timeToCalculate) /
						 * 24); if (days >= 1) // return days + " day ago";
						 * return ""+time; // else { // return days +
						 * " days ago"; // }


			Date createdOn = new Date(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = sdf.format(createdOn);
			return formattedDate;
		} else if (hours == 0 && minutes == 0) {
			return "Just now";
			// } else if (hours == 1 && minutes == 1) {
			// return hours + " hour " + minutes + " min ago";
		} else if (hours == 0 && minutes > 1) {
			return minutes + " min ago";
		}
		return hours + " h " + minutes + " m ago";
	}*/
        fun getAge(year: Int, month: Int, day: Int): String {
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob[year, month] = day
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            val ageInt = age
            return ageInt.toString()
        }

        fun timeAgo(time: Long): String {
            var days = 0
            val timeToCalculate = System.currentTimeMillis() - time
            val hours = TimeUnit.MILLISECONDS.toHours(timeToCalculate)
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(timeToCalculate)
                    - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS
                    .toHours(timeToCalculate)
            ))
            if (hours > 24) {
                days = (TimeUnit.MILLISECONDS.toHours(timeToCalculate) / 24).toInt()
                return if (days == 1) "$days day ago" else {
                    //
                    "$days days ago"
                }


//			Date createdOn = new Date(time);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String formattedDate = sdf.format(createdOn);
//			return formattedDate;
            } else if (hours == 0L && minutes == 0L) {
                return "Just now"
                // } else if (hours == 1 && minutes == 1) {
                // return hours + " hour " + minutes + " min ago";
            } else if (hours == 0L && minutes >= 1) {
                return "$minutes min ago"
            }
            return "$hours h $minutes m ago"
        }

        //String str= dayFormat.format(calendar.getTime());
        val currentDay: String
            get() {
                val dayFormat = SimpleDateFormat("EEEE", Locale.US)
                val calendar = Calendar.getInstance()
                //String str= dayFormat.format(calendar.getTime());
                return dayFormat.format(calendar.time)
            }
        val currentDayForWeightLoss: String
            get() {
                val dayFormat = SimpleDateFormat("EEEE,dd MMM yy", Locale.US)
                val calendar = Calendar.getInstance()
                return dayFormat.format(calendar.time)
            }

        fun getTimeSpan(timeStr: Long): String {
            return DateUtils.getRelativeTimeSpanString(
                timeStr,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
            ).toString()
                .replace(" ago", " ago").replace("in ", "").replace(" seconds", " s")
                .replace(" minutes", " m").replace(" minute", " m").replace(" hours", " h")
                .replace(" hour", " h")
                .replace(" days", " d")
                .replace(" day", " d")
        }

        fun getDestance(
            Lat_source: Double, Long_Source: Double,
            Lat_destination: String, Long_destination: String
        ): Double {
            try {
                var distance: Double
                val latB = Lat_destination.toDouble()
                val lngB = Long_destination.toDouble()
                val locationA = Location("point A")
                locationA.latitude = Lat_source
                locationA.longitude = Long_Source
                val locationB = Location("point B")
                locationB.latitude = latB
                locationB.longitude = lngB
                distance = locationA.distanceTo(locationB).toDouble()
                println("distance you got is?????$distance")
                distance = distance / 1000
                return distance
            } catch (e: NumberFormatException) {
                Log.d("catch MyApplication", "" + e)
                e.printStackTrace()
            }
            return 0.0
        }

        fun dateCurrent(): String {
            val c = Calendar.getInstance()
            // c.add(Calendar.DATE, 1);
            println("Current time => " + c.time)
            val df = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            return df.format(c.time)
        }

        fun dateToTimeMills(strDate: String?): Long {
            // String str_date="13-Nov-2011";
            val formatter: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
            try {
                val date = formatter.parse(strDate) as Date
                println("Today is " + date.time)
                val str = date.time
                Log.e("", "" + str)
                return date.time
            } catch (e: Exception) {
            }
            return 0
        }

        fun dateToTimeMills2(strDate: String?): Long {
            // String str_date="13-Nov-2011";
            val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val date = formatter.parse(strDate) as Date
                println("Today is " + date.time)
                val str = date.time
                Log.e("", "" + str)
                return date.time
            } catch (e: Exception) {
            }
            return 0
        }

        fun isValidLatLng(lat: Double, lng: Double): Boolean {
            if (lat < -90 || lat > 90) {
                return false
            } else if (lng < -180 || lng > 180) {
                return false
            }
            return true
        }

        fun dateToUTC(dtStart: String?): String {
            // String dtStart = "04-14-2016 03:27:18";
            var date: Date? = null
            //  SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            val format = SimpleDateFormat("dd-MMM-yyyy")
            try {
                date = format.parse(dtStart)
                println("date11111>>>>$date")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            ////
            // Date myDate = new Date();

            //Log.e("myDate>>", "" + myDate);
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            calendar.time = date
            val time = calendar.time
            Log.e("time>>", "" + time)
            //  SimpleDateFormat outputFmt = new SimpleDateFormat("MMM dd, yyy h:mm a zz");
            val outputFmt = SimpleDateFormat("EEE, dd MMM yyyy")
            val dateAsString = outputFmt.format(time)
            println("UTC>>>$dateAsString") //Apr 16, 2016 2:20 PM GMT+05:30
            return dateAsString
        }

        ///**
        fun getDateOrTimeFromMillis(x: String): String {
            val formatter: DateFormat = SimpleDateFormat("dd/MM/yy - hh:mm a")
            val milliSeconds: Long
            milliSeconds = try {
                x.toLong()
            } catch (e: Exception) {
                x.replace(".", "").toLong()
            }
            println(milliSeconds)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = System.currentTimeMillis()
            val s = formatter.format(calendar.time).split("-".toRegex()).toTypedArray()[0]
            val s1 = formatter.format(calendar2.time).split("-".toRegex()).toTypedArray()[0]
            return if (s == s1) {
                formatter.format(calendar.time).split("-".toRegex()).toTypedArray()[1]
            } else {
                formatter.format(calendar.time).split("-".toRegex()).toTypedArray()[0]
            }
        }

        fun getStrings(a: Array<DoubleArray?>): Array<Array<String>?> {
            val output: Array<Array<String>?> = arrayOfNulls(a.size)
            var i = 0
            for (d in a) {
                output[i++] =
                    Arrays.toString(d).replace("[", "").replace("]", "").split(",".toRegex())
                        .toTypedArray()
            }
            return output
        }

        fun getRealPathFromURI(context: Activity, contentUri: Uri?): String? {
            val filePathColumn = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.ORIENTATION
            )
            if (BuildConfig.DEBUG && contentUri == null) {
                error("Assertion failed")
            }
            val cursor = contentUri?.let {
                context.contentResolver.query(
                    it,
                    filePathColumn, null, null, null
                )
            }
            cursor?.let {
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                return cursor.getString(columnIndex)
            }

            return ""
        }
    }
}