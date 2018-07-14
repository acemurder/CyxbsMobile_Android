package com.redrock.schedule.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redrock.common.ContextProvider;
import com.redrock.common.network.SimpleObserver;
import com.redrock.common.network.SubscriberListener;
import com.redrock.common.ui.BaseFragment;
import com.redrock.common.util.LogUtils;
import com.redrock.common.util.SPUtils;
import com.redrock.common.util.SchoolCalendar;
import com.redrock.schedule.R;
import com.redrock.schedule.R2;
import com.redrock.schedule.event.ForceFetchCourseEvent;
import com.redrock.schedule.network.ScheduleRequestManager;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class CourseContainerFragment extends BaseFragment {

    public static final String TAG = "CourseContainerFragment";

    public static final String SP_COLUMN_LAUNCH = "CourseContainerFragment_first_time_launch";

    @BindView(R2.id.tab_course_tabs)
    TabLayout mTabs;
    @BindView(R2.id.tab_course_viewpager)
    ViewPager mPager;

    @OnClick(R2.id.course_fab)
    void clickToExchange() {
        changeCurrentItem();
    }

    private TextView mToolbarTitle;
    private String title;

    private TabPagerAdapter mAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private int mNowWeek = new SchoolCalendar().getWeekOfTerm();

    private ViewPager.OnPageChangeListener mPageListener;
    private ViewPager.OnPageChangeListener mTabListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mTitles = new ArrayList<>();
        mTitles.addAll(Arrays.asList(getResources().getStringArray(R.array.titles_weeks)));
        //mNowWeek = new SchoolCalendar().getWeekOfTerm();
        LogUtils.LOGI(TAG, "NowWeek : " + mNowWeek);
        if (mNowWeek <= 18 && mNowWeek >= 1) {
            mTitles.set(mNowWeek, getActivity().getResources().getString(R.string.now_week));
        }
        if (mFragmentList.isEmpty()) {
            for (int i = 0; i < mTitles.size(); i++) {
                CourseFragment temp = new CourseFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(CourseFragment.BUNDLE_KEY, i);
                temp.setArguments(bundle);
                mFragmentList.add(temp);
            }
        }
    }

    public void saveInfoToSP() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean(SP_COLUMN_LAUNCH, false);
        editor.apply();
    }

    static ObservableEmitter<String> emitter;

    static public Observable<String> toolbarTitle = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> e) {
            emitter = e;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_container, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new TabPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList, mTitles);
        mPager.setAdapter(mAdapter);
//        mPager.setOffscreenPageLimit(mTitles.size());
        mPager.addOnPageChangeListener(mTabListener = new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mPager.addOnPageChangeListener(mPageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title = mTitles.get(position);
                emitter.onNext(title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabs.setupWithViewPager(mPager);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabs.setVisibility(View.GONE);
        return view;
    }

    static ObservableEmitter<Pair<Boolean,Boolean>> emitter2;
    public static Observable<Pair<Boolean,Boolean>> courseUnfoldState = Observable.create(new ObservableOnSubscribe<Pair<Boolean, Boolean>>() {
        @Override
        public void subscribe(ObservableEmitter<Pair<Boolean, Boolean>> e) {
            emitter2 = e;
        }
    });

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mNowWeek <= 18 && mNowWeek >= 1) setCurrentItem(mNowWeek);
        if (mToolbarTitle != null) {
            mToolbarTitle.setOnClickListener(v -> {
                if (isVisible()) {
                    if (mTabs.getVisibility() == View.VISIBLE) {
                        mTabs.setVisibility(View.GONE);
                        emitter2.onNext(new Pair<>(true, false));
                    } else {
                        mTabs.setVisibility(View.VISIBLE);
                        mTabs.setScrollPosition(mPager.getCurrentItem(), 0, true);
                        emitter2.onNext(new Pair<>(true, true));
                    }
                }
            });
        }
        loadNowWeek();
        //remindFn(view);
    }

    public void forceFetchCourse() {
        //FIXME: When reinstall，mPager sometimes is null,but the Course View shows normally。
        if (mPager != null) {
            int week = mPager.getCurrentItem();
            EventBus.getDefault().post(new ForceFetchCourseEvent(week));
        }
    }

    @Override
    public void onDestroyView() {
        mPager.removeOnPageChangeListener(mTabListener);
        mPager.removeOnPageChangeListener(mPageListener);
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setCurrentItem(int position) {
        if (mPager != null) {
            mPager.setCurrentItem(position, true);
        }
    }

    private void changeCurrentItem() {
        int position = mPager.getCurrentItem();
        if (position != mNowWeek) {
            setCurrentItem(mNowWeek);
        } else if (position != 0) {
            setCurrentItem(0);
        }
    }

    private void loadNowWeek() {
        ScheduleRequestManager.INSTANCE.getNowWeek(new SimpleObserver<>(ContextProvider.getContext(), new SubscriberListener<Integer>() {
            @Override
            public void onNext(Integer i) {
                int nowWeek = i;
                Log.d(TAG, "onNext: now week: " + i);
                updateFirstDay(nowWeek);
                if (mNowWeek <= 18 && mNowWeek >= 1) {
                    setCurrentItem(mNowWeek);
                }
            }
        }), "2013214151", "");
    }

    private void updateFirstDay(int nowWeek) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -((nowWeek - 1) * 7 + (now.get(Calendar.DAY_OF_WEEK) + 5) % 7));
        SPUtils.set(ContextProvider.getContext(), "first_day", now.getTimeInMillis());
        mNowWeek = new SchoolCalendar().getWeekOfTerm();
    }

    public String getTitle() {
        return title == null ? "课表" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}