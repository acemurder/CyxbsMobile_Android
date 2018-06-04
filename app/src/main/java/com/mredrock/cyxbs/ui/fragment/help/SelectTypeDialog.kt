package com.mredrock.cyxbs.ui.fragment.help

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.help.MyAskActivity
import com.mredrock.cyxbs.ui.activity.help.MyHelpActivity
import com.mredrock.cyxbs.ui.activity.help.PostHelpActivity
import com.mredrock.cyxbs.util.Utils
import kotlinx.android.synthetic.main.dialog_select_help_type.*

class SelectTypeDialog(context: Context?, themeResId: Int) : Dialog(context, themeResId) {
    private val TAG = "SelectTypeDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_type)
        init()
    }

    private fun init() {
        btn_next.setOnClickListener {
            val type: String = when (select_group.checkedRadioButtonId) {
                R.id.btn_help_select_type_learn -> "学习"
                R.id.btn_help_select_type_life -> "生活"
                R.id.btn_help_select_type_emotion -> "情感"
                R.id.btn_help_select_type_others -> "其他"
                else -> "null"
            }
            if(type == "null") Utils.toast(context, "请选择一个类型哦~")
            else {
                this.cancel()
                PostHelpActivity.startActivity(context, type)
                //MyAskActivity.startActivity(context)
            }
        }
        btn_cancel.setOnClickListener { this.cancel() }

    }
}