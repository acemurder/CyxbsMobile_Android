package com.mredrock.cyxbs.ui.adapter.qa

import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.QuestionDetail
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.*
import kotlinx.android.synthetic.main.header_question_detail.view.*
import kotlinx.android.synthetic.main.popup_window_question_detail_sort_by.view.*
import org.apache.commons.lang3.text.StrBuilder
import org.jetbrains.anko.layoutInflater
import java.util.*

/**
 * Created By jay68 on 2018/2/14.
 */
class QuestionDetailHeaderViewWrapper(itemView: View) : BaseViewHolder<QuestionDetail>(itemView) {
    private val sortOrderContainer: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(sortOrderContainer,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    var onSortOrderChangedListener: ((sortOrder: String) -> Unit)? = null

    override fun setData(questionDetail: QuestionDetail) {
        super.setData(questionDetail)
        itemView.apply {
            title.text = questionDetail.title
            content.text = questionDetail.spannableDescription
            images.setImageUrls(questionDetail.photoUrls, this, singleImage)
            ImageLoader.getInstance().loadAvatar(questionDetail.questionerPhotoThumbnailSrc, avatar)
            nickname.text = questionDetail.questionerNickname
            nickname.initGender(questionDetail.shouldShowGenderIcon(), questionDetail.isFemale)
            disappearTime.text = convertTime(questionDetail.disappearAt)
            reward.text = questionDetail.reward
            answerCount.text = "${questionDetail.answers?.size ?: 0}"
            initSortBy()
        }
    }

    private fun initSortBy() {
        if (itemView.answerCount.text == "0") {
            itemView.sortBy.gone()
        } else {
            itemView.sortBy.visible()
            itemView.sortBy.setOnClickListener { popupWindow.show() }
        }
    }

    private fun convertTime(disappearAt: String): String {
        val disappear = disappearAt.toDate("yyyy-MM-dd HH:mm:ss")
        val minutes = ((disappear.time - Date().time) / 60000).toInt()
        val result = StrBuilder()
        if (minutes / 60 != 0) {
            result.append("${minutes / 60}小时")
        }
        if (minutes % 60 != 0) {
            result.append("${minutes % 60}分钟")
        }
        if (result.isEmpty) {
            result.append("0分钟")
        }
        result.append("后消失")
        return result.build()
    }

    private fun initMenuContentView(): View {
        val root = context.layoutInflater.inflate(R.layout.popup_window_question_detail_sort_by, null, false)
        root.sortByDefault.setOnClickListener {
            val sortOrder = root.sortByDefaultText.text.toString()
            itemView.sortBy.text = sortOrder
            onSortOrderChangedListener?.invoke(sortOrder)
            changeCheckedState(true, root)
            popupWindow.dismiss()
        }
        root.sortByTime.setOnClickListener {
            val sortOrder = root.sortByTimeText.text.toString()
            itemView.sortBy.text = sortOrder
            onSortOrderChangedListener?.invoke(sortOrder)
            changeCheckedState(false, root)
            popupWindow.dismiss()
        }
        root.frame.setOnClickListener { popupWindow.dismiss() }
        return root
    }

    private fun changeCheckedState(checkedDefault: Boolean, root: View) {
        val defaultColor = context.resources.getColor(
                if (checkedDefault) R.color.question_detail_popup_window_sort_by_checked
                else R.color.question_detail_popup_window_sort_by
        )
        val timeColor = context.resources.getColor(
                if (!checkedDefault) R.color.question_detail_popup_window_sort_by_checked
                else R.color.question_detail_popup_window_sort_by
        )
        root.sortByDefaultText.setTextColor(defaultColor)
        root.sortByTimeText.setTextColor(timeColor)
        root.checkedDefault.setVisible(checkedDefault)
        root.checkedTime.setVisible(!checkedDefault)
    }

    private fun PopupWindow.init(): PopupWindow {
        isTouchable = true
        isOutsideTouchable = true
        return this
    }

    private fun PopupWindow.show() {
        changeCheckedState(itemView.sortBy.text == sortOrderContainer.sortByDefaultText.text, sortOrderContainer)
        showAsDropDown(itemView.bottomDivider)
    }
}