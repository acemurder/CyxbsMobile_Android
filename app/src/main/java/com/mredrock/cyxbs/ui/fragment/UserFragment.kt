package com.mredrock.cyxbs.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.BaseAPP
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.event.AskLoginEvent
import com.mredrock.cyxbs.event.LoginStateChangeEvent
import com.mredrock.cyxbs.model.User
import com.mredrock.cyxbs.network.RequestManager
import com.mredrock.cyxbs.subscriber.SimpleObserver
import com.mredrock.cyxbs.subscriber.SubscriberListener
import com.mredrock.cyxbs.ui.activity.me.EditInfoActivity
import com.mredrock.cyxbs.ui.activity.me.SettingActivity
import com.mredrock.cyxbs.ui.activity.me.StoreActivity
import com.mredrock.cyxbs.ui.activity.qa.DraftActivity
import com.mredrock.cyxbs.util.ImageLoader
import kotlinx.android.synthetic.main.fragment_user.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.startActivity

/**
 * 我的页面
 */
class UserFragment : BaseFragment() {

    private var mUser: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_user, container, false)!!

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPersonInfoData()
        //todo 页面跳转
        dailySign.setOnClickListener { checkLoginBeforeAction("签到") { } }
        store.setOnClickListener { checkLoginBeforeAction("积分商场") { startActivity<StoreActivity>() } }
        question.setOnClickListener { checkLoginBeforeAction("问一问") { } }
        help.setOnClickListener { checkLoginBeforeAction("帮一帮") { } }
        draft.setOnClickListener { checkLoginBeforeAction("草稿箱") { startActivity<DraftActivity>() } }
        relateMe.setOnClickListener { checkLoginBeforeAction("与我相关") { } }
        setting.setOnClickListener { startActivity<SettingActivity>() }

        val clickToEditInfo = View.OnClickListener { checkLoginBeforeAction("个人资料") { startActivity<EditInfoActivity>() } }
        avatar.setOnClickListener(clickToEditInfo)
        username.setOnClickListener(clickToEditInfo)
        introduce.setOnClickListener(clickToEditInfo)
    }

    private fun checkLoginBeforeAction(msg: String, action: () -> Unit) {
        if (BaseAPP.isLogin()) {
            action.invoke()
        } else {
            EventBus.getDefault().post(AskLoginEvent("请先登陆才能查看${msg}哦~"))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshEditLayout()
    }

    private fun getPersonInfoData() {
        if (!BaseAPP.isLogin()) {
            username.setText(R.string.fragment_user_empty_username)
            avatar.setImageResource(R.drawable.default_avatar)
            introduce.setText(R.string.fragment_user_empty_introduce)
            clearAllRemind()
            return
        }
        mUser = BaseAPP.getUser(activity)
        if (mUser != null) {
            RequestManager.getInstance().getPersonInfo(SimpleObserver(activity,
                    object : SubscriberListener<User>() {
                        override fun onNext(user: User?) {
                            super.onNext(user)
                            if (user != null) {
                                mUser = User.cloneFromUserInfo(mUser, user)
                                BaseAPP.setUser(activity, mUser)
                                refreshEditLayout()
                            }
                        }

                    }), mUser!!.stuNum, mUser!!.idNum)
        }
    }

    private fun clearAllRemind() {
        dailySign.isRemindIconShowing = false
        question.isRemindIconShowing = false
        help.isRemindIconShowing = false
        relateMe.isRemindIconShowing = false
        setting.isRemindIconShowing = false
    }


    private fun refreshEditLayout() {
        if (BaseAPP.isLogin()) {
            mUser = BaseAPP.getUser(activity)
            ImageLoader.getInstance().loadAvatar(mUser!!.photo_thumbnail_src, avatar)
            username.text = if (mUser!!.nickname.isBlank()) getString(R.string.fragment_user_empty_username) else mUser!!.nickname
            introduce.text = if (mUser!!.introduction.isBlank()) getString(R.string.fragment_user_empty_introduce) else mUser!!.introduction
        } else {
            username.setText(R.string.fragment_user_empty_username)
            avatar.setImageResource(R.drawable.default_avatar)
            introduce.setText(R.string.fragment_user_empty_introduce)
        }
    }

    override fun onLoginStateChangeEvent(event: LoginStateChangeEvent) {
        super.onLoginStateChangeEvent(event)
        refreshEditLayout()
    }
}
