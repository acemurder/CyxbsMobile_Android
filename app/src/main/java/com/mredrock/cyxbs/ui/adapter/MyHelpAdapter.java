package com.mredrock.cyxbs.ui.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.help.MyQuestion;
import com.mredrock.cyxbs.model.help.Question;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yan on 2018/5/23.
 */

public class MyHelpAdapter extends RecyclerView.Adapter<MyHelpAdapter.ViewHolder> {
    private ArrayList<MyQuestion> list;
    private SimpleDateFormat formatToData;
    private SimpleDateFormat formatToDay;
    private SimpleDateFormat formatToHour;

    public static final int TYPE_HELP_ADOPTED = 0;
    public static final int TYPE_HELP_UNADOPTED = 1;
    public static final int TYPE_ASK_ADOPTED = 2;
    public static final int TYPE_ASK_UNADOPTED = 3;

    private int type;

    public MyHelpAdapter(ArrayList<MyQuestion> list, int type) {
        formatToData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatToDay = new SimpleDateFormat("yyyy-MM-dd");
        formatToHour = new SimpleDateFormat("HH:mm");
        this.list = new ArrayList<>();
        if (list != null ) this.list.addAll(list);
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_help, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyQuestion question = list.get(position);
        holder.mAnswer.setText("帮助：" + question.content);
        holder.mQuestion.setText("提问：" + question.question_title);

        if (type == TYPE_HELP_ADOPTED) {
            holder.mTime.setText("采纳时间: " + changeTime(question.updated_at));
        } else if (type == TYPE_ASK_ADOPTED) {
            holder.mTime.setText("提问时间: " + changeTime(question.created_at));
        } else {
            holder.mTime.setText("发布时间: " + changeTime(question.created_at));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.red_point)
        ImageView mPoint;
        @BindView(R.id.tv_question)
        TextView mQuestion;
        @BindView(R.id.tv_answer)
        TextView mAnswer;
        @BindView(R.id.tv_time)
        TextView mTime;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addDataList(List<MyQuestion> questions) {
        list.addAll(questions);
        notifyItemRangeInserted(list.size(), questions.size());
    }

    public void replaceDataList(List<MyQuestion> questions) {
        list.clear();
        list.addAll(questions);
        notifyDataSetChanged();
    }


    @SuppressLint("SimpleDateFormat")
    private String changeTime(String time) {
        Date disappearDate = null;
        try {
            disappearDate = formatToData.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "error";
        }
        Date now = new Date();
        long t = now.getTime() - disappearDate.getTime();
        long day = t / (24 * 60 * 60 * 1000);
        long hour = (t / (60 * 60 * 1000) - day * 24);
        long min = ((t / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long sec = (t / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day > 0) {
            return formatToDay.format(disappearDate);
        }  else if (hour > 0) {
            return formatToHour.format(disappearDate);
        } else {
            return "刚刚";
        }
    }
}
