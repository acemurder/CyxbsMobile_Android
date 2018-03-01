package com.mredrock.cyxbs.util.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created By jay68 on 2018/2/23.
 */
fun Date.toFormatString(format: String) = SimpleDateFormat(format, Locale.getDefault()).format(time)

fun String.toDate(format: String) = SimpleDateFormat(format, Locale.getDefault()).parse(this)