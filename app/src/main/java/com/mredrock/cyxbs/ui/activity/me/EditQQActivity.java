package com.mredrock.cyxbs.ui.activity.me;

import com.redrock.common.config.Const;
import com.redrock.common.network.RedRockApiWrapper;
import com.mredrock.cyxbs.network.RequestManager;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.Observer;


public class EditQQActivity extends EditCommonActivity {


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void provideData(Observer<RedRockApiWrapper<Object>> observer, String
            stuNum, String idNum, String info) {
        RequestManager.getInstance().setPersonQQ(observer, stuNum, idNum, info);
    }


    @Override
    protected String getExtra() {
        return Const.Extras.EDIT_QQ;
    }
}
