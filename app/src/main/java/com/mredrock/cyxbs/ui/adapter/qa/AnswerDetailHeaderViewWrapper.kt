package com.mredrock.cyxbs.ui.adapter.qa

import android.annotation.SuppressLint
import android.text.Html
import android.view.View
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.model.qa.Answer
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.*
import kotlinx.android.synthetic.main.header_answer_detail.view.*

/**
 * Created By jay68 on 2018/3/20.
 */
class AnswerDetailHeaderViewWrapper(itemView: View,
                                    private val tags: String,
                                    private val qid: String,
                                    private val questionTitle: String,
                                    private var remarkCount: Int,
                                    private var hasAdoptedAnswer: Boolean,
                                    private val shouldShowGenderIcon: Boolean,
                                    private val isSelf: Boolean) : BaseViewHolder<Answer>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: Answer) {
        super.setData(data)
        itemView.apply {
            title.text = Html.fromHtml("<font color=\"#7195fa\">#$tags#</font> $questionTitle")
            ImageLoader.getInstance().loadAvatar(data.photoThumbnailSrc, avatar)
            nickname.text = data.nickname
            nickname.initGender(shouldShowGenderIcon, data.isFemale)
            date.text = data.createdAt.split(" ")[0]
            initAccept(accepted, accept, data.isAdopted, isSelf, hasAdoptedAnswer) { doAccept(data.id, qid) }
            content.text = data.content
            images.setImageUrls(data.photoUrl, this, singleImage)
            commentCount.text = "" + remarkCount
        }
    }

    fun refreshCommentCount(count: String) {
        itemView.commentCount.text = count
    }

    private fun doAccept(aid: String, qid: String) {
        //TODO 待测试
        val user = BaseAPP.getUser(context)!!
        RequestManager.INSTANCE.acceptAnswer(SimpleObserver<Unit>(context, object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                //todo notify pre activity to refresh
                hasAdoptedAnswer = true
                itemView.apply {
                    accept.gone()
                    accepted.visible()
                }
            }
        }), user.stuNum, user.idNum, aid, qid)
    }
}