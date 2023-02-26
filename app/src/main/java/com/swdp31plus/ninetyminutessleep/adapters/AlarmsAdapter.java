package com.swdp31plus.ninetyminutessleep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;

import java.util.ArrayList;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {

    private ArrayList<Alarm> alarmsList;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_alarm_ribbon, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmsList.get(position);
        holder.layoutAlarmRibbonTimeTxt.setText(alarm.getTime());
        holder.layoutAlarmRibbonCardview.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView layoutAlarmRibbonTimeTxt;

        CardView layoutAlarmRibbonCardview;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            layoutAlarmRibbonTimeTxt = itemView.findViewById(R.id.layout_alarm_ribbon_time_text);
            layoutAlarmRibbonCardview = itemView.findViewById(R.id.layout_alarm_ribbon_cardview);
        }
    }

    public AlarmsAdapter() {
        alarmsList = new ArrayList<>();
    }
    public void add(Alarm alarm) {
        alarmsList.add(alarm);
    }

    public void addAll(ArrayList<Alarm> alarms) {
        alarmsList.addAll(alarms);
    }
    public void remove(Alarm alarm) {
        alarmsList.remove(alarm);
    }
    public void removeAll(){
        alarmsList.clear();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Alarm alarm);
    }
}
