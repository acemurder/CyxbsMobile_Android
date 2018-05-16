package com.mredrock.cyxbs.ui.adapter.qa

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.Answer
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.initGender
import kotlinx.android.synthetic.main.header_answer_detail.view.title
import kotlinx.android.synthetic.main.item_answer_detail.view.*

/**
 * Created By jay68 on 2018/3/22.
 */
class AnswerDetailAdapter(context: Activity,
                          tags: String,
                          qid: String,
                          questionTitle: String,
                          commentCount: Int,
                          hasAdoptedAnswer: Boolean,
                          isSelf: Boolean,
                          rv: RecyclerView,
                          answer: Answer,
                          private val shouldShowGenderIcon: Boolean) : BaseHeaderAndFooterViewAdapter<Answer>(context) {

    init {
        val layoutInflater = LayoutInflater.from(context)
        var itemView = layoutInflater.inflate(R.layout.header_answer_detail, rv, false)
        itemView.title.setOnClickListener { context.finish() }
        val header = AnswerDetailHeaderViewWrapper(itemView, tags, qid, questionTitle, commentCount,
                hasAdoptedAnswer, shouldShowGenderIcon, isSelf)
        header.setData(answer)
        headerViewWrapper = header

        itemView = layoutInflater.inflate(R.layout.item_empty_answer_detail, rv, false)
        val empty = AnswerDetailEmptyViewWrapper(itemView)
        empty.setData(isSelf)
        emptyViewWrapper = empty
    }

    override fun onCreateDataViewHolder(parent: ViewGroup) = AnswerDetailViewWrapper(parent)

    inner class AnswerDetailViewWrapper(parent: ViewGroup) : BaseViewHolder<Answer>(parent,
            R.layout.item_answer_detail) {

        override fun setData(data: Answer) {
            super.setData(data)
            itemView.apply {
                ImageLoader.getInstance().loadAvatar(data.photoThumbnailSrc, avatar)
                nickname.text = data.nickname
                nickname.initGender(shouldShowGenderIcon, data.isFemale)
                date.text = data.createdAt.split(" ")[0]
                content.text = data.content
            }
        }
    }
}