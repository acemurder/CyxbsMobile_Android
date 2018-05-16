package com.mredrock.cyxbs.util.extensions

import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created By jay68 on 2018/2/28.
 */
fun Activity.doPermissionAction(
        vararg permission: String,
        action: (() -> Unit)? = null,
        doOnRefuse: (() -> Unit)? = null
) {
    val rxPermissions = RxPermissions(this@doPermissionAction)
    if (permission.any { rxPermissions.isGranted(it) }) {
        action?.invoke()
        return
    }
    rxPermissions.request(*permission).subscribe { if (it) action?.invoke() else doOnRefuse?.invoke() }
}