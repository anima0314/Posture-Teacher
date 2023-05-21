package com.gnupr.postureteacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Stats2DetailAdapter extends RecyclerView.Adapter<Stats2DetailAdapter.ViewHoler>{

    private ArrayList<Stats2DetailModel> arrayList;

    public Stats2DetailAdapter(ArrayList<Stats2DetailModel> arrayList) {
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats_detail,parent,false);
        ViewHoler holder = new ViewHoler(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Stats2DetailAdapter.ViewHoler holder, int position) {
        holder.tv_id.setText(arrayList.get(position).getId() + "번째 플랭크자세");
        holder.tv_first_time.setText("자세가 올바른 시간 : "+arrayList.get(position).getTime());
        holder.tv_end_time.setText("올바른 자세 비율 : "+arrayList.get(position).getPercent());
        }

    @Override
    public int getItemCount() { return arrayList.size(); }

    public class ViewHoler extends RecyclerView.ViewHolder {
        //xml id는 수정 못함
        protected TextView tv_id;
        protected TextView tv_first_time;
        protected TextView tv_end_time;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            this.tv_id = itemView.findViewById(R.id.stats_detail_id);
            this.tv_first_time = itemView.findViewById(R.id.stats_first_time);
            this.tv_end_time = itemView.findViewById(R.id.stats_end_time);
        }
    }
}
