package com.mredrock.cyxbs.util.extensions

import android.view.View

/**
 * Created By jay68 on 2018/2/19.
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setVisible(visible: Boolean, hideState: Int = View.INVISIBLE) {
    visibility = if (visible) View.VISIBLE else hideState
}