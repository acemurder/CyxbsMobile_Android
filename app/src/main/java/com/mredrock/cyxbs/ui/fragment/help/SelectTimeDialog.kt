package com.mredrock.cyxbs.ui.fragment.help

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.util.DialogUtil
import kotlinx.android.synthetic.main.dialog_select_help_time.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import top.defaults.view.DateTimePickerView
import java.text.SimpleDateFormat
import java.util.*


class SelectTimeDialog(context: Context?, themeResId: Int, var time : String = "", val listener: SelectDialogListener) : Dialog(context, themeResId) {
    private val TAG = "SelectTimeDialog"
    private var formatToData: SimpleDateFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_time)
        init()
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
        formatToData = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        btn_cancel.onClick { cancel() }
        btn_next.onClick {
            listener.onNext(time)
            cancel()
        }

        datePickerView.setStartDate(Calendar.getInstance())

        if (time != "" && !time.isEmpty()) {
            val data = formatToData!!.parse(time)
            val calendar = Calendar.getInstance()
            calendar.time = data
            datePickerView.selectedDate = calendar

        } else {
            time = formatToData!!.format(Calendar.getInstance().time).toString()
            datePickerView.selectedDate = Calendar.getInstance()
        }

        datePickerView.setOnSelectedDateChangedListener{
            date ->
            run {
                time = formatToData?.format(date.time).toString()
                listener.onChange(time)
            }
        }
    }

}