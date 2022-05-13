package com.facekikycverification.utils

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

// TODO: Auto-generated Javadoc
/**
 * The Class TouchEffect will use for touch effect on view
 */
class TouchEffect : OnTouchListener {
    /* (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            v.alpha = 0.6f
        } else if (event.action == MotionEvent.ACTION_UP
                || event.action == MotionEvent.ACTION_CANCEL) {
            v.alpha = 1f
        }
        return false
    }
}