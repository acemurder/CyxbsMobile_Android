package com.mredrock.cyxbs.ui.adapter.qa

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder

/**
 * Created By jay68 on 2018/3/20.
 */
abstract class BaseHeaderAndFooterViewAdapter<T>(private val context: Context) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_FOOTER = 1
        const val TYPE_DATA = 2
        const val TYPE_EMPTY = 3
    }

    protected val dataList = ArrayList<T>()

    var headerViewWrapper: BaseViewHolder<*>? = null
    var footerViewWrapper: BaseViewHolder<*>? = null
    var emptyViewWrapper: BaseViewHolder<*>? = null

    open fun refreshData(dataSet: Collection<T>) {
        this.dataList.clear()
        this.dataList.addAll(dataSet)
        notifyDataSetChanged()
    }

    open fun addData(dataSet: Collection<T>) {
        this.dataList.addAll(dataSet)
        notifyItemInserted(itemCount - 1)
    }

    override fun getItemViewType(position: Int) = when {
        headerViewWrapper != null && position == 0 -> TYPE_HEADER
        emptyViewWrapper != null && dataList.size == 0 -> TYPE_EMPTY
        footerViewWrapper != null && position == itemCount - 1 -> TYPE_FOOTER
        else -> TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> headerViewWrapper
        TYPE_FOOTER -> footerViewWrapper
        TYPE_EMPTY -> emptyViewWrapper
        else -> onCreateDataViewHolder(parent)
    }

    override fun getItemCount(): Int {
        var count = dataList.size
        if (headerViewWrapper != null) ++count
        if (footerViewWrapper != null && dataList.size != 0) ++count
        if (emptyViewWrapper != null && dataList.size == 0) ++count
        return count
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) = when (getItemViewType(position)) {
        TYPE_FOOTER -> {
            onLoadMoreData(footerViewWrapper!!)
            holder.itemView.setOnClickListener { onLoadMoreData(footerViewWrapper!!) }
        }
        TYPE_DATA -> {
            (holder as BaseViewHolder<T>).setData(dataList[getDataSetPosition(position)])
            holder.itemView.setOnClickListener { onItemClickListener(holder, getDataSetPosition(position)) }
        }
        else -> Unit
    }

    private fun getDataSetPosition(position: Int) = if (headerViewWrapper == null) position else position - 1

    abstract fun onCreateDataViewHolder(parent: ViewGroup): BaseViewHolder<T>
    open fun onLoadMoreData(footer: BaseViewHolder<*>) = Unit
    open fun onItemClickListener(holder: BaseViewHolder<T>, position: Int) = Unit
}