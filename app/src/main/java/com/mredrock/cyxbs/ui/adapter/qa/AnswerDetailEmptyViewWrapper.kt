package com.mredrock.cyxbs.ui.adapter.qa

import android.view.View
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.util.DensityUtils
import kotlinx.android.synthetic.main.item_empty_answer_detail.view.*

/**
 * Created By jay68 on 2018/4/8.
 */
class AnswerDetailEmptyViewWrapper(itemView: View) : BaseViewHolder<Boolean>(itemView) {
    override fun setData(isSelf: Boolean) {
        super.setData(isSelf)
        val resId = if (isSelf) R.string.question_detail_no_answer_questioner else R.string.question_detail_no_answer_helper
        itemView.text.setText(resId)
        /*val height = DensityUtils.getScreenHeight(itemView.context)
        val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (layoutParams.topMargin == 0) {
            itemView.post {
                layoutParams.topMargin = (height - itemView.top - itemView.height
                        - DensityUtils.dp2px(context, 46 * 2f/*footer高度46dp, toolbar近似46dp*/)) / 2
                itemView.requestLayout()
            }
        }*/
    }
}