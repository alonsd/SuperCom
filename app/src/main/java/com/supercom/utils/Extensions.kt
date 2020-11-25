package com.supercom.utils

import android.text.format.DateUtils
import android.view.View
import android.widget.DatePicker
import com.supercom.utils.Constants.Date.YEAR_IN_MILLIS
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*





fun getAnyDateAsISODate2(year : Int, month : Int, day : Int) : String {
    val dateToSubtractInMillis = DateUtils.WEEK_IN_MILLIS
    val now = Instant.now()
//    now.minusMillis(YEAR_IN_MILLIS * year).minusMillis(DateUtils.)


    val nowTime = LocalDateTime.of(year, month, day, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}



fun getCurrentISODateAsString(): String {
    val nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}

fun getYesterdayISODateAsString(): String {
    val nowTime = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}

fun getAnyDateAsISODate(year : Int, month : Int, day : Int) : String {
    val nowTime = LocalDateTime.of(year, month, day, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}

fun View.setAsVisible() {
    visibility = View.VISIBLE
}

fun View.setAsGone() {
    visibility = View.GONE
}