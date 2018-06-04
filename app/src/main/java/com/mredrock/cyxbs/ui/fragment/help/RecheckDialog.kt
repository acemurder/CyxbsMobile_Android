package com.mredrock.cyxbs.ui.fragment.help

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mredrock.cyxbs.R
import kotlinx.android.synthetic.main.dialog_recheck_help.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.SimpleDateFormat

class RecheckDialog(context: Context?, themeResId: Int, val time: String, val reward: Int, val listener: SelectDialogListener) : Dialog(context, themeResId) {
    private val TAG = "RecheckDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_recheck_help)
        init()
    }


    @SuppressLint("SetTextI18n")
    private fun init() {
        var formatToData = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val data = formatToData!!.parse(time)
        formatToData = SimpleDateFormat("yyyy年MM月dd日  HH时   mm分")
        val text = formatToData.format(data)

        tv_time.text = text
        tv_reward.text = "${reward}积分"
        btn_next.onClick {
            listener.onNext(reward)
            cancel()
        }
        btn_cancel.onClick { cancel() }
    }

}