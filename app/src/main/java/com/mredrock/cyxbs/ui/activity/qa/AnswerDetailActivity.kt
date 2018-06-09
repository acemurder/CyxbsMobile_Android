package com.mredrock.cyxbs.ui.activity.qa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.Answer
import com.mredrock.cyxbs.model.qa.Draft
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.AnswerDetailAdapter
import com.mredrock.cyxbs.ui.adapter.qa.AnswerDetailHeaderViewWrapper
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.verticalLayout
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.activity_comment_detail.*
import kotlinx.android.synthetic.main.dialog_answer_detail.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AnswerDetailActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var tags: String
    private lateinit var qid: String
    private lateinit var questionTitle: String
    private lateinit var answer: Answer
    private var isSelf = false
    private var hasAdoptedAnswer = true
    private var shouldShowGenderIcon = false

    private lateinit var adapter: AnswerDetailAdapter
    private val commentDialog: BottomSheetDialog by lazy { initDialog() }

    private val menuContentView: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(menuContentView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    companion object {
        @JvmStatic
        var draft = ""

        @JvmStatic
        fun start(context: Context,
                  tags: String,
                  qid: String,
                  questionTitle: String,
                  hasAdoptedAnswer: Boolean,
                  isSelf: Boolean,
                  shouldShowGenderIcon: Boolean,
                  answer: Answer) {
            context.startActivity<AnswerDetailActivity>(
                    "tags" to tags,
                    "qid" to qid,
                    "questionTitle" to questionTitle,
                    "hasAdoptedAnswer" to hasAdoptedAnswer,
                    "isSelf" to isSelf,
                    "shouldShowGenderIcon" to shouldShowGenderIcon,
                    "answer" to answer
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_detail)
        initData()
        initView()
    }

    private fun initData() {
        tags = intent.getStringExtra("tags")
        qid = intent.getStringExtra("qid")
        questionTitle = intent.getStringExtra("questionTitle")
        hasAdoptedAnswer = intent.getBooleanExtra("hasAdoptedAnswer", true)
        isSelf = intent.getBooleanExtra("isSelf", false)
        shouldShowGenderIcon = intent.getBooleanExtra("shouldShowGenderIcon", false)
        answer = intent.getParcelableExtra("answer")
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        initFooter()
        initRv()
    }

    @SuppressLint("SetTextI18n")
    private fun initFooter() {
        initFavor(answer.isPraised, answer.praiseNum)
        commentNum.text = "(${answer.commentNum})"
        praise.setOnClickListener { doFavor() }
        comment.setOnClickListener { doComment() }
    }

    private fun doFavor() {
        praise.isClickable = false
        val flag = if (answer.isPraised) -1 else 1
        initFavor(!answer.isPraised, "${answer.praiseNumInt + flag}")
        val user = BaseAPP.getUser(this)
        RequestManager.INSTANCE.praiseAnswer(SimpleObserver<Unit>(this, object : SubscriberListener<Unit>() {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                praise.isClickable = true
                answer.is_praised = if (answer.isPraised) "0" else "1"
                answer.praiseNum = "${answer.praiseNumInt + flag}"
            }

            override fun onError(e: Throwable?): Boolean {
                praise.isClickable = true
                initFavor(answer.isPraised, answer.praiseNum)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, answer.id, answer.isPraised)
    }

    private fun initFavor(isPraised: Boolean, count: String) {
        val resId = if (isPraised) R.drawable.ic_answer_detail_favor else R.drawable.ic_answer_detail_unfavor
        val drawable = resources.getDrawable(resId)
        praiseIcon.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        praiseNum.text = "($count)"
    }

    private fun doComment() {
        footer.isEnabled = false
        footer.animate().apply {
            translationYBy(footer.height.toFloat())
            duration = 200
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    footer.apply {
                        isEnabled = true
                        translationY = 0f
                        gone()
                    }
                    commentDialog.show()
                }
            })
        }
    }

    private fun initDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(this)
        val container = layoutInflater.inflate(R.layout.dialog_answer_detail, null, false)
        dialog.setContentView(container)
        container.submit.setOnClickListener { doSubmitComment(container.content) }
        dialog.setOnShowListener { container.content.setText(draft) }
        dialog.setOnDismissListener {
            draft = container.content.editableText.toString()
            footer.visible()
        }
        return dialog
    }

    private fun doSubmitComment(editText: EditText) {
        val content = editText.editableText.toString()
        if (content.isBlank()) {
            toast("评论不能为空哦~")
            return
        }
        val user = BaseAPP.getUser(this)!!
        RequestManager.INSTANCE.commentAnswer(SimpleObserver<Unit>(this, true, false, object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                editText.setText("")
                draft = ""
                commentDialog.dismiss()
                answer.commentNum = "${answer.commentNumInt + 1}"
                commentNum.text = "(${answer.commentNum})"
                (adapter.headerViewWrapper as AnswerDetailHeaderViewWrapper).refreshCommentCount(answer.commentNum)
                easyRv.setRefreshing(true, true)
            }
        }), user.stuNum, user.idNum, answer.id, content)
    }

    private fun initRv() {
        easyRv.recyclerView.verticalLayout()
        adapter = AnswerDetailAdapter(this, tags, qid, questionTitle, answer.commentNumInt,
                hasAdoptedAnswer, isSelf, easyRv.recyclerView, answer, shouldShowGenderIcon)
        easyRv.adapter = adapter
        easyRv.setRefreshListener(this)
        easyRv.setRefreshing(true, true)
    }

    override fun onRefresh() {
        val user = BaseAPP.getUser(this)!!
        RequestManager.INSTANCE.getAnswerCommentList(SimpleObserver<List<Answer>>(
                this, object : SubscriberListener<List<Answer>>(QAErrorHandler) {

            override fun onNext(t: List<Answer>) {
                super.onNext(t)
                adapter.refreshData(t)
                easyRv.setRefreshing(false)
            }

            override fun onError(e: Throwable?): Boolean {
                easyRv.setRefreshing(false)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, answer.id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_question_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.more) {
            popupWindow.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMenuContentView(): View {
        val root = layoutInflater.inflate(R.layout.popup_window_question_detail_menu, null, false)
        root.find<View>(R.id.share).setOnClickListener {
            //todo share
            popupWindow.dismiss()
        }
        root.find<View>(R.id.report).setOnClickListener {
            ReportActivity.start(this, qid)
            popupWindow.dismiss()
        }
        return root
    }

    private fun PopupWindow.init(): PopupWindow {
        isTouchable = true
        isOutsideTouchable = true
        animationStyle = R.style.PopupAnimation
        setOnDismissListener { frame.gone() }
        return this
    }

    private fun PopupWindow.show() {
        showAtLocation(toolbar, Gravity.END or Gravity.TOP, 0, toolbar.height)
        frame.visible()
    }

    private fun saveToDraft() {
        val str = AnswerDetailActivity.draft
        if (str.isBlank()) return
        toast("您未提交的内容将提交至草稿箱")
        val user = BaseAPP.getUser(this)
        RequestManager.INSTANCE.addDraft(SimpleObserver(BaseAPP.getContext(), object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onError(e: Throwable?): Boolean {
                BaseAPP.getContext().longToast("保存至草稿箱失败，请在app退出前重新尝试或直接提交, app退出后记录将丢失")
                return true
            }
        }), user.stuNum, user.idNum, Draft.TYPE_COMMENT, str, answer.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveToDraft()
    }
}
