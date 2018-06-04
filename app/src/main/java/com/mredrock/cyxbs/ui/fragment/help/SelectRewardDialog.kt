package com.mredrock.cyxbs.ui.fragment.help

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioGroup
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.util.Utils
import kotlinx.android.synthetic.main.dialog_select_help_reward.*
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * Created by yan on 2018/6/1.
 */
class SelectRewardDialog(context: Context?, themeResId: Int, var reward: Int, val my_discount_balance: Int,  val listener: SelectDialogListener) : Dialog(context, themeResId) {
    private val TAG = "SelectTimeDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_reward)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        group.setCheckedRadioButton(reward.toString())
        tv_my_rewards.text = "积分剩余：${my_discount_balance}"
        tv_selected_rewards.text = "${reward}积分"

        group.setOnCheckedChangeListener({ radioGroup, id ->
            run {
                val select_reward = group.checkedRadioButtonText.toInt()
                if (select_reward > my_discount_balance) {
                    Utils.toast(context, "你的积分不够哦~")
                } else {
                    reward = select_reward
                    tv_selected_rewards.text = "${reward}积分"
                    listener.onChange(reward)
                }
            }
        })

        btn_next.onClick {
            listener.onNext(reward)
            cancel()
        }
        btn_cancel.onClick { cancel() }
    }

}