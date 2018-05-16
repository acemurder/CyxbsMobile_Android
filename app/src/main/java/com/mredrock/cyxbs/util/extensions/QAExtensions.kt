package com.mredrock.cyxbs.util.extensions

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.component.widget.ninelayout.NineGridlayout
import com.mredrock.cyxbs.model.qa.QuestionDetail
import com.mredrock.cyxbs.model.social.Image
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.ui.activity.social.ImageActivity
import com.mredrock.cyxbs.util.ImageLoader
import org.jetbrains.anko.*

/**
 * 将问答社区中没法继承的公有逻辑提取到这里
 * Created By jay68 on 2018/3/20.
 */

fun NineGridlayout.setImageUrls(url: List<String>?, itemView: View, singleImage: ImageView? = null) = when {
    url == null || url.isEmpty() -> {
        gone()
        singleImage?.gone()
    }
    url.size == 1 && singleImage != null -> {
        gone()
        singleImage.visible()
        singleImage.setOnClickListener { ImageActivity.startWithData(singleImage.context, url[0]) }
        ImageLoader.getInstance().loadOffcialImg(url[0], singleImage, itemView)
    }
    else -> {
        singleImage?.gone()
        visible()
        val imageList = List(url.size) { Image(url[it], Image.TYPE_ADD) }
        setImagesData(imageList)
        setOnAddImagItemClickListener { _, position ->
            ImageActivity.startWithData(itemView.context, url[position])
        }
    }
}

fun TextView.initGender(shouldShowGenderIcon: Boolean, isFemale: Boolean = false) {
    if (shouldShowGenderIcon) {
        val resId = if (isFemale) R.drawable.ic_question_detail_female else R.drawable.ic_question_detail_male
        val gender = resources.getDrawable(resId)
        setCompoundDrawablesWithIntrinsicBounds(null, null, gender, null)
    } else {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }
}

fun initAccept(accepted: TextView, accept: TextView, isAdopted: Boolean, isSelf: Boolean,
               hasAdoptedAnswer: Boolean, doAccept: () -> Unit) {
    if (isAdopted) {
        accepted.visible()
        accept.gone()
    } else if (!isSelf || hasAdoptedAnswer) {
        accepted.gone()
        accept.gone()
    } else {
        accepted.gone()
        accept.visible()
        accept.setOnClickListener { performAccept(it.context, doAccept) }
    }
}

var acceptDialog: DialogInterface? = null

private fun performAccept(context: Context, doAccept: () -> Unit) {
    acceptDialog = context.alert {
        customView {
            verticalLayout {
                textView("\t\t\t确定采纳此回答吗？（只能采纳\t\t\n一个回答，确定后就无法取消了哦）") {
                    textColor = Color.parseColor("#cc000000")
                    textSize = 15f
                }.lparams(wrapContent, wrapContent) {
                    topMargin = dip(35)
                    bottomMargin = dip(25)
                    leftMargin = dip(30)
                    rightMargin = dip(30)
                }
                linearLayout {
                    button("确定") {
                        textColor = Color.parseColor("#FEFEFE")
                        backgroundColor = Color.parseColor("#cc788efa")
                        setOnClickListener {
                            doAccept()
                            acceptDialog?.dismiss()
                        }
                    }.lparams(0, wrapContent) { weight = 76f }

                    view().lparams(0, wrapContent) { weight = 61f }

                    button("取消") {
                        textColor = Color.parseColor("#FEFEFE")
                        backgroundColor = Color.parseColor("#cc788efa")
                        setOnClickListener { acceptDialog?.dismiss() }
                    }.lparams(0, wrapContent) { weight = 76f }
                }.lparams(matchParent, wrapContent) { bottomMargin = dip(35) }
            }
        }
    }.show()
}