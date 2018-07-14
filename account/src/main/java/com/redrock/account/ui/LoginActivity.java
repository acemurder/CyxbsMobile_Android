package com.redrock.account.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.redrock.account.R;
import com.redrock.account.R2;
import com.redrock.account.network.AccountRequestManager;
import com.redrock.common.account.LoginStateChangeEvent;
import com.redrock.common.account.AccountManager;
import com.redrock.common.account.User;
import com.redrock.common.network.SimpleObserver;
import com.redrock.common.network.SubscriberListener;
import com.redrock.common.ui.BaseActivity;
import com.redrock.common.util.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path="/account/LoginActivity")
public class LoginActivity extends BaseActivity {
    @BindView(R2.id.login_stu_num_edit)
    AppCompatEditText stuNumEdit;
    @BindView(R2.id.login_id_num_edit)
    AppCompatEditText idNumEdit;
    @BindView(R2.id.login_submit_button)
    Button submitButton;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.toolbar_title)
    TextView toolbarTitle;

    public static final String TAG = "LoginActivity";

    @OnClick(R2.id.login_submit_button)
    void clickToLogin() {
        attemptLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initUser();
        autoSendFn();
        initToolbar();
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbarTitle.setText("登 录");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(v -> LoginActivity.this.finish());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    /**
     * 软键盘登录
     */
    private void autoSendFn() {
        idNumEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                attemptLogin();
            }
            return false;
        });
    }

    private void initUser() {
        AccountManager.setUser(null);
    }

    public void attemptLogin() {
        String stuNum = stuNumEdit.getText().toString();
        String idNum = idNumEdit.getText().toString();
        if ("".equals(idNum)) {
            Utils.toast(this, "请输入密码");
        }
        AccountRequestManager.INSTANCE
                .login(new SimpleObserver<>(this, true, false, new SubscriberListener<User>() {
                    @Override
                    public void onNext(User user) {
                        super.onNext(user);
                        if (user != null) {
                            AccountManager.setUser(user);
                            MobclickAgent.onProfileSignIn(stuNum);
                        } else {
                            Utils.toast(LoginActivity.this, "登录失败, 返回了信息为空");
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        EventBus.getDefault().post(new LoginStateChangeEvent(true));
                        finish();
                        if (!AccountManager.hasNickName()) {
                            ARouter.getInstance().build("/app/EditNickNameActivity").navigation();
                        }
                    }
                }), stuNum, idNum);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
