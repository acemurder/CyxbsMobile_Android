package com.mredrock.cyxbs.common.base

import android.databinding.BaseObservable
import android.os.Bundle
import android.support.v4.app.FragmentActivity

/**
 * Created by ：AceMurder
 * Created on ：2017/11/13
 * Created for : LightMusic.
 * Enjoy it !!!
 */
open class BasePresenter(protected var activity: FragmentActivity) : BaseObservable() {
    fun onSaveInstanceState(outState: Bundle?) {}

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {}

}