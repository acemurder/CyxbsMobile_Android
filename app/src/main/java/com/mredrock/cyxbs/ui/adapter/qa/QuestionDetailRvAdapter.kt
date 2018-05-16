package com.mredrock.cyxbs.ui.adapter.qa

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.Answer
import com.mredrock.cyxbs.model.qa.QuestionDetail
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.qa.AnswerDetailActivity
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.LogUtils
import com.mredrock.cyxbs.util.extensions.*
import kotlinx.android.synthetic.main.item_question_detail.view.*

/**
 * Created By jay68 on 2018/2/22.
 */
class QuestionDetailRvAdapter(val context: Context, rv: RecyclerView) : BaseHeaderAndFooterViewAdapter<Answer>(context) {
    val sortByDefault = context.getString(R.string.question_detail_sort_by_default)!!
    val sortByTime = context.getString(R.string.question_detail_sort_by_time)!!

    var sortOrder: String = sortByDefault
    var questionDetail: QuestionDetail? = null
    var nextPage = 2

    init {
        val layoutInflater = LayoutInflater.from(context)
        var itemView = layoutInflater.inflate(R.layout.header_question_detail, rv, false)
        val header = QuestionDetailHeaderViewWrapper(itemView)
        header.onSortOrderChangedListener = { resortData(it) }
        headerViewWrapper = header

        itemView = layoutInflater.inflate(R.layout.item_empty_answer_detail, rv, false)
        emptyViewWrapper = AnswerDetailEmptyViewWrapper(itemView)

        itemView = layoutInflater.inflate(R.layout.footer_question_detail, rv, false)
        footerViewWrapper = QuestionDetailFooterViewWrapper(itemView)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup): BaseViewHolder<Answer> = QuestionDetailViewWrapper(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Answer>, position: Int) {
        super.onItemClickListener(holder, position)
        val question = questionDetail ?: return
        AnswerDetailActivity.start(context, question.tags, question.id, question.title, question.hasAdoptedAnswer,
                question.isSelf, question.shouldShowGenderIcon(), dataSet[position])
    }

    fun refreshData(questionDetail: QuestionDetail) {
        this.questionDetail = questionDetail
        (headerViewWrapper as QuestionDetailHeaderViewWrapper).setData(questionDetail)
        val answers = questionDetail.answers ?: mutableListOf()
        nextPage = 2
        refreshData(answers)
        resortData(sortOrder)
    }

    override fun onLoadMoreData(footer: BaseViewHolder<*>) {
        super.onLoadMoreData(footer)
        if (footer !is QuestionDetailFooterViewWrapper) {
            LogUtils.LOGW(javaClass.simpleName, "footer is not the subclass of QuestionDetailFooterViewWrapper")
            return
        }
        footer.setData(QuestionDetailFooterViewWrapper.LOADING)
        val user = BaseAPP.getUser(context)!!
        RequestManager.INSTANCE.loadMoreAnswer(SimpleObserver(context, object : SubscriberListener<List<Answer>>(QAErrorHandler) {
            override fun onNext(t: List<Answer>?) {
                super.onNext(t)
                if (t == null || t.isEmpty()) {
                    footer.setData(QuestionDetailFooterViewWrapper.NO_MORE_DATA)
                    return
                }
                footer.setData(QuestionDetailFooterViewWrapper.LOAD_SUCCESS)
                addData(t)
                ++nextPage
            }

            override fun onError(e: Throwable?): Boolean {
                footer.setData(QuestionDetailFooterViewWrapper.LOAD_FAIL)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, questionDetail!!.id, nextPage)
    }

    override fun addData(dataSet: List<Answer>) {
        super.addData(dataSet)
        resortData(sortOrder)
    }

    private fun resortData(sortBy: String) {
        sortOrder = sortBy
        if (sortBy == sortByDefault) {
            sortByDefault()
        } else {
            sortByTime()
        }
    }

    private fun sortByDefault() {
        val sortedList = dataSet.sortedWith(kotlin.Comparator { o1, o2 ->
            compareValuesBy(o2, o1, Answer::isAdopted, Answer::hotValue, Answer::time)
        })
        refreshData(sortedList)
    }

    private fun sortByTime() {
        val sortedList = dataSet.sortedByDescending(Answer::time)
        refreshData(sortedList)
    }

    inner class QuestionDetailViewWrapper(parent: ViewGroup) : BaseViewHolder<Answer>(parent, R.layout.item_question_detail) {
        override fun setData(data: Answer) {
            super.setData(data)
            itemView.apply {
                ImageLoader.getInstance().loadAvatar(data.photoThumbnailSrc, avatar)
                nickname.text = data.nickname
                nickname.initGender(questionDetail!!.shouldShowGenderIcon(), data.isFemale)
                content.setText(data.content)
                date.text = data.createdAt.toDate("yyyy-MM-dd HH:mm:ss").toFormatString("yyyy-MM-dd")
                comment.text = data.commentNum
                initFavor(data)
                initAccept(accepted, accept, data.isAdopted, questionDetail!!.isSelf, questionDetail!!.hasAdoptedAnswer) {
                    doAccept(data.id, questionDetail!!.id)
                }
            }
        }

        private fun initFavor(answer: Answer) {
            initFavor(answer.isPraised, answer.praiseNum)
            itemView.favor.setOnClickListener { doFavor(answer) }
        }

        private fun initFavor(isPraised: Boolean, praiseNum: String) {
            val resId = if (isPraised) R.drawable.ic_question_detail_favor else R.drawable.ic_question_detail_unfavor
            val drawable = itemView.resources.getDrawable(resId)
            itemView.favor.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            itemView.favor.text = praiseNum
        }

        private fun doFavor(answer: Answer) {
            itemView.favor.isClickable = false
            val flag = if (answer.isPraised) -1 else 1
            initFavor(!answer.isPraised, "${answer.praiseNumInt + flag}")
            val user = BaseAPP.getUser(context)
            RequestManager.INSTANCE.praiseAnswer(SimpleObserver<Unit>(context, object : SubscriberListener<Unit>(QAErrorHandler) {
                override fun onNext(t: Unit?) {
                    super.onNext(t)
                    itemView.favor.isClickable = true
                    answer.is_praised = if (answer.isPraised) "0" else "1"
                    answer.praiseNum = "${answer.praiseNumInt + flag}"
                }

                override fun onError(e: Throwable?): Boolean {
                    itemView.favor.isClickable = true
                    initFavor(answer.isPraised, answer.praiseNum)
                    return super.onError(e)
                }
            }), user.stuNum, user.idNum, answer.id, answer.isPraised)
        }

        private fun doAccept(aid: String, qid: String) {
            val user = BaseAPP.getUser(context)!!
            RequestManager.INSTANCE.acceptAnswer(SimpleObserver<Unit>(context, object : SubscriberListener<Unit>(QAErrorHandler) {
                override fun onNext(t: Unit?) {
                    super.onNext(t)
                    questionDetail!!.hasAdoptedAnswer = true
                    itemView.apply {
                        accept.gone()
                        accepted.visible()
                    }
                }
            }), user.stuNum, user.idNum, aid, qid)
        }
    }
}