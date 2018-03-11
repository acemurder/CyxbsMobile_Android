package com.mredrock.cyxbs.electric.ui;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mredrock.cyxbs.common.base.BaseDataBindingActivity;
import com.mredrock.cyxbs.common.base.adapter.DataBoundAdapter;
import com.mredrock.cyxbs.common.base.adapter.DataBoundViewHolder;
import com.mredrock.cyxbs.common.data.DataBus;
import com.mredrock.cyxbs.common.util.SPUtils;
import com.mredrock.cyxbs.electric.R;
import com.mredrock.cyxbs.electric.databinding.ActivityDomitorySetBinding;
import com.mredrock.cyxbs.electric.databinding.ItemBuildingSelectBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by ：AceMurder
 * Created on ：2018/1/28
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class DormitorySetActivity extends BaseDataBindingActivity <DormitorySetPresenter, ActivityDomitorySetBinding>{

    private static final String TAG = "DormitorySetting";
    private BottomSheetBehavior bottomSheetBehavior;
    private BottomSheetDialog dialog;
    private int mBuildingPosition = -1;


    public static final String BUILDING_KEY = "building_position";
    public static final String DORMITORY_KEY = "dormitory_number";

    public static String[] sDormitoryBuildings;
    public static String[] sDormitoryBuildingsApi;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_domitory_set;
    }

    @Override
    public void viewCreated(@Nullable Bundle savedInstanceState) {

    }

    private void initView(){

        binding.buildingNumberEdit.setInputType(InputType.TYPE_NULL);

        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this)
                .inflate(R.layout.electric_bottom_dailog, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(recyclerView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        bottomSheetBehavior = BottomSheetBehavior.from((View) recyclerView.getParent());
//        bottomSheetBehavior.setPeekHeight((int) Utils.dp2PxInt(this, 250));
//
//        DormitorySettingActivity.DormitoryAdapter adapter = new DormitorySettingActivity.DormitoryAdapter();
//        adapter.setOnItemClickListener(((position) -> {
//            binding.buildingNumberEdit.setText(sDormitoryBuildings[position]);
//            mBuildingPosition = position;
//            dialog.dismiss();
//        }));
      //  recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int buildingPosition = (int) SPUtils.get(this, BUILDING_KEY, -1);
        if (buildingPosition < 0)
            return;
        binding.buildingNumberEdit.setText(sDormitoryBuildings[buildingPosition]);
        mBuildingPosition = buildingPosition;
        String dormitory = (String) SPUtils.get(DataBus.getContext(),DORMITORY_KEY,"");
        binding.dormitoryNumberEdit.setText(dormitory);
    }



    static class DormitorySelectAdapter extends DataBoundAdapter<ItemBuildingSelectBinding> {

        public DormitorySelectAdapter(int mLayoutId) {
            super(mLayoutId);
        }

        @Override
        public int getItemLayoutId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        protected void bindItem(@NotNull DataBoundViewHolder<? extends ItemBuildingSelectBinding> holder, int position, @NotNull List<?> payloads) {

        }
    }



    static class DormitoryAdapter extends RecyclerView.Adapter<DormitorySetActivity.DormitoryAdapter.Holder> {

        private DormitorySetActivity.DormitoryAdapter.OnItemClickListener mItemClickListener;

        public void setOnItemClickListener(DormitorySetActivity.DormitoryAdapter.OnItemClickListener li) {
            mItemClickListener = li;
        }

        @Override
        public DormitorySetActivity.DormitoryAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building, parent, false);
            return new DormitorySetActivity.DormitoryAdapter.Holder(item);
        }

        @Override
        public void onBindViewHolder(final DormitorySetActivity.DormitoryAdapter.Holder holder, int position) {
            holder.tv.setText(sDormitoryBuildings[position]);
            if(mItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(holder.getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return sDormitoryBuildings.length;
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tv;

            public Holder(View itemView) {
                super(itemView);
                tv =  itemView.findViewById(R.id.tv_item_building);
            }
        }

        interface OnItemClickListener {
            void onItemClick(int position);
        }
    }
}
