package com.mredrock.cyxbs.ui.activity.help

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import com.jude.swipbackhelper.SwipeBackHelper
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.TabPagerAdapter
import com.mredrock.cyxbs.ui.fragment.help.BaseMyHelpFragment
import kotlinx.android.synthetic.main.activity_my_question.*
import kotlinx.android.synthetic.main.item_tablayout_with_point.view.*

class MyAskActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MyAskActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_question)
        init()
        initToolbar()
    }

    private fun init() {
        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false)
        val f1 = BaseMyHelpFragment(BaseMyHelpFragment.TYPE_ASK_ADOPTED)
        val f2 = BaseMyHelpFragment(BaseMyHelpFragment.TYPE_ASK_UNADOPTED)

        val fragmentList = listOf<BaseMyHelpFragment>(f1, f2)
        val titleList = listOf("已解决", "未解决")

        view_pager.adapter = TabPagerAdapter(supportFragmentManager, fragmentList, titleList)
        view_pager.offscreenPageLimit = fragmentList.size

        tab_layout.tabMode = TabLayout.MODE_FIXED
        tab_layout.setupWithViewPager(view_pager)

        for (i in 0 until tab_layout.tabCount) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_tablayout_with_point, null)
            view.tv_tab_title.text = titleList[i]

            if(fragmentList[i].isReaded)
                view.iv_tab_red.visibility = View.VISIBLE

            tab_layout.getTabAt(i)!!.customView = view
        }

    }

    private fun initToolbar() {
        if (toolbar != null) {
            toolbar.title = ""
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener({ this.finish() })
            if (actionBar != null) {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeButtonEnabled(true)
            }
        }
    }
}
