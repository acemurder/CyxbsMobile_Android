package com.mredrock.cyxbs.ui.fragment.help

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioGroup
import com.mredrock.cyxbs.R
import kotlinx.android.synthetic.main.dialog_select_help_reward.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * Created by yan on 2018/6/1.
 */
class SelectRewardDialog(context: Context?, themeResId: Int, val listener: OnRewardChangeListener) : Dialog(context, themeResId) {
    private val TAG = "SelectTimeDialog"
    var reward = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_reward)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        group.setOnCheckedChangeListener({ radioGroup, id ->
            run {
                reward = when (id) {
                    R.id.btn_reward_1 -> 1
                    R.id.btn_reward_2 -> 2
                    R.id.btn_reward_3 -> 3
                    R.id.btn_reward_5 -> 4
                    R.id.btn_reward_10 -> 10
                    R.id.btn_reward_15 -> 15
                    else -> -1
                }
                tv_selected_rewards.text = "${reward}积分"
                listener.onChange(reward)
            }
        })

        btn_next.onClick { listener.onNext() }
    }

    interface OnRewardChangeListener {
        fun onChange(reward : Int)
        fun onNext()
    }
}