package com.supercom.utils.view

import android.view.View
import com.supercom.utils.setAsGone
import com.supercom.utils.setAsVisible

object GeneralViewUtils {

    fun showFirstHideRest(visibleView: View, vararg goneViews: View) {
        visibleView.setAsVisible()
        goneViews.forEach { view ->
            view.setAsGone()
        }
    }

    fun hideFirstShowRest(goneView: View, vararg visibleViews: View) {
        goneView.setAsGone()
        visibleViews.forEach { view ->
            view.setAsVisible()
        }
    }
}