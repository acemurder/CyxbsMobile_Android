package com.mredrock.cyxbs.ui.fragment.help

import android.app.Dialog

/**
 * Created by yan on 2018/6/1.
 */
interface DialogListener {
    fun <T> onChange(data:T)
    fun <T> onNext(d: Dialog, data:T)
}