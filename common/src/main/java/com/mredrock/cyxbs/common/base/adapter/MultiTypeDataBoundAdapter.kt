package com.acemurder.lightmusic.base.adapter

import android.databinding.ViewDataBinding

/**
 * Created by ：AceMurder
 * Created on ：2017/12/6
 * Created for : LightMusic.
 * Enjoy it !!!
 */
abstract class MultiTypeDataBoundAdapter (private var mItems: MutableList<Any>) : BaseDataBoundAdapter<ViewDataBinding>(){

    override fun bindItem(holder: DataBoundViewHolder<ViewDataBinding>, position: Int, payloads: List<Any>) {

    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): Any {
        return mItems[position]
    }

    fun addItem(item: Any) {
        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    fun addItem(position: Int, item: Any) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }
}