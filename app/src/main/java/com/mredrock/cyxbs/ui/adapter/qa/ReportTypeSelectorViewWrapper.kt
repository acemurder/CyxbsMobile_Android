package com.mredrock.cyxbs.ui.adapter.qa

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_report_selector.view.*

/**
 * Created By jay68 on 2018/06/01.
 */
class ReportTypeSelectorViewWrapper(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setValue(value: String) {
        itemView.radioButton.text = value
    }

    fun setChecked(checked: Boolean) {
        itemView.radioButton.isChecked = checked
    }
}