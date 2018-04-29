package com.mredrock.cyxbs.network.error

/**
 * Created By jay68 on 2018/4/11.
 */
interface ErrorHandler {
    /**
     * true表示处理了此异常；
     * false表示未处理，异常将往上层传递
     */
    fun handle(e: Throwable?): Boolean
}