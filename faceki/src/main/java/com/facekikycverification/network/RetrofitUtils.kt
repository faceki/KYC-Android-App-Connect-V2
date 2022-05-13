package com.facekikycverification.network

import android.app.Activity
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.facekikycverification.BuildConfig
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object RetrofitUtils {
    const val MEDIA_TYPE_MULTIPART_FORM_DATA_VALUE = "multipart/form-data"
    val MEDIA_TYPE_MULTIPART_FORM_DATA =
        MediaType.parse(MEDIA_TYPE_MULTIPART_FORM_DATA_VALUE)
    const val MEDIA_TYPE_doc_VALUE = "application/doc"
    val MEDIA_TYPE_DOC =
        MediaType.parse(MEDIA_TYPE_doc_VALUE)
    private const val MEDIA_TYPE_ALL_VALUE = "image/*"
    val MEDIA_TYPE_IMAGE_ALL =
        MediaType.parse(MEDIA_TYPE_ALL_VALUE)
    private const val MEDIA_TYPE_PNG_VALUE = "image/png"
    val MEDIA_TYPE_IMAGE_PNG =
        MediaType.parse(MEDIA_TYPE_PNG_VALUE)
    private const val MEDIA_TYPE_JPG_VALUE = "image/jpg"
    val MEDIA_TYPE_IMAGE_JPG =
        MediaType.parse(MEDIA_TYPE_JPG_VALUE)
    private const val MEDIA_TYPE_JPEG_VALUE = "image/jpeg"
    val MEDIA_TYPE_IMAGE_JPEG =
        MediaType.parse(MEDIA_TYPE_JPEG_VALUE)
    private const val MEDIA_TYPE_VIDEO_ALL_VALUE = "video/*"
    val MEDIA_TYPE_VIDEO_ALL =
        MediaType.parse(MEDIA_TYPE_VIDEO_ALL_VALUE)
    private const val MEDIA_TYPE_TEXT_PLAIN_VALUE = "text/plain"
    val MEDIA_TYPE_TEXT_PLAIN =
        MediaType.parse(MEDIA_TYPE_TEXT_PLAIN_VALUE)
    private const val MEDIA_TYPE_UNKNOWN_VALUE = "application/octet-stream"
    val MEDIA_TYPE_UNKNOWN =
        MediaType.parse(MEDIA_TYPE_UNKNOWN_VALUE)
    private const val MEDIA_TYPE_pdf_VALUE = "application/pdf"
    val MEDIA_TYPE_PDF =
        MediaType.parse(MEDIA_TYPE_pdf_VALUE)

    fun createPartFromString(descriptionString: String?): RequestBody {
        return RequestBody.create(MEDIA_TYPE_MULTIPART_FORM_DATA, descriptionString)
    }

    fun createFilePart(
        variableName: String?,
        filePath: String?,
        mediaType: MediaType?
    ): MultipartBody.Part {
        val file = File(filePath)

        // create RequestBody instance from file
        val requestFile = RequestBody.create(mediaType, file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(variableName, file.name, requestFile)
    }

    fun createMultipartRequest(requestValuePairsMap: HashMap<String, Any>): HashMap<String, RequestBody> {
        val requestMap =
            HashMap<String, RequestBody>()
        val iterator: Iterator<*> = requestValuePairsMap.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next() as String
            val value = requestValuePairsMap[key]
            requestMap[key] = createPartFromString(value as String?)
        }
        return requestMap
    }

    fun getRealPathFromURI(context: Activity, contentUri: Uri?): String? {

        val filePathColumn = arrayOf(
            MediaStore.Images.Media.DATA,
//            MediaStore.Images.Media.ORIENTATION
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
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        }
        cursor!!.close()
        return ""
    }

    fun getPDFPath(context: Activity,uri: Uri?): String? {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
        )
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.contentResolver.query(contentUri, projection, null, null, null)!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

}