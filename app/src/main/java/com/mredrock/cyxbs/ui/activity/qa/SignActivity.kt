package com.mredrock.cyxbs.ui.activity.qa

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.model.qa.CheckInStatus
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.network.error.QAErrorHandler
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.util.ImageLoader
import com.mredrock.cyxbs.util.extensions.gone
import com.mredrock.cyxbs.util.extensions.visible
import kotlinx.android.synthetic.main.activity_sign.*
import kotlinx.android.synthetic.main.popup_window_sign_menu.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity

class SignActivity : BaseActivity() {
    val dayNames = arrayOf(1, 2, 3, 4, 5, 6, 7)
    val dayPoints = arrayOf(10, 10, 20, 10, 30, 10, 40)

    private val menuContentView: View by lazy { initMenuContentView() }
    private val popupWindow: PopupWindow by lazy {
        PopupWindow(menuContentView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                true).init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        initToolBar()
        initData()
        remindSwitch.setOnCheckedChangeListener { _, isChecked -> remindStatusChange(isChecked) }
        //todo get my point and show
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar)
        title = ""
        toolbar_title.text = "每日签到"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initData() {
        val user = BaseAPP.getUser(this)!!
        ImageLoader.getInstance().loadAvatar(user.photo_src, avatar)
        RequestManager.INSTANCE.getCheckInStatus(SimpleObserver(this, true, object : SubscriberListener<CheckInStatus>(QAErrorHandler) {
            override fun onNext(t: CheckInStatus) {
                super.onNext(t)
                initView(t)
            }
        }), user.stuNum, user.idNum)
    }

    @SuppressLint("SetTextI18n")
    private fun initView(status: CheckInStatus) {
        if (status.isChecked) {
            signedFrame.visible()
            toSignFrame.gone()
        } else {
            signedFrame.gone()
            toSignFrame.visible()
            sign.setOnClickListener { checkIn() }
        }

        dayCount.text = "${if (status.serialDays < 10) "0" else ""}${status.serialDays}"
        val serialDays = status.serialDays % 7
        val flag = status.checkedInt
        val range: IntRange
        val todayIndex: Int
        if (serialDays < 3 + flag || serialDays == 7) {
            range = 0..4
            todayIndex = serialDays - flag
        } else if (serialDays == 3 + flag) {
            range = 1..5
            todayIndex = 3
        } else {
            range = 2..6
            todayIndex = serialDays - flag
        }

        val checkedDrawable = resources.getDrawable(R.drawable.shape_circle_src_activity_sign)
        val todayColor = resources.getColor(R.color.sign_today_name)
        val otherDayColor = resources.getColor(R.color.sign_day_name)
        for ((j, i) in range.withIndex()) {
            val dayName = dayNameContainer.getChildAt(j) as TextView
            val dayPoint = dayPointContainer.getChildAt(j) as TextView
            dayName.text = "第${dayNames[i]}天"
            dayName.setTextColor(if (j == todayIndex) todayColor else otherDayColor)
            (daySymbolContainer.getChildAt(j * 2 + 1) as ImageView).setImageDrawable(if (i < serialDays) checkedDrawable else null)
            dayPoint.text = "+${dayPoints[i]}"
            dayPoint.setTextColor(if (j == todayIndex) todayColor else otherDayColor)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkIn() {
        val user = BaseAPP.getUser(this)!!
        RequestManager.INSTANCE.checkIn(SimpleObserver(this, true, object : SubscriberListener<Unit>(QAErrorHandler) {
            override fun onNext(t: Unit) {
                signedFrame.visible()
                toSignFrame.gone()
                (daySymbolContainer.getChildAt(5) as ImageView).setImageResource(R.drawable.shape_circle_src_activity_sign)
                val count = dayCount.text.toString().toInt() + 1
                dayCount.text = "${if (count < 10) "0" else ""}$count"
                super.onNext(t)
            }
        }), user.stuNum, user.idNum)
    }

    private fun remindStatusChange(isChecked: Boolean) {
        //todo remind sign
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
        val root = layoutInflater.inflate(R.layout.popup_window_sign_menu, null, false)
        root.rules.setOnClickListener {
            startActivity<SignRulesActivity>()
            popupWindow.dismiss()
        }
        root.spec.setOnClickListener {
            startActivity<PointSpecActivity>()
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
}
