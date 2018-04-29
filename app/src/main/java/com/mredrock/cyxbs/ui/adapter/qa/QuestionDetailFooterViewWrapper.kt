package com.mredrock.cyxbs.ui.adapter.qa

import android.view.View
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.footer_question_detail.view.*

/**
 * Created By jay68 on 2018/4/24.
 */
class QuestionDetailFooterViewWrapper(itemView: View) : BaseViewHolder<Int>(itemView) {
    companion object {
        const val LOADING = 0
        const val LOAD_FAIL = 1
        const val NO_MORE_DATA = 2
        const val LOAD_SUCCESS = 3
    }

    override fun setData(data: Int) = when (data) {
        0 -> {
            itemView.isClickable = false
            itemView.hint.text = "加载中..."
        }

        1 -> {
            itemView.isClickable = true
            itemView.hint.text = "加载错误(＞﹏＜)"
        }

        2 -> {
            itemView.isClickable = false
            itemView.hint.text = "没有更多了~"
        }

        3 -> {
            itemView.isClickable = true
            itemView.hint.text = "加载成功(‾◡◝)"
        }

        else -> Unit
    }
}