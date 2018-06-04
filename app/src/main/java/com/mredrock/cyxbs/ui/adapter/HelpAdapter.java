package com.mredrock.cyxbs.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.help.Question;
import com.mredrock.cyxbs.model.social.Image;
import com.mredrock.cyxbs.ui.activity.qa.QuestionDetailActivity;
import com.mredrock.cyxbs.util.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

/**
 * Created by yan on 2018/2/20.
 */

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {
    public static int TYPE_EMOTION = 0;
    public static int TYPE_OTHER = 1;

    private int type;
    private List<Question> mQuestions;

    public HelpAdapter(List<Question> mQuestions, int type) {
        if (mQuestions == null) mQuestions = new ArrayList<>();
        this.mQuestions = mQuestions;
        this.type = type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "HelpAdapter.ViewHolder";
        public View itemView;

        private Question mQuestion;

        private SimpleDateFormat formatToData;
        private SimpleDateFormat formatToMonth;
        private SimpleDateFormat formatToDay;
        private SimpleDateFormat formatToHour;


        @BindView(R.id.list_help_img_avatar)
        public ImageView mImgAvatar;
        @BindView(R.id.list_help_text_nickname)
        public TextView mNickName;
        @BindView(R.id.list_help_text_time)
        public TextView mTextTime;
        @BindView(R.id.list_help_title)
        public TextView mTitle;
        @BindView(R.id.list_help_description)
        public TextView mDescription;
        @BindView(R.id.list_help_rewards)
        public TextView mRewards;
        @BindView(R.id.list_help_tag)
        public TextView mTag;
        @BindView(R.id.img_sex)
        ImageView mImgSex;


        public boolean enableAvatarClick = true;
        public boolean isFromPersonInfo = false;
        public boolean isFromMyTrend = false;

        private boolean isSingle = false;
        private Disposable mDisposable;
        private Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.itemView = itemView;
            this.context = context;
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.list_help_img_avatar)
        public void takeToPersonInfoActivity(View view) {

        }

        @OnClick(R.id.help_item_card_view)
        public void onItemClick(View view) {
            QuestionDetailActivity.start(view.getContext(), mQuestion.getId() + "");
        }


        public final static String[] getUrls(String url) {
            return url != null ? url.split(",") : new String[]{""};
        }

        public final static List<Image> getImageList(String[] urls) {
            List<Image> mImgList = new ArrayList<>();
            for (String url : urls)
                if (!url.equals("")) mImgList.add(new Image(url, Image.TYPE_ADD));
            return mImgList;
        }

        public void setData(Question mQuestion, int type) {
            this.mQuestion = mQuestion;
            mNickName.setText(mQuestion.getNickname());
            mTag.setText("#" + mQuestion.getTags() + "#");
            mTitle.setText(mQuestion.getTitle());
            mDescription.setText(mQuestion.getDescription());
            mTextTime.setText(changeTime(mQuestion.getDisappear_at()));
            mRewards.setText(mQuestion.getReward() + "积分");
            if (mQuestion.getPhoto_thumbnail_src() != "")
                ImageLoader.getInstance().loadAvatar((String) mQuestion.getPhoto_thumbnail_src(), mImgAvatar);

            if (type == TYPE_EMOTION) {
                if (mQuestion.getGender().equals("女")) {
                    Glide.with(context).load(R.drawable.img_help_sex_girl).into(mImgSex);
                } else if (mQuestion.getGender().equals("男")) {
                    Glide.with(context).load(R.drawable.img_help_sex_boy).into(mImgSex);
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private String changeTime(String time) {
            if (formatToData == null || formatToHour == null) {
                formatToData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatToMonth = new SimpleDateFormat("MM月dd天HH小时后消失");
                formatToDay = new SimpleDateFormat("dd天HH小时后消失");
                formatToHour = new SimpleDateFormat("HH小时后消失");
            }

            Date disappearDate = null;
            try {
                disappearDate = formatToData.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
                return "error";
            }
            Date now = new Date();
            long t = disappearDate.getTime() - now.getTime();
            long day = t / (24 * 60 * 60 * 1000);
            long hour = (t / (60 * 60 * 1000) - day * 24);
            long min = ((t / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long sec = (t / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (day > 0) {
                return day + "天" + hour + "小时后消失";
            } else if (hour > 0) {
                return hour + "小时" + min + "分钟后消失";
            } else {
                return min + "分钟" + sec + "秒后消失";
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HelpAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_question, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = mQuestions.get(position);
        holder.setData(question, type);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public void addDataList(List<Question> questions) {
        mQuestions.addAll(questions);
        notifyItemRangeInserted(mQuestions.size(), questions.size());
    }

    public void replaceDataList(List<Question> questions) {
        mQuestions.clear();
        mQuestions.addAll(questions);
        notifyDataSetChanged();
    }
}
