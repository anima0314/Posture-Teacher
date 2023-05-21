package com.gnupr.postureteacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Stats2Adapter extends RecyclerView.Adapter<Stats2Adapter.ViewHoler>{

    private ArrayList<Stats2Model> arrayList;

    public Stats2Adapter(ArrayList<Stats2Model> arrayList) {
        this.arrayList = arrayList;
    }

    // 클릭 이벤트하려고 만든 부분
    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }
    public Stats2Adapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(Stats2Adapter.OnItemClickListener listener){
        this.mListener = listener;
    }
    //

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
        holder.tv_time.setText("한회당 측정시간 : "+arrayList.get(pos-position).getCycletime());
        holder.tv_percent.setText("목표대비 달성율 : "+arrayList.get(pos-position).getPercent());
        holder.tv_unstable.setText("실제 측정 횟수 : "+arrayList.get(pos-position).getLaps());
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {
        //xml id는 수정 못함
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
            itemClick(itemView);
        }

        private void itemClick(View itemView){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = arrayList.size()-1;
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v,position);
                    }
                }
            });
        }
    }
}
