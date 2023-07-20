package com.swdp31plus.ninetyminutessleep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {

    private ArrayList<NewAlarm> alarmsList;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_alarm_ribbon, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewAlarm alarm = alarmsList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getAvailableLocales()[0]);
        String newAlarmTime = sdf.format(alarm.getTime());
        holder.layoutAlarmRibbonTimeTxt.setText(newAlarmTime);
        holder.layoutAlarmRibbonCardview.setOnClickListener(v -> {
            if (onItemClickListener != null) {
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
    public void add(NewAlarm alarm) {
        alarmsList.add(alarm);
    }

    public void addAll(ArrayList<NewAlarm> alarms) {
        alarmsList.addAll(alarms);
    }
    public void remove(NewAlarm alarm) {
        alarmsList.remove(alarm);
    }
    public void removeAll(){
        alarmsList.clear();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(NewAlarm alarm);
    }
}
