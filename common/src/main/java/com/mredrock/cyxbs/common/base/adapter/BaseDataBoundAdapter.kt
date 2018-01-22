package com.acemurder.lightmusic.base.adapter

import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import java.lang.IllegalArgumentException

/**
 * Created by ：AceMurder
 * Created on ：2017/12/6
 * Created for : LightMusic.
 * Enjoy it !!!
 */
abstract class BaseDataBoundAdapter <T : ViewDataBinding> : RecyclerView.Adapter<DataBoundViewHolder<T>>(){
    private val DB_PAYLOAD = Any()
    private var mRecyclerView: RecyclerView? = null


    private val mOnRebindCallback = object : OnRebindCallback<T>() {
        override fun onPreBind(binding: T): Boolean {
            if (mRecyclerView == null || mRecyclerView!!.isComputingLayout) {
                return true
            }
            val childAdapterPosition = mRecyclerView!!.getChildAdapterPosition(binding.root)
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true
            }
            notifyItemChanged(childAdapterPosition, DB_PAYLOAD)
            return false
        }
    }

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<T> {
        val vh : DataBoundViewHolder<T> = DataBoundViewHolder.create(parent, viewType)
        vh.binding.addOnRebindCallback(mOnRebindCallback)
        return vh
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<T>, position: Int,
                                  payloads: List<Any>?) {
        if (payloads!!.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            bindItem(holder, position, payloads)
        }
        holder.binding.executePendingBindings()
    }


    protected abstract fun bindItem(holder: DataBoundViewHolder<T>, position: Int,
                                    payloads: List<Any>)

    private fun hasNonDataBindingInvalidate(payloads: List<Any>): Boolean {
        for (payload in payloads) {
            if (payload !== DB_PAYLOAD) {
                return true
            }
        }
        return false
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<T>, position: Int) {
        throw IllegalArgumentException("just overridden to make final.")
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = null
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayoutId(position)
    }

    @LayoutRes
    abstract fun getItemLayoutId(position: Int): Int
}