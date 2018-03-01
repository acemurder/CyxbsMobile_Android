package com.mredrock.cyxbs.ui.activity.qa

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupWindow
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.AnswerList
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.AnswerListRvAdapter
import kotlinx.android.synthetic.main.activity_answer_list.*
import org.jetbrains.anko.*


class AnswerListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var adapter: AnswerListRvAdapter
    private lateinit var questionId: String
    private var isSelf = false

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
            context.startActivity<AnswerListActivity>("questionId" to questionId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_list)
        initView()
        initData()
    }

    override fun onResume() {
        super.onResume()
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
        recyclerView.setLayoutManager(layoutManager)

        adapter = AnswerListRvAdapter(this)
        recyclerView.adapter = adapter

        recyclerView.setRefreshListener(this)
        adapter.onItemClickListener = {
            //todo item click
        }
    }

    private fun initData() {
        questionId = intent.getStringExtra("questionId")
        recyclerView.setRefreshing(true)
    }

    override fun onRefresh() {
        val user = BaseAPP.getUser(this)
        RequestManager.INSTANCE.getAnswerList(SimpleObserver(this, object : SubscriberListener<AnswerList>() {
            override fun onNext(answerList: AnswerList) {
                super.onNext(answerList)
                recyclerView.setRefreshing(false)
                isSelf = answerList.isSelf
                adapter.refreshData(answerList)
                initFooter()
            }

            override fun onError(e: Throwable?): Boolean {
                recyclerView.setRefreshing(false)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, questionId)
    }

    private fun initFooter() {
        if (isSelf) {
            leftFooterButtonTv.setCompoundDrawables(resources.getDrawable(R.drawable.ic_answer_list_add_reward), null, null, null)
            leftFooterButtonTv.setText(R.string.answer_list_add_reward)
            rightFooterButtonTv.setCompoundDrawables(resources.getDrawable(R.drawable.ic_answer_list_cancel_question), null, null, null)
            rightFooterButtonTv.setText(R.string.answer_list_cancel_question)
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
                    textView("\t\t\t对方将通过你的描述\n来决定是否采纳你的答案") {
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
                            AnswerQuestionActivity.start(this@AnswerListActivity, questionId)
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
        //todo cancel question
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_answer_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.more) {
            popupWindow.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMenuContentView(): View {
        val root = layoutInflater.inflate(R.layout.popup_window_answer_list_menu, null, false)
        root.find<View>(R.id.share).setOnClickListener {
            //todo share
        }
        root.find<View>(R.id.report).setOnClickListener {
            //todo report
        }
        return root
    }

    private fun PopupWindow.init(): PopupWindow {
        isTouchable = true
        isOutsideTouchable = true
        animationStyle = R.style.PopupAnimation
        setOnDismissListener { frame.visibility = View.GONE }
        return this
    }

    private fun PopupWindow.show() {
        showAtLocation(toolbar, Gravity.END or Gravity.TOP, 0, toolbar.height)
        frame.visibility = View.VISIBLE
    }
}
