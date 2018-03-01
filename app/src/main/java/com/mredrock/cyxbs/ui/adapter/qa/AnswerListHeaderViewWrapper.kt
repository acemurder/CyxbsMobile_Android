package com.mredrock.cyxbs.ui.adapter.qa

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.AnswerList
import com.mredrock.cyxbs.model.social.Image
import com.mredrock.cyxbs.ui.activity.social.ImageActivity
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.setVisible
import com.mredrock.cyxbs.util.extensions.toDate
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.header_answer_list.view.*
import kotlinx.android.synthetic.main.popup_window_answer_list_sort_by.view.*
import org.apache.commons.lang3.text.StrBuilder
import org.jetbrains.anko.layoutInflater
import java.util.*

/**
 * Created By jay68 on 2018/2/14.
 */
class AnswerListHeaderViewWrapper(parent: ViewGroup) : BaseViewHolder<AnswerList>(parent, R.layout.header_answer_list) {
    private val sortOrderContainer: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(sortOrderContainer,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    var onSortOrderChangedListener: ((sortOrder: String) -> Unit)? = null

    override fun setData(answerList: AnswerList) {
        super.setData(answerList)
        itemView.apply {
            title.text = answerList.title
            content.text = answerList.description
            loadImage(answerList.photoUrls)
            ImageLoader.getInstance().loadAvatar(answerList.questionerPhotoThumbnailSrc, avatar)
            nickname.text = answerList.questionerNickname
            initGenderIcon(answerList)
            disappearTime.text = convertTime(answerList.disappearAt)
            reward.text = answerList.reward
            answerCount.text = "${answerList.answers?.size ?: 0}"
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

    private fun loadImage(url: List<String>?) {
        if (url.isEmpty()) return
        if (url!!.size == 1) {
            itemView.images.gone()
            itemView.singleImage.visible()
            itemView.setOnClickListener { ImageActivity.startWithData(itemView.context, url[0]) }
            ImageLoader.getInstance().loadOffcialImg(url[0], itemView.singleImage, itemView)
        } else {
            itemView.singleImage.gone()
            itemView.images.visible()
            val imageList = List(url.size) { Image(url[it], Image.TYPE_ADD) }
            itemView.images.setImagesData(imageList)
            itemView.images.setOnAddImagItemClickListener { _, position ->
                ImageActivity.startWithData(itemView.context, url[position])
            }
        }
    }

    private fun initGenderIcon(answerList: AnswerList) {
        if (answerList.shouldShowGenderIcon()) {
            val resId = if (answerList.isFemale) R.drawable.ic_answer_list_female else R.drawable.ic_answer_list_male
            val gender = itemView.resources.getDrawable(resId)
            itemView.nickname.setCompoundDrawablesWithIntrinsicBounds(null, null, gender, null)
        } else {
            itemView.nickname.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
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

    private fun List<*>?.isEmpty() = (this == null || isEmpty())

    private fun initMenuContentView(): View {
        val root = context.layoutInflater.inflate(R.layout.popup_window_answer_list_sort_by, null, false)
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
                if (checkedDefault) R.color.answer_list_popup_window_sort_by_checked
                else R.color.answer_list_popup_window_sort_by
        )
        val timeColor = context.resources.getColor(
                if (!checkedDefault) R.color.answer_list_popup_window_sort_by_checked
                else R.color.answer_list_popup_window_sort_by
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