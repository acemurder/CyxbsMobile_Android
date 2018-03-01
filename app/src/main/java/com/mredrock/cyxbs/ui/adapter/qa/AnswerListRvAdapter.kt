package com.mredrock.cyxbs.ui.adapter.qa

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.AnswerList
import com.mredrock.cyxbs.model.qa.AnswersItem
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.toDate
import com.mredrock.cyxbs.util.extensions.toFormatString
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.item_answer_list.view.*
import org.jetbrains.anko.toast

/**
 * Created By jay68 on 2018/2/22.
 */
class AnswerListRvAdapter(context: Context) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    companion object {
        @JvmStatic
        val TYPE_HEADER = 0
        @JvmStatic
        val TYPE_ANSWER = 1
        @JvmStatic
        val TYPE_EMPTY = 2
    }

    val sortByDefault = context.getString(R.string.answer_list_sort_by_default)!!
    val sortByTime = context.getString(R.string.answer_list_sort_by_time)!!

    var sortOrder: String = sortByDefault
    var answerList: AnswerList? = null
    var onItemClickListener: ((position: Int) -> Unit)? = null

    override fun getItemViewType(position: Int) = when {
        position == 0 -> TYPE_HEADER
        answerList?.hasAnswer() ?: false -> TYPE_ANSWER
        else -> TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> {
            val header = AnswerListHeaderViewWrapper(parent)
            header.onSortOrderChangedListener = { resortAnswer(it) }
            header

        }
        TYPE_ANSWER -> AnswerListViewWrapper(parent)
        else -> AnswerListEmptyViewWrapper(parent)
    }

    override fun getItemCount() = when {
        answerList == null -> 0
        answerList!!.hasAnswer() -> answerList!!.answers!!.size + 1
        else -> 2
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) = when (getItemViewType(position)) {
        TYPE_HEADER -> (holder as AnswerListHeaderViewWrapper).setData(answerList!!)
        TYPE_ANSWER -> {
            (holder as AnswerListViewWrapper).setData(answerList!!.answers!![position - 1])
            holder.itemView.setOnClickListener { onItemClickListener?.invoke(position - 1) }
        }
        else -> (holder as AnswerListEmptyViewWrapper).setData(answerList!!.isSelf)
    }

    fun refreshData(answerList: AnswerList) {
        this.answerList = answerList
        resortAnswer(sortOrder)
    }

    fun resortAnswer(sortOrder: String) {
        //todo 排序
        if (answerList == null) return
        this.sortOrder = sortOrder
        if (sortOrder == sortByTime) {
            answerList!!.sortByTime()
        } else {
            answerList!!.sortByDefault()
        }
        notifyDataSetChanged()
    }

    inner class AnswerListViewWrapper(parent: ViewGroup) : BaseViewHolder<AnswersItem>(parent, R.layout.item_answer_list) {
        override fun setData(data: AnswersItem) {
            super.setData(data)
            itemView.apply {
                ImageLoader.getInstance().loadAvatar(data.photoThumbnailSrc, avatar)
                nickname.text = data.nickname
                initGenderIcon(data)
                content.setText(data.content)
                date.text = data.createdAt.toDate("yyyy-MM-dd HH:mm:ss").toFormatString("yyyy-MM-dd")
                comment.text = data.commentNum
                initFavor(data)
                initAccept(data.isAdopted)
            }
        }

        private fun initFavor(answersItem: AnswersItem) {
            initFavor(answersItem.isPraised, answersItem.praiseNum)
            itemView.favor.setOnClickListener { doFavor(answersItem) }
        }

        private fun initFavor(isPraised: Boolean, praiseNum: String) {
            val resId = if (isPraised) R.drawable.ic_answer_list_favor else R.drawable.ic_answer_list_unfavor
            val drawable = itemView.resources.getDrawable(resId)
            itemView.favor.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            itemView.favor.text = praiseNum
        }

        private fun initGenderIcon(data: AnswersItem) {
            if (answerList!!.shouldShowGenderIcon()) {
                val resId = if (data.isFemale) R.drawable.ic_answer_list_female else R.drawable.ic_answer_list_male
                val gender = itemView.resources.getDrawable(resId)
                itemView.nickname.setCompoundDrawablesWithIntrinsicBounds(null, null, gender, null)
            } else {
                itemView.nickname.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }

        private fun initAccept(isAdopted: Boolean) {
            if (isAdopted) {
                itemView.accepted.visible()
                itemView.accept.gone()
            } else if (!answerList!!.isSelf || answerList!!.hasAdoptedAnswer) {
                itemView.accepted.gone()
                itemView.accept.gone()
            } else {
                itemView.accepted.gone()
                itemView.accept.visible()
            }
        }

        private fun doFavor(answersItem: AnswersItem) {
            val flag = if (answersItem.isPraised) -1 else 1
            initFavor(!answersItem.isPraised, "${answersItem.praiseNumInt + flag}")
            val user = BaseAPP.getUser(context)
            RequestManager.INSTANCE.praiseAnswer(SimpleObserver<Unit>(context, object : SubscriberListener<Unit>(){
                override fun onNext(t: Unit?) {
                    super.onNext(t)
                    answersItem.is_praised = if (answersItem.isPraised) "0" else "1"
                    answersItem.praiseNum = "${answersItem.praiseNumInt + flag}"
                }

                override fun onError(e: Throwable?): Boolean {
                    initFavor(answersItem.isPraised, answersItem.praiseNum)
                    return if (e?.message?.equals("can`t praise yourself") == true) {
                        context.toast("不能赞自己哦~")
                        true
                    } else super.onError(e)
                }
            }), user.stuNum, user.idNum, answersItem.id, answersItem.isPraised)
        }
    }
}