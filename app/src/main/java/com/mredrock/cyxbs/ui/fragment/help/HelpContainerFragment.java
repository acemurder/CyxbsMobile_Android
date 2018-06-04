package com.mredrock.cyxbs.ui.fragment.help;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mredrock.cyxbs.BaseAPP;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.User;
import com.mredrock.cyxbs.model.social.PersonInfo;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.subscriber.SimpleObserver;
import com.mredrock.cyxbs.subscriber.SubscriberListener;
import com.mredrock.cyxbs.ui.adapter.TabPagerAdapter;
import com.mredrock.cyxbs.ui.fragment.BaseFragment;
import com.mredrock.cyxbs.ui.fragment.social.BBDDNewsFragment;
import com.mredrock.cyxbs.ui.fragment.social.HotNewsFragment;
import com.mredrock.cyxbs.ui.fragment.social.OfficialFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpContainerFragment extends BaseFragment {
    @BindView(R.id.question_TabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.question_ViewPager)
    ViewPager mViewPager;
    private boolean firstLogin = false;
    private int resumenCount = 0;

    private User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_container, container, false);
        ButterKnife.bind(this, view);
        getUserData();
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstLogin && resumenCount == 1) {
            firstLogin = false;
            getUserData();
        }
        ++resumenCount;
    }

    private void getUserData() {
        if (BaseAPP.isLogin()) {
            mUser = BaseAPP.getUser(getContext());
            if (mUser.id == null) getPersonInfoData();
            else init();
        } else {
            firstLogin = true;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getPersonInfoData() {
        if (!BaseAPP.isLogin()) {
            return;
        }
        if (mUser != null) {
            RequestManager.getInstance().getPersonInfo(new SimpleObserver<>(getActivity(), new SubscriberListener<PersonInfo>() {
                @Override
                public void onNext(PersonInfo personInfo) {
                    super.onNext(personInfo);
                    super.onNext(personInfo);
                    mUser = User.cloneFromUserInfo(mUser, personInfo);
                    BaseAPP.setUser(getActivity(), mUser);
                }
            }), mUser.stuNum, mUser.stuNum, mUser.idNum);
        }

    }

    private void init() {
        List<Fragment> fragmentList = new ArrayList<>();
        AllHelpFragment mAllHelpFragment = new AllHelpFragment();
        LearnHelpFragment mLearnHelpFragment = new LearnHelpFragment();
        EmotionHelpFragment mEmotionFragment = new EmotionHelpFragment();
        LifeHelpFragment mLifeHelpFragment = new LifeHelpFragment();

        fragmentList.add(mAllHelpFragment);
        fragmentList.add(mLearnHelpFragment);
        fragmentList.add(mEmotionFragment);
        fragmentList.add(mLifeHelpFragment);

        TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager(), fragmentList, Arrays
                .asList(getActivity().getResources().getStringArray(R.array.question_tab_titles)));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragmentList.size());

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
