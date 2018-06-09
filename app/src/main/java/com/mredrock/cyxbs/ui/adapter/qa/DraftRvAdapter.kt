package com.mredrock.cyxbs.ui.adapter.qa

import android.app.Activity
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.EditText
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.Draft
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.help.PostHelpActivity
import com.mredrock.cyxbs.ui.activity.qa.AnswerQuestionActivity
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.invisible
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.dialog_answer_detail.view.*
import kotlinx.android.synthetic.main.item_draft_rv.view.*
import org.jetbrains.anko.*
import kotlinx.android.synthetic.main.dialog_answer_detail.view.content as comment

/**
 * Created By jay68 on 2018/06/08.
 */
class DraftRvAdapter(private val context: Activity, private val rv: RecyclerView) : BaseHeaderAndFooterViewAdapter<Draft>(context) {
    private var curPage = 1
    private val replyDialog by lazy { initDialog() }
    private var onSubmitListener: (() -> Unit)? = null
    private var onRefreshListener: ((String) -> Unit)? = null
    private var aid: String = ""

    var isEditing = false
        set(value) {
            field = value
            hideFooter(value)
        }
    //执行删除后刷新数据集，避免上拉加载出错
    var needToRefreshData = false

    override fun onCreateDataViewHolder(parent: ViewGroup): BaseViewHolder<Draft> = DraftViewWrapper(parent)

    override fun onItemClickListener(holder: BaseViewHolder<Draft>, position: Int) {
        super.onItemClickListener(holder, position)
        if (isEditing) return

        val draft = dataList[position]
        when (draft.type) {
            Draft.TYPE_QUESTION -> {
                val question = draft.question ?: return
                PostHelpActivity.startActivityFromDraft(context, question.kind, draft.id, question.title, question.description)
            }
            Draft.TYPE_ANSWER -> {
                AnswerQuestionActivity.startFromDraft(context, draft.targetId, draft.id, draft.content)
            }
            Draft.TYPE_COMMENT -> {
                aid = draft.targetId
                replyDialog.findViewById<EditText>(R.id.content)?.setText(draft.content)
                onSubmitListener = {
                    dataList.removeAt(position)
                    notifyItemRemoved(position)
                    needToRefreshData = true
                    (holder as DraftViewWrapper).deleteDraft(draft.id, draft, position)
                }
                onRefreshListener = { doRefresh(draft, position, it) }
                replyDialog.show()
            }
        }
    }

    private fun initDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(context)
        val container = dialog.layoutInflater.inflate(R.layout.dialog_answer_detail, rv, false)
        dialog.setContentView(container)
        container.submit.setOnClickListener { doSubmitComment(container.comment) }
        dialog.setOnShowListener { container.comment.setText(RelateMeRvAdapter.draft) }
        dialog.setOnDismissListener {
            val comment = container.comment.text.toString()
            if (comment.isNotBlank()) {
                onRefreshListener?.invoke(comment)
            }
        }
        return dialog
    }

    private fun doSubmitComment(editText: EditText) {
        val content = editText.editableText.toString()
        if (content.isBlank()) {
            context.toast("评论不能为空哦~")
            return
        }
        val user = BaseAPP.getUser(context)!!
        RequestManager.INSTANCE.commentAnswer(SimpleObserver<Unit>(context, true, false, object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                editText.setText("")
                replyDialog.dismiss()
                context.toast("评论成功")
                onSubmitListener?.invoke()
            }
        }), user.stuNum, user.idNum, aid, content)
    }

    private fun doRefresh(draft: Draft, position: Int, newContent: String) {
        if (draft.content == newContent) return
        dataList[position] = draft.copy(content = newContent)
        notifyItemChanged(position)
        val user = BaseAPP.getUser(context)
        RequestManager.INSTANCE.refreshDraft(SimpleObserver(context, object : SubscriberListener<Unit>() {
            override fun onError(e: Throwable?): Boolean {
                dataList[position] = draft
                notifyItemChanged(position)
                return true
            }
        }), user.stuNum, user.idNum, newContent, draft.id)
    }

    override fun onLoadMoreData(footer: BaseViewHolder<*>) {
        super.onLoadMoreData(footer)
        if (isEditing) return
        (footerViewWrapper as BaseFooterViewWrapper).setData(BaseFooterViewWrapper.LOADING)
        val user = BaseAPP.getUser(context)!!
        RequestManager.INSTANCE.getDraftList(SimpleObserver(context, object : SubscriberListener<List<Draft>>() {
            override fun onNext(t: List<Draft>) {
                super.onNext(t)
                if (t.isEmpty()) {
                    (footerViewWrapper as BaseFooterViewWrapper).setData(BaseFooterViewWrapper.NO_MORE_DATA)
                    return
                }
                addData(t)
                (footerViewWrapper as BaseFooterViewWrapper).setData(BaseFooterViewWrapper.LOAD_SUCCESS)
            }

            override fun onError(e: Throwable?): Boolean {
                (footerViewWrapper as BaseFooterViewWrapper).setData(BaseFooterViewWrapper.LOAD_FAIL)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, ++curPage)
    }

    override fun refreshData(dataSet: Collection<Draft>) {
        super.refreshData(dataSet)
        curPage = 1
        needToRefreshData = false
    }

    private fun hideFooter(editing: Boolean) {
        if (editing) footerViewWrapper?.itemView?.invisible() else footerViewWrapper?.itemView?.visible()
    }

    inner class DraftViewWrapper(parent: ViewGroup) : BaseViewHolder<Draft>(parent, R.layout.item_draft_rv) {
        override fun setData(data: Draft) {
            super.setData(data)
            itemView.apply {
                title.text = data.titleDisplay
                content.text = data.contentDisplay
                time.text = data.timeDisplay
                if (data.isQuestion) {
                    content.gone()
                } else {
                    content.visible()
                }
                if (isEditing) {
                    contentContainer.translationX = dip(55).toFloat()
                    deleteButton.visible()
                    deleteButton.setOnClickListener { deleteItem(data.id) }
                } else {
                    contentContainer.translationX = dip(0).toFloat()
                    deleteButton.gone()
                }
            }
        }

        private fun deleteItem(id: String) {
            context.alert("删除后将无法回复，确定要删除吗？", "提示") {
                yesButton {
                    val position = rv.getChildAdapterPosition(itemView)
                    val draft = dataList.removeAt(position)
                    notifyItemRemoved(position)
                    needToRefreshData = true
                    deleteDraft(id, draft, position)
                }
                noButton { }
            }.show()
        }

        fun deleteDraft(id: String, draft: Draft, position: Int) {
            val user = BaseAPP.getUser(context)!!
            RequestManager.INSTANCE.deleteDraft(SimpleObserver(context, object : SubscriberListener<Unit>() {
                override fun onError(e: Throwable?): Boolean {
                    notifyItemInserted(position)
                    dataList.add(position, draft)
                    return super.onError(e)
                }
            }), user.stuNum, user.idNum, id)
        }
    }
}