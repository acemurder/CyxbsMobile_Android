package com.mredrock.cyxbs.ui.adapter.qa

import android.content.Context
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.RelateMeItem
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.qa.QuestionDetailActivity
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.toFormatString
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.dialog_answer_detail.view.submit
import kotlinx.android.synthetic.main.item_relate_me_rv.view.*
import org.jetbrains.anko.toast
import java.util.*
import kotlinx.android.synthetic.main.dialog_answer_detail.view.content as comment

/**
 * Created By jay68 on 2018/06/05.
 */
class RelateMeRvAdapter(rv: RecyclerView, private val context: Context, private val onLoadMoreDataListener: (BaseViewHolder<Int>) -> Unit) : BaseHeaderAndFooterViewAdapter<RelateMeItem>(context) {
    companion object {
        var draft: String = ""
    }

    private val replyDialog by lazy { initDialog() }
    private lateinit var aid: String

    init {
        val itemView = LayoutInflater.from(context).inflate(R.layout.footer_question_detail, rv, false)
        footerViewWrapper = QuestionDetailFooterViewWrapper(itemView)
    }

    private fun initDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(context)
        val container = dialog.layoutInflater.inflate(R.layout.dialog_answer_detail, null, false)
        dialog.setContentView(container)
        container.submit.setOnClickListener { doSubmitComment(container.comment) }
        dialog.setOnShowListener { container.comment.setText(draft) }
        dialog.setOnDismissListener { draft = container.comment.text.toString() }
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
                draft = ""
                replyDialog.dismiss()
                context.toast("评论成功")
            }
        }), user.stuNum, user.idNum, aid, content)
    }

    override fun onCreateDataViewHolder(parent: ViewGroup) = RelateMeViewWrapper(parent)

    override fun onLoadMoreData(footer: BaseViewHolder<*>) {
        super.onLoadMoreData(footer)
        onLoadMoreDataListener(footer as BaseViewHolder<Int>)
    }

    inner class RelateMeViewWrapper(parent: ViewGroup) : BaseViewHolder<RelateMeItem>(parent, R.layout.item_relate_me_rv) {

        override fun setData(data: RelateMeItem) {
            super.setData(data)
            itemView.apply {
                ImageLoader.getInstance().loadAvatar(data.photoThumbnailSrc, avatar)
                nickname.text = data.nickname
                type.text = data.typeDescription
                time.text = formatTime(data.createdAt)
                answerContent.text = data.answerContent
                answerContainer.setOnClickListener { QuestionDetailActivity.start(context, data.questionId) }
                reply.setOnClickListener {
                    if (!replyDialog.isShowing) {
                        replyDialog.show()
                        aid = data.targetId
                    }
                }

                if (data.isComment) {
                    content.visible()
                    reply.visible()
                    content.text = data.content
                } else {
                    content.gone()
                    reply.gone()
                }

                if (data.photoSrc.isNotBlank()) {
                    image.visible()
                    ImageLoader.getInstance().loadRedrockImage(data.photoSrc, image)
                } else {
                    image.gone()
                }
            }
        }

        private fun formatTime(createAt: String): String {
            val today = Date()
            val time = createAt.split("\\s")
            return if (today.toFormatString("yyyy-MM-dd") == time[0]) {
                time[1]
            } else {
                time[0]
            }
        }
    }
}