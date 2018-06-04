package com.mredrock.cyxbs.ui.fragment.help

import android.annotation.SuppressLint
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
import com.mredrock.cyxbs.model.User
import com.mredrock.cyxbs.model.help.MyQuestion
import com.mredrock.cyxbs.model.help.Question
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.EndlessRecyclerOnScrollListener
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener

import com.mredrock.cyxbs.ui.activity.social.FooterViewWrapper
import com.mredrock.cyxbs.ui.adapter.HeaderViewRecyclerAdapter
import com.mredrock.cyxbs.ui.adapter.HelpAdapter
import com.mredrock.cyxbs.ui.adapter.MyHelpAdapter
import com.mredrock.cyxbs.ui.fragment.BaseFragment
import com.mredrock.cyxbs.util.Utils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_base_my_help.*
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ValidFragment")
class BaseMyHelpFragment : BaseFragment, SwipeRefreshLayout.OnRefreshListener {
    val PER_PAGE_NUM = 6
    val TAG = "BaseHelpFragment"
    val FIRST_PAGE_INDEX = 1
    val isReaded = true

    private var mUser: User? = null
    private var adapter : MyHelpAdapter? = null
    private var type = TYPE_ASK_ADOPTED

    private var currentIndex = 0
    private var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var mFooterViewWrapper: FooterViewWrapper? = null
    private var mHeaderViewRecyclerAdapter: HeaderViewRecyclerAdapter? = null
    private var list: List<MyQuestion> ? = null

    companion object {
        const val TYPE_HELP_ADOPTED = 0
        const val TYPE_HELP_UNADOPTED = 1
        const val TYPE_ASK_ADOPTED = 2
        const val TYPE_ASK_UNADOPTED = 3
    }

    constructor(type: Int): this() {
        this.type = type
    }

    constructor() {}


    fun provideData(observer: SimpleObserver<List<MyQuestion>>, size: Int, page: Int) {
        when (type) {
            TYPE_HELP_ADOPTED ->  {
                RequestManager.getInstance().getMyHelp( observer, mUser?.stuNum, mUser?.idNum , page, PER_PAGE_NUM, 1)
            }

            TYPE_HELP_UNADOPTED -> {
                RequestManager.getInstance().getMyHelp( observer, mUser?.stuNum, mUser?.idNum , page, PER_PAGE_NUM, 2)
            }

            TYPE_ASK_ADOPTED -> {
                RequestManager.getInstance().getMyAsk( observer, mUser?.stuNum, mUser?.idNum , page, PER_PAGE_NUM, 2)
            }

            TYPE_ASK_UNADOPTED -> {
                RequestManager.getInstance().getMyAsk( observer, mUser?.stuNum, mUser?.idNum , page, PER_PAGE_NUM, 1)
            }

            else -> {

            }
        }
    }

    override fun onRefresh() {
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX)
        currentIndex = 0
        addOnScrollListener()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_base_my_help, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUser = BaseAPP.getUser(activity)
        init()
    }

    private fun init() {
        refresh.setColorSchemeColors(
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorAccent),
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorPrimary)
        )
        refresh.setOnRefreshListener(this)

        mLinearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLinearLayoutManager

        addOnScrollListener()
        initAdapter(null)
    }

    fun initAdapter(questions: List<MyQuestion>?) {
        if (recyclerView == null) return   // prevent it be called before lazy loading
        list = questions
        adapter = MyHelpAdapter(null, type)
        mHeaderViewRecyclerAdapter = HeaderViewRecyclerAdapter(adapter)
        recyclerView.setAdapter(mHeaderViewRecyclerAdapter)
        addFooterView(mHeaderViewRecyclerAdapter!!)
        mFooterViewWrapper?.getCircleProgressBar()!!.visibility = View.INVISIBLE
    }

    private fun addOnScrollListener() {
        if (endlessRecyclerOnScrollListener != null)
            recyclerView.removeOnScrollListener(endlessRecyclerOnScrollListener)
        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            override fun onLoadMore(page: Int) {
                currentIndex++
                getNextPageData(PER_PAGE_NUM, currentIndex)
            }

            override fun onShow() {

            }

            override fun onHide() {

            }
        }
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener)
    }

    fun getCurrentData(size: Int, page: Int) {
        refresh.post(Runnable { this.showLoadingProgress() })
        provideData(SimpleObserver(activity, object : SubscriberListener<List<MyQuestion>>() {
            override fun onError(e: Throwable): Boolean {
                super.onError(e)
                mFooterViewWrapper?.showLoadingFailed()
                closeLoadingProgress()
                return false
            }

            override fun onNext(Questions: List<MyQuestion>) {
                super.onNext(Questions)
                if (list == null) {
                    initAdapter(Questions)
                    if (Questions.size == 0);
                    mFooterViewWrapper?.showLoadingNoData()
                } else
                    adapter?.replaceDataList(Questions)
                closeLoadingProgress()
            }
        }), size, page)
    }

    private fun getNextPageData(size: Int, page: Int) {
        mFooterViewWrapper?.showLoading()
        provideData(SimpleObserver(context, object : SubscriberListener<List<MyQuestion>>() {
            override fun onError(e: Throwable): Boolean {
                super.onError(e)
                mFooterViewWrapper?.showLoadingFailed()
                return true
            }

            override fun onNext(questions: List<MyQuestion>) {
                super.onNext(questions)
                if (questions.size == 0) {
                    mFooterViewWrapper?.showLoadingNoMoreData()
                    return
                }
                adapter?.addDataList(questions)
            }
        }), size, page)
    }

    private fun addFooterView(mHeaderViewRecyclerAdapter: HeaderViewRecyclerAdapter) {
        mFooterViewWrapper = FooterViewWrapper(recyclerView)
        mHeaderViewRecyclerAdapter.addFooterView(mFooterViewWrapper!!.getFooterView())
        mFooterViewWrapper!!.onFailedClick { view ->
            if (currentIndex == 0) getCurrentData(PER_PAGE_NUM, currentIndex)
            getNextPageData(PER_PAGE_NUM, currentIndex)
        }
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
