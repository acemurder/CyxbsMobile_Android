package com.mredrock.cyxbs.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mredrock.cyxbs.R;
import com.redrock.common.account.AccountManager;
import com.redrock.common.ui.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by simonla on 2016/8/9.
 * Have a good day.
 */
public class UnLoginFragment extends BaseFragment {
    @BindView(R.id.tv_login)
    TextView mTvLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unlogin, null);
        ButterKnife.bind(this, view);
        if (AccountManager.isFresh()) {
            mTvLogin.setText("萌新泥壕，课表菌正在从教务处搬运你的课表哟，请耐心等待");
            mTvLogin.setTextColor(getResources().getColor(R.color.gray_edit));
        } else {
            mTvLogin.setOnClickListener(v ->
                    ARouter.getInstance().build("/account/LoginActivity").navigation());
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
