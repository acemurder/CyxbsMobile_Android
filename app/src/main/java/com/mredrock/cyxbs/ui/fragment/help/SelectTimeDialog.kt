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


class SelectTimeDialog(context: Context?, themeResId: Int, val listener: OnTimeChangeListener) : Dialog(context, themeResId) {
    private val TAG = "SelectTimeDialog"
    private var formatToData: SimpleDateFormat? = null
    var time: String = " "

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
        datePickerView.selectedDate = Calendar.getInstance()
        datePickerView.setOnSelectedDateChangedListener{
            date ->
            run {
                val year = date.get(Calendar.YEAR)
//                val month = date.get(Calendar.MONTH)
//                val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)
//                val hour = date.get(Calendar.HOUR_OF_DAY)
//                val minute = date.get(Calendar.MINUTE)
                time = formatToData?.format(date.time).toString()
                listener.onChange(time)

            }
        }
    }

    interface OnTimeChangeListener {
        fun onChange(time: String)
        fun onNext(time: String)
    }
}