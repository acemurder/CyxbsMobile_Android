package com.mredrock.cyxbs.ui.fragment.explore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.config.Const
import com.mredrock.cyxbs.event.AskLoginEvent
import com.mredrock.cyxbs.ui.activity.explore.MapActivity
import com.mredrock.cyxbs.ui.activity.explore.electric.ElectricChargeActivity
import com.mredrock.cyxbs.ui.activity.me.*
import com.mredrock.cyxbs.ui.adapter.ExploreRollerViewAdapter
import com.mredrock.cyxbs.ui.fragment.BaseFragment
import com.mredrock.cyxbs.ui.widget.RollerView
import com.mredrock.cyxbs.util.LogUtils
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.browse
import org.jetbrains.anko.support.v4.startActivity


/**
 * Created By jay68 on 2018/05/30.
 */
class ExploreFragment : BaseFragment() {

    private val iconResIds = intArrayOf(
            R.drawable.ic_explore_no_course, R.drawable.ic_explore_empty_room, R.drawable.ic_explore_grade,
            R.drawable.ic_explore_volunteer_time, R.drawable.ic_explore_map, R.drawable.ic_explore_remind,
            R.drawable.ic_explore_calendar, R.drawable.ic_explore_electric, R.drawable.ic_explore_about
    )

    private val titleRes = arrayOf(
            "没课约", "空教室", "成绩单",
            "志愿时长", "重邮地图", "课前提醒",
            "校历", "查电费", "关于红岩"
    )

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_explore, container, false)
        setupRollerView(view.findViewById(R.id.rollerView))
        setupGridView(view.findViewById(R.id.gridView), inflater)
        return view
    }

    private fun setupRollerView(rollerView: RollerView) {
        rollerView.setAdapter(ExploreRollerViewAdapter(context, intArrayOf(R.drawable.img_cqupt1,
                R.drawable.img_cqupt2,
                R.drawable.img_cqupt3,
                R.drawable.img_cqupt1,
                R.drawable.img_cqupt2,
                R.drawable.img_cqupt3)))

        /*RequestManager.getInstance().getRollerViewInfo(SimpleObserver<List<RollerViewInfo>>(activity, object : SubscriberListener<List<RollerViewInfo>>() {
            override fun onNext(t: List<RollerViewInfo>) {
                super.onNext(t)
                rollerView.setAdapter(ExploreRollerViewAdapter(context, t))
            }
        }), "4")*/
    }

    private fun setupGridView(gridView: GridView, inflater: LayoutInflater) {
        gridView.adapter = object : BaseAdapter() {
            override fun getCount() = iconResIds.size

            override fun getItem(position: Int) = position

            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val container = inflater.inflate(R.layout.item_explore, parent, false)
                val title = container.findViewById<TextView>(R.id.title)
                val icon = container.findViewById<ImageView>(R.id.icon)
                title.text = titleRes[position]
                icon.setImageResource(iconResIds[position])
                return container
            }
        }

        gridView.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> checkLoginBeforeAction(titleRes[i]) { startActivity<NoCourseActivity>() }
                1 -> startActivity<EmptyRoomQueryActivity>()
                2 -> checkLoginBeforeAction(titleRes[i]) { startActivity<ExamAndGradeActivity>() }
                3 -> checkLoginBeforeAction(titleRes[i]) { startActivity<VolunteerTimeLoginActivity>() }
                4 -> startActivity<MapActivity>()
                5 -> checkLoginBeforeAction(titleRes[i]) { startActivity<RemindActivity>() }
                6 -> startActivity<SchoolCalendarActivity>()
                7 -> startActivity<ElectricChargeActivity>()
                8 -> browse(Const.REDROCK_PORTAL)
            }
        }

        //对垂直间距进行修正
        gridView.post {
            val height = gridView.height
            val padding = gridView.paddingBottom + gridView.paddingTop
            val item = gridView.getChildAt(0).height
            val space = (height - 3 * item - padding) / 2
            if (space > 0)
                gridView.verticalSpacing = space
            else
                gridView.verticalSpacing = 0
        }
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (BaseAPP.isLogin()) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能使用${msg}哦~"))
        }
    }

    companion object {
        private val TAG = LogUtils.makeLogTag(ExploreFragment::class.java)
    }
}
