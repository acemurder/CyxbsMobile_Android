package com.mredrock.cyxbs.ui.fragment.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.BaseAPP

import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.help.Question
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.help.MyHelpActivity
import com.mredrock.cyxbs.ui.adapter.MyHelpAdapter
import com.mredrock.cyxbs.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_base_my_help.*

class BaseMyHelpFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    val PER_PAGE_NUM = 6
    val TAG = "BaseHelpFragment"
    val FIRST_PAGE_INDEX = 1
    val isReaded = true

    override fun onRefresh() {
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_base_my_help, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        refresh.setColorSchemeColors(
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorAccent),
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorPrimary)
        )
        refresh.setOnRefreshListener(this)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        var list = arrayListOf<Question>()
        for (i in 1..10) {
            val q = Question()
            q.tags = "帮助：是的，公式是没有问题的，但是你的..."
            q.title = "提问：线性第一题不会做，求解"
            q.disappear_at = "刚刚"
            list.add(q)
        }

        recyclerView.adapter = MyHelpAdapter(list)


    }

    private fun getCurrentData(size: Int, page: Int) {
        refresh.post({ this.showLoadingProgress() })

    }

    private fun showLoadingProgress() {
        if (refresh != null) {
            refresh.isRefreshing = true
        }
    }

    private fun closeLoadingProgress() {
        if (refresh != null) {
            refresh.isRefreshing = false
        }
    }
}
