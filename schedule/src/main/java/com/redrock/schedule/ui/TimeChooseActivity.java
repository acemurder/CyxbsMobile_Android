package com.redrock.schedule.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redrock.common.ContextProvider;
import com.redrock.common.ui.BaseActivity;
import com.redrock.common.util.DensityUtils;
import com.redrock.common.util.LogUtils;
import com.redrock.schedule.event.TimeChooseEvent;
import com.redrock.schedule.model.Position;
import com.redrock.schedule.R;
import com.redrock.schedule.R2;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TimeChooseActivity extends BaseActivity {


    public static final String BUNDLE_KEY = "TIME_CHOOSE";

    @BindView(R2.id.course_weeks)
    LinearLayout mCourseWeeks;
    @BindView(R2.id.course_weekday)
    LinearLayout mCourseWeekday;
    @BindView(R2.id.course_time)
    LinearLayout mCourseTime;
    @BindView(R2.id.course_schedule_holder)
    LinearLayout mCourseScheduleHolder;

    @BindView(R2.id.time_choose_content)
    TimeChooseView timeChooseView;



    @OnClick({R2.id.choose_time_iv_back,R2.id.time_choose_iv_ok})
    public void onTitleClick(View v){
        if (v.getId() == R.id.choose_time_iv_back)
            onBackPressed();
        else
            getPosition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_choose);
        ButterKnife.bind(this);
        initWeekView();
    }


    public void initWeekView() {
        String[] date = getResources().getStringArray(R.array.course_weekdays);
        int screeHeight = DensityUtils.getScreenHeight(this);


        int height = (screeHeight - findViewById(R.id.affair_toolbar).getHeight() - mCourseWeeks.getHeight()) / 6 * 5 - DensityUtils.dp2px(this,3);

        mCourseTime.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dp2px(this, 40), height));
        timeChooseView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        for (int i = 0; i < 7; i++) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            tv.setLayoutParams(params);
            tv.setText(date[i]);
            tv.setGravity(Gravity.CENTER);
            mCourseWeeks.addView(tv);
        }
        for (int i = 0; i < 12; i++) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            tv.setBackgroundColor(Color.parseColor("#f6f6f6"));
            String courseNumber = i + 1 + "";
            tv.setText(courseNumber);
            tv.setGravity(Gravity.CENTER);
            mCourseTime.addView(tv);
        }
    }

    public void getPosition() {
        ArrayList<Position> positions = new ArrayList<>();
        positions.addAll(timeChooseView.getPositions());
        if (positions.size() == 0){
            Toast.makeText(ContextProvider.getContext(),"还没有选择时间哦",Toast.LENGTH_SHORT).show();
        }else {
            Collections.sort(positions);
            for (Position p : positions)
                LogUtils.LOGD(this.getClass().getSimpleName(),p.toString());
            EventBus.getDefault().post(new TimeChooseEvent(positions));
            onBackPressed();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ArrayList<Position> pos = (ArrayList<Position>) getIntent().getSerializableExtra(BUNDLE_KEY);
        if (pos.size() != 0)
            timeChooseView.setPositions(pos);
    }
}
