package com.gnupr.postureteacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StatsDetailAdapter extends RecyclerView.Adapter<StatsDetailAdapter.ViewHoler>{

    private ArrayList<StatsDetailModel> arrayList;

    public StatsDetailAdapter(ArrayList<StatsDetailModel> arrayList) {
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
    public void onBindViewHolder(@NonNull StatsDetailAdapter.ViewHoler holder, int position) {
        holder.tv_id.setText(arrayList.get(position).getId() + "번째 어긋난자세");
        holder.tv_first_time.setText("어긋나기 시작한 시간 : "+arrayList.get(position).getFirst_time());
        holder.tv_end_time.setText("자세가 회복된 시간 : "+arrayList.get(position).getEnd_time());
        holder.tv_total_time.setText("자세가 불안정한 시간 : "+arrayList.get(position).getTotal_time());
    }

    @Override
    public int getItemCount() { return arrayList.size(); }

    public class ViewHoler extends RecyclerView.ViewHolder {
        protected TextView tv_id;
        protected TextView tv_first_time;
        protected TextView tv_end_time;
        protected TextView tv_total_time;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            this.tv_id = itemView.findViewById(R.id.stats_detail_id);
            this.tv_first_time = itemView.findViewById(R.id.stats_first_time);
            this.tv_end_time = itemView.findViewById(R.id.stats_end_time);
            this.tv_total_time = itemView.findViewById(R.id.stats_total_time);
        }
    }
}
