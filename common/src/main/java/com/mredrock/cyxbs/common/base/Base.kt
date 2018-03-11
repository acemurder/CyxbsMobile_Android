package com.mredrock.cyxbs.common.base

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by ：AceMurder
 * Created on ：2017/11/13
 * Created for : LightMusic.
 * Enjoy it !!!
 */


interface IView {

    fun getLayoutRes(): Int

    fun viewCreated(savedInstanceState: Bundle?)

    fun <P : BasePresenter> createPresenter(): P?

    fun createContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
}

interface IToolbar {

    fun hideToolbar()

    fun showToolbar()

    fun setTitle(resId: Int)

    fun setTitle(title: String)
}

open class EmptyPresenter (activity: FragmentActivity): BasePresenter(activity)




