package com.mredrock.cyxbs.network.error

import com.mredrock.cyxbs.BaseAPP
import org.jetbrains.anko.toast

/**
 * Created By jay68 on 2018/4/11.
 */
object QAErrorHandler : ErrorHandler {
    override fun handle(e: Throwable?) = run {
        BaseAPP.getContext().toast("failed: " + e?.message)
        false
    }
}