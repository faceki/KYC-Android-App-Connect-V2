package com.facekikycverification.network

/**
 * Interface is used for common purpose in Application.
 *
 * @author and15031989
 */
interface IApiCallback {
    /**
     * Method for getting the type and data.
     *
     * @param data Actual data
     */
    fun onSuccess( type:String,data: Any?)

    /**
     * Failure Reason
     * @param data
     */
    fun onFailure(data: Any?)
}