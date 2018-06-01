package com.mredrock.cyxbs.ui.activity.qa

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.QuestionDetail
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.QuestionDetailRvAdapter
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.activity_question_detail.*
import org.jetbrains.anko.*

class QuestionDetailActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var adapter: QuestionDetailRvAdapter
    private lateinit var questionId: String

    private val menuContentView: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(menuContentView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    private var answerDialog: DialogInterface? = null

    companion object {
        @JvmStatic
        fun start(context: Context, questionId: String) {
            context.startActivity<QuestionDetailActivity>("questionId" to questionId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)
        initView()
        initData()
        onRefresh()
    }

    private fun initView() {
        initToolbar()
        initRv()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initRv() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        easyRv.setLayoutManager(layoutManager)

        adapter = QuestionDetailRvAdapter(this, easyRv.recyclerView)
        easyRv.adapter = adapter
        easyRv.setRefreshListener(this)
    }

    private fun initData() {
        questionId = intent.getStringExtra("questionId")
        easyRv.setRefreshing(true)
    }

    override fun onRefresh() {
        val user = BaseAPP.getUser(this)
        RequestManager.INSTANCE.getAnswerList(SimpleObserver(this, object : SubscriberListener<QuestionDetail>(QAErrorHandler) {
            override fun onNext(questionDetail: QuestionDetail) {
                super.onNext(questionDetail)
                easyRv.setRefreshing(false)
                questionDetail.id = questionId
                adapter.refreshData(questionDetail)
                initFooter(questionDetail.isSelf, questionDetail.hasAdoptedAnswer)
            }

            override fun onError(e: Throwable?): Boolean {
                easyRv.setRefreshing(false)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, questionId)
    }

    private fun initFooter(isSelf: Boolean, hasAdoptedAnswer: Boolean) {
        if (hasAdoptedAnswer) {
            footer.gone()
            return
        }
        if (isSelf) {
            leftFooterButtonTv.setCompoundDrawablesWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_question_detail_add_reward), null, null, null
            )
            leftFooterButtonTv.setText(R.string.question_detail_add_reward)
            rightFooterButtonTv.setCompoundDrawablesWithIntrinsicBounds(
                    resources.getDrawable(R.drawable.ic_question_detail_cancel_question), null, null, null
            )
            rightFooterButtonTv.setText(R.string.question_detail_cancel_question)
        }
        leftFooterButton.setOnClickListener {
            if (isSelf) addReward()
            else ignore()
        }
        rightFooterButton.setOnClickListener {
            if (isSelf) deleteQuestion()
            else answerQuestion()
        }
    }

    private fun addReward() {
        //todo add reward
    }

    private fun ignore() {
        //todo ignore
    }

    private fun answerQuestion() {
        answerDialog = alert {
            customView {
                verticalLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    textView("\t\t\t对方将通过你的描述\t\t\n来决定是否采纳你的答案") {
                        textColor = Color.parseColor("#cc000000")
                        textSize = 15f
                    }.lparams(wrapContent, wrapContent) {
                        topMargin = dip(35)
                        bottomMargin = dip(25)
                    }
                    button("确定") {
                        textColor = Color.parseColor("#FEFEFE")
                        backgroundColor = Color.parseColor("#cc788efa")
                        setOnClickListener {
                            AnswerQuestionActivity.start(this@QuestionDetailActivity, questionId)
                            answerDialog?.dismiss()
                        }
                    }.lparams(matchParent, wrapContent) {
                        bottomMargin = dip(35)
                        leftMargin = dip(60)
                        rightMargin = dip(60)
                    }
                }
            }
        }.show()
    }

    private fun deleteQuestion() {
        val user = BaseAPP.getUser(this)
        RequestManager.INSTANCE.cancelQuestion(SimpleObserver<Unit>(this, object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                //todo notify pre activity question has been cancel
                finish()
            }
        }), user.stuNum, user.idNum, questionId)
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
        }
        root.find<View>(R.id.report).setOnClickListener { ReportActivity.start(this, questionId) }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AnswerQuestionActivity.ANSWER && resultCode == Activity.RESULT_OK) {
            easyRv.setRefreshing(true, true)
        }
    }
}
