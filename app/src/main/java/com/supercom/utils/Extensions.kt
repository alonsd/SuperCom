package com.supercom.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getNowISODateAsString(): String {
    val nowTime = LocalDateTime.now().minusMonths(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}

fun getYesterdayISODateAsString(): String {
    val nowTime = LocalDateTime.now().minusMonths(7).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")).plus("Z")
    return String(nowTime.encodeToByteArray(), Charsets.UTF_8)
}