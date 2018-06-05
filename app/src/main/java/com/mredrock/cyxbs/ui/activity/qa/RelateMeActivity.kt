package com.mredrock.cyxbs.ui.activity.qa

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.RelateMeItem
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.QuestionDetailFooterViewWrapper
import com.mredrock.cyxbs.ui.adapter.qa.RelateMeRvAdapter
import kotlinx.android.synthetic.main.activity_relate_me.*
import kotlinx.android.synthetic.main.layout_me_empty_data.view.*
import java.util.*

class RelateMeActivity : BaseActivity(), TabLayout.OnTabSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private var type = 3

    private lateinit var adapter: RelateMeRvAdapter
    private var allList: ArrayList<RelateMeItem> = ArrayList()
    private var commentList: ArrayList<RelateMeItem> = ArrayList()
    private var favorList: ArrayList<RelateMeItem> = ArrayList()
//    private var shouldRefreshAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relate_me)
        initToolbar()
        initView()
    }

    private fun initToolbar() {
        toolbar.title = "与我相关"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initView() {
        tabLayout.apply {
            addTab(tabLayout.newTab().setText("全部"))
            addTab(tabLayout.newTab().setText("评论"))
            addTab(tabLayout.newTab().setText("点赞"))
            addOnTabSelectedListener(this@RelateMeActivity)
        }

        easyRv.apply {
            val linearLayoutManager = LinearLayoutManager(this@RelateMeActivity)
            val itemView = layoutInflater.inflate(R.layout.layout_me_empty_data, this, false)
            itemView.hint.text = getString(R.string.relate_me_empty_hint)
            emptyView = itemView
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            setLayoutManager(linearLayoutManager)
            this@RelateMeActivity.adapter = RelateMeRvAdapter(easyRv.recyclerView, this@RelateMeActivity) { onLoadMore(it) }
            adapter = this@RelateMeActivity.adapter
            showEmpty()
            setRefreshListener(this@RelateMeActivity)
            easyRv.setRefreshing(true, true)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        when (tab.position) {
            1 -> {
                type = 2
                adapter.refreshData(commentList)
            }
            2 -> {
                type = 1
                adapter.refreshData(favorList)
            }
            else -> {
                type = 3
                /*if (shouldRefreshAll) {
                    allList.clear()
                    allList.addAll(favorList)
                    allList.addAll(commentList)
                    allList.sortByDescending { it.createdAt }
                }*/
                adapter.refreshData(allList)
            }
        }
        easyRv.scrollToPosition(0)
        if (adapter.itemCount == 0) {
            easyRv.setRefreshing(true, true)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) = Unit

    override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

    override fun onRefresh() {
        val user = BaseAPP.getUser(this)!!
        val presentType = type
        RequestManager.INSTANCE.getRelateMeList(SimpleObserver(this, object : SubscriberListener<List<RelateMeItem>>(QAErrorHandler) {
            override fun onNext(t: List<RelateMeItem>) {
                super.onNext(t)
                easyRv.setRefreshing(false)
                adapter.refreshData(t)
                val list = getPresentList(presentType)
                list.clear()
                list.addAll(t)
                /*shouldRefreshAll = true
                if (presentType == 3) {
                    shouldRefreshAll = false
                    val map = t.groupBy { it.isComment }
                    commentList.clear()
                    favorList.clear()
                    commentList.addAll(map.getOrDefault(true, emptyList()))
                    favorList.addAll(map.getOrDefault(false, emptyList()))
                }*/
            }

            override fun onError(e: Throwable?): Boolean {
                easyRv.setRefreshing(false)
                adapter.refreshData(emptyList())
                easyRv.showEmpty()
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, 1, presentType)
    }

    private fun onLoadMore(wrapper: BaseViewHolder<Int>) {
        wrapper.setData(QuestionDetailFooterViewWrapper.LOADING)
        val user = BaseAPP.getUser(this)!!
        val presentType = type
        val list = getPresentList(presentType)
        if (list.size % 6 != 0) {
            wrapper.setData(QuestionDetailFooterViewWrapper.NO_MORE_DATA)
            return
        }
        RequestManager.INSTANCE.getRelateMeList(SimpleObserver(this, object : SubscriberListener<List<RelateMeItem>>(QAErrorHandler) {
            override fun onNext(t: List<RelateMeItem>) {
                super.onNext(t)
                wrapper.setData(QuestionDetailFooterViewWrapper.LOAD_SUCCESS)
                if (t.isEmpty()) {
                    wrapper.setData(QuestionDetailFooterViewWrapper.NO_MORE_DATA)
                    return
                }
                list.addAll(t)
                adapter.addData(t)
                /*shouldRefreshAll = true
                if (presentType == 3) {
                    shouldRefreshAll = false
                    val map = t.groupBy { it.isComment }
                    commentList.addAll(map.getOrDefault(true, emptyList()))
                    favorList.addAll(map.getOrDefault(false, emptyList()))
                }*/
            }

            override fun onError(e: Throwable?): Boolean {
                easyRv.setRefreshing(false)
                wrapper.setData(QuestionDetailFooterViewWrapper.LOAD_FAIL)
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, list.size / 6 + 1, presentType)
    }

    private fun getPresentList(presentType: Int) = when (presentType) {
        1 -> favorList
        2 -> commentList
        else -> allList
    }
}
