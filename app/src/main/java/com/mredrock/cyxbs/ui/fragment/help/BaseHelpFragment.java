package com.mredrock.cyxbs.ui.fragment.help;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.mredrock.cyxbs.BaseAPP;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.event.LoginStateChangeEvent;
import com.mredrock.cyxbs.model.help.Question;
import com.mredrock.cyxbs.model.social.HotNews;
import com.mredrock.cyxbs.model.social.HotNewsContent;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.subscriber.EndlessRecyclerOnScrollListener;
import com.mredrock.cyxbs.subscriber.SimpleObserver;
import com.mredrock.cyxbs.subscriber.SubscriberListener;
import com.mredrock.cyxbs.ui.activity.social.FooterViewWrapper;
import com.mredrock.cyxbs.ui.activity.social.HeaderViewWrapper;
import com.mredrock.cyxbs.ui.activity.social.PostNewsActivity;
import com.mredrock.cyxbs.ui.adapter.HeaderViewRecyclerAdapter;
import com.mredrock.cyxbs.ui.adapter.HelpAdapter;
import com.mredrock.cyxbs.ui.adapter.NewsAdapter;
import com.mredrock.cyxbs.ui.fragment.BaseLazyFragment;
import com.mredrock.cyxbs.ui.fragment.social.BaseNewsFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;

import static com.mredrock.cyxbs.ui.activity.lost.ReleaseActivity.getTime;
import static com.taobao.accs.client.AccsConfig.build;

/**
 * Created by yan on 2018/2/20.
 */

public abstract class BaseHelpFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int PER_PAGE_NUM = 6;
    public static final String TAG = "BaseHelpFragment";
    public static final int FIRST_PAGE_INDEX = 1;
    public int type = HelpAdapter.TYPE_EMOTION;

    private boolean hasLoginStateChanged = false;

    @BindView(R.id.fab_main)
    FloatingActionButton mFabMain;
    @BindView(R.id.information_RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.information_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    public int currentIndex = 0;
    private List<Question> mListHotNews;
    private FooterViewWrapper mFooterViewWrapper;

    protected HelpAdapter mHelpAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    abstract void provideData(SimpleObserver<List<Question>> observer, int size, int page);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void init() {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorAccent),
                ContextCompat.getColor(BaseAPP.getContext(), R.color.colorPrimary)
        );
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mFabMain.setOnClickListener(view1 -> {
            if (BaseAPP.getUser(getActivity()).id == null || BaseAPP.getUser(getActivity()).id.equals("0")) {
                RequestManager.getInstance().checkWithUserId("还没有完善信息，不能发动态哟！");
            } else
                popBottomDialog();
        });
        mLinearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity());

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        addOnScrollListener();
        initAdapter(null);
    }

    public void getCurrentData(int size, int page) {
        mSwipeRefreshLayout.post(this::showLoadingProgress);
        provideData(new SimpleObserver<>(getActivity(), new SubscriberListener<List<Question>>() {
            @Override
            public boolean onError(Throwable e) {
                super.onError(e);
                mFooterViewWrapper.showLoadingFailed();
                closeLoadingProgress();
                return false;
            }

            @Override
            public void onNext(List<Question> Questions) {
                super.onNext(Questions);
                if (mListHotNews == null) {
                    initAdapter(Questions);
                    if (Questions.size() == 0);
                        mFooterViewWrapper.showLoadingNoData();
                } else mHelpAdapter.replaceDataList(Questions);
                Log.i("====>>>", "page===>>>" + page + "size==>>" + Questions.size());
                closeLoadingProgress();
            }
        }), size, page);
    }

    private void getNextPageData(int size, int page) {
        mFooterViewWrapper.showLoading();
        provideData(new SimpleObserver<>(getContext(), new SubscriberListener<List<Question>>() {
            @Override
            public boolean onError(Throwable e) {
                super.onError(e);
                mFooterViewWrapper.showLoadingFailed();
                return true;
            }

            @Override
            public void onNext(List<Question> questions) {
                super.onNext(questions);
                if (questions.size() == 0) {
                    mFooterViewWrapper.showLoadingNoMoreData();
                    return;
                }
                mHelpAdapter.addDataList(questions);
                //Log.i("====>>>", "page===>>>" + page + "size==>>" + hotNewses.size());
            }
        }), size, page);
    }

    @Override
    protected void onFirstUserVisible() {
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX);
    }

    private void addOnScrollListener() {
        if (endlessRecyclerOnScrollListener != null)
            mRecyclerView.removeOnScrollListener(endlessRecyclerOnScrollListener);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                currentIndex++;
                getNextPageData(PER_PAGE_NUM, currentIndex);
            }

            @Override
            public void onShow() {

            }

            @Override
            public void onHide() {

            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    @Override
    public void onRefresh() {
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX);
        currentIndex = 0;
        addOnScrollListener();

    }

    private void showLoadingProgress() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void closeLoadingProgress() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void initAdapter(List<Question> questions) {
        if (mRecyclerView == null) return;  // prevent it be called before lazy loading
        mListHotNews = questions;
        mHelpAdapter = new HelpAdapter(questions, type);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mHelpAdapter);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        addFooterView(mHeaderViewRecyclerAdapter);
        mFooterViewWrapper.getCircleProgressBar().setVisibility(View.INVISIBLE);
    }

    private void addFooterView(HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter) {
        mFooterViewWrapper = new FooterViewWrapper(mRecyclerView);
        mHeaderViewRecyclerAdapter.addFooterView(mFooterViewWrapper.getFooterView());
        mFooterViewWrapper.onFailedClick(view -> {
            if (currentIndex == 0) getCurrentData(PER_PAGE_NUM, currentIndex);
            getNextPageData(PER_PAGE_NUM, currentIndex);
        });
    }

    private void popBottomDialog() {
        Dialog dialog = new SelectTypeDialog(getContext(), R.style.BottomDialogTheme);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomPopupAnimation);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasLoginStateChanged) {
            getCurrentData(PER_PAGE_NUM, 0);
            hasLoginStateChanged = false;
        }
    }

    @Override
    public void onLoginStateChangeEvent(LoginStateChangeEvent event) {
        super.onLoginStateChangeEvent(event);
        hasLoginStateChanged = true;
    }
}
