package com.mredrock.cyxbs.ui.activity.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.component.widget.Toolbar;
import com.redrock.common.account.AccountManager;
import com.redrock.common.config.Const;
import com.redrock.common.network.RedRockApiWrapper;
import com.mredrock.cyxbs.network.RequestManager;
import com.redrock.common.util.Utils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import io.reactivex.Observer;

@Route(path="/app/EditNickNameActivity")
public class EditNickNameActivity extends EditCommonActivity {

    @BindView(R.id.edit_common_toolbar)
    Toolbar editCommonToolbar;

    @BindView(R.id.edit_common_et)
    EditText editCommonEt;

    boolean isForceModify;


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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void provideData(Observer<RedRockApiWrapper<Object>> observer, String stuNum, String idNum, String info) {
        RequestManager.getInstance().setPersonNickName(observer, stuNum, idNum, info);
    }

    private void initialize() {
        isForceModify = !AccountManager.hasNickName();
        if (isForceModify) {
            editCommonToolbar.setLeftText("");
            editCommonToolbar.setLeftTextListener(null);
        }
        editCommonToolbar.setRightTextListener(v -> {
            String input = editCommonEt.getText().toString().replaceAll("\\s", "");
            if (input.length() == 0) {
                Utils.toast(EditNickNameActivity.this, "你还没有输入昵称哟！");
            } else {
                editCommonEt.setText(input);
                EditNickNameActivity.super.setPersonInfo();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(editCommonEt, "要有昵称才能浏览哦~~~", Snackbar.LENGTH_SHORT).show();
        if (!isForceModify) {
            super.onBackPressed();
        }
    }

    @Override
    protected String getExtra() {
        return Const.Extras.EDIT_NICK_NAME;
    }
}
