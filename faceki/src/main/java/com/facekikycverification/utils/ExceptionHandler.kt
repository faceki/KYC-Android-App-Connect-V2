package com.facekikycverification.utils

import android.content.Context
import android.content.Intent
import android.os.Process
import com.facekikycverification.ui.MainActivity
import java.io.PrintWriter
import java.io.StringWriter

class ExceptionHandler
/**
 * Instantiates a new exception handler.
 *
 * @param context the context
 */(
        /**
         * The my context.
         */
        private val myContext: Context) : Thread.UncaughtExceptionHandler {
    /* (non-Javadoc)
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        System.err.println(stackTrace) // You can use LogCat too
        val intent = Intent(myContext, MainActivity::class.java)
        intent.putExtra(CRASH_REPORT, stackTrace.toString())
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        myContext.startActivity(intent)
        Process.killProcess(Process.myPid())
        System.exit(10)
    }

    companion object {
        /**
         * The Constant CRASH_REPORT.
         */
        const val  CRASH_REPORT = "crashReport"
    }
}