package com.mredrock.cyxbs.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.help.Question;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yan on 2018/5/23.
 */

public class MyHelpAdapter extends RecyclerView.Adapter<MyHelpAdapter.ViewHolder> {
    private ArrayList<Question> list;

    public MyHelpAdapter(ArrayList<Question> list) {
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_help, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = list.get(position);
        holder.mAnswer.setText(question.getTitle());
        holder.mQuestion.setText(question.getTags());
        holder.mTime.setText(question.getDisappear_at());
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
}
