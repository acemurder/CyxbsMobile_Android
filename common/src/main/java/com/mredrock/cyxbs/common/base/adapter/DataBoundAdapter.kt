package com.acemurder.lightmusic.base.adapter

import android.databinding.ViewDataBinding

/**
 * Created by ：AceMurder
 * Created on ：2017/12/6
 * Created for : LightMusic.
 * Enjoy it !!!
 */
abstract class DataBoundAdapter <T : ViewDataBinding>(private var mLayoutId: Int):BaseDataBoundAdapter<T>(){
    override fun getItemLayoutId(position: Int): Int {
        return mLayoutId
    }
}