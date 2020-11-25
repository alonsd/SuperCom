package com.supercom.utils

import android.text.format.DateUtils

object Constants {

    object Api {
        const val BASE_URL = "https://api.covid19api.com/"
    }

    object RequestCodes {

        const val LOCATION_REQUEST_CODE = 10
    }

    object Date {

        const val YEAR_IN_MILLIS = DateUtils.WEEK_IN_MILLIS * 7
    }
}