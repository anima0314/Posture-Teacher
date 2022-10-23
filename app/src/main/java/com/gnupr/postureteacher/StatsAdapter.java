package com.gnupr.postureteacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHoler>{

    private ArrayList<StatsModel> arrayList;

    public StatsAdapter(ArrayList<StatsModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats,parent,false);
        ViewHoler holder = new ViewHoler(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        int pos = arrayList.size()-1;
        holder.tv_id.setText("측정 id : " + arrayList.get(pos-position).getId() + "번");
        holder.tv_time.setText("총 측정시간 : "+arrayList.get(pos-position).getTime());
        holder.tv_percent.setText("올바른 자세 비율 : "+arrayList.get(pos-position).getPercent());
        holder.tv_unstable.setText("자세가 불안정해진 시간 : "+arrayList.get(pos-position).getUnstable());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {
        protected TextView tv_id;
        protected TextView tv_time;
        protected TextView tv_percent;
        protected TextView tv_unstable;
        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            this.tv_id = itemView.findViewById(R.id.stats_id);
            this.tv_time = itemView.findViewById(R.id.stats_time);
            this.tv_percent = itemView.findViewById(R.id.stats_percent);
            this.tv_unstable = itemView.findViewById(R.id.stats_unstable);
        }
    }
}
