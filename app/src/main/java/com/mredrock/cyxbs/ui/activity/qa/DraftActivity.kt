package com.mredrock.cyxbs.ui.activity.qa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.Draft
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.BaseFooterViewWrapper
import com.mredrock.cyxbs.ui.adapter.qa.DraftRvAdapter
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.toInt
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.activity_draft.*
import kotlinx.android.synthetic.main.item_draft_rv.view.*
import kotlinx.android.synthetic.main.layout_me_empty_data.view.*
import org.jetbrains.anko.dip

class DraftActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val RESULT_DRAFT_SUBMITTED = 0x123
        const val RESULT_DRAFT_REFRESHED = 0x124
    }

    private var isEditing = false
    private var runningAnimatorCount = 0
    private lateinit var adapter: DraftRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        initToolBar()
        initRv()
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar)
        title = ""
        toolbar_title.text = "草稿箱"
        toolbar.setNavigationOnClickListener { finish() }
        toolbarMenu.setOnClickListener {
            if (runningAnimatorCount != 0) return@setOnClickListener
            isEditing = !isEditing
            toolbarMenu.text = if (isEditing) "取消" else "编辑"
            adapter.isEditing = isEditing
            easyRv.swipeToRefresh.isEnabled = !isEditing
            animateShowDeleteButton()
        }
    }

    private fun animateShowDeleteButton() {
        val lm = easyRv.recyclerView.layoutManager as LinearLayoutManager
        //对所有可见item执行动画
        for ((j, i) in (lm.findFirstVisibleItemPosition()..lm.findLastVisibleItemPosition()).withIndex()) {
            val itemView = lm.getChildAt(i)
            if (itemView == null || itemView == adapter.footerViewWrapper?.itemView) {
                continue
            }
            ++runningAnimatorCount
            animateDeleteButton(itemView.deleteButton, j)
            animateContentContainer(itemView.contentContainer, j)
        }
    }

    private fun animateContentContainer(view: View, index: Int) {
        val translation = dip(55) * (if (isEditing) 1 else -1)  //fab size + margin
        view.animate().apply {
            translationXBy(translation.toFloat())
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = index * 100L + if (!isEditing) 100 else 0
        }.start()
    }

    private fun animateDeleteButton(view: View, index: Int) {
        view.apply {
            scaleX = 1f - isEditing.toInt()
            scaleY = 1f - isEditing.toInt()
            if (isEditing) visible()
        }
        view.animate().apply {
            scaleX(isEditing.toInt().toFloat())
            scaleY(isEditing.toInt().toFloat())
            duration = 300
            interpolator = LinearInterpolator()
            startDelay = index * 100L + if (isEditing) 100 else 0
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    --runningAnimatorCount
                    if (!isEditing) view.gone()
                    if (runningAnimatorCount == 0) {
                        if (adapter.needToRefreshData) {
                            //执行删除后刷新数据集，避免上拉加载出错
                            easyRv.setRefreshing(true, true)
                        } else {
                            //刷新列表，将动画未执行的item项置位相应状态
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }.start()
    }

    private fun initRv() {
        adapter = DraftRvAdapter(this, easyRv.recyclerView)
        easyRv.apply {
            val linearLayoutManager = LinearLayoutManager(this@DraftActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            setLayoutManager(linearLayoutManager)
            adapter = this@DraftActivity.adapter
            val footerItemView = layoutInflater.inflate(R.layout.footer_base_footer_view_wrapper, this, false)
            this@DraftActivity.adapter.footerViewWrapper = BaseFooterViewWrapper(footerItemView)
            val emptyItemView = layoutInflater.inflate(R.layout.layout_me_empty_data, this, false)
            emptyItemView.hint.text = getString(R.string.draft_empty_hint)
            emptyView = emptyItemView
            showEmpty()
            setRefreshListener(this@DraftActivity)
            setRefreshing(true, true)
        }
    }

    override fun onRefresh() {
        val user = BaseAPP.getUser(this)!!
        RequestManager.INSTANCE.getDraftList(SimpleObserver(this, object : SubscriberListener<List<Draft>>() {
            override fun onNext(t: List<Draft>) {
                super.onNext(t)
                adapter.refreshData(t)
                toolbarMenu.visible()
                if (t.isEmpty()) {
                    toolbarMenu.gone()
                    easyRv.showEmpty()
                }
            }

            override fun onError(e: Throwable?): Boolean {
                easyRv.showEmpty()
                toolbarMenu.gone()
                adapter.refreshData(emptyList())
                return super.onError(e)
            }
        }), user.stuNum, user.idNum, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_DRAFT_SUBMITTED && data != null) {
            deleteDraft(data.getStringExtra("id"))
        } else {
            easyRv.postDelayed({ easyRv.setRefreshing(true, true) }, 200)
        }
    }

    private fun deleteDraft(draftId: String) {
        val user = BaseAPP.getUser(this)!!
        RequestManager.INSTANCE.deleteDraft(SimpleObserver(this, object : SubscriberListener<Unit>() {
            override fun onNext(t: Unit?) {
                super.onNext(t)
                easyRv.setRefreshing(true, true)
            }
        }), user.stuNum, user.idNum, draftId)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_draft, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.edit).title = if (isEditing) "取消" else "编辑"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.edit -> {
            isEditing != isEditing
            invalidateOptionsMenu()
            true
        }
        else -> false
    }*/
}
