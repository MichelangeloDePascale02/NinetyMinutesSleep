package com.swdp31plus.ninetyminutessleep.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.Sound;
import com.swdp31plus.ninetyminutessleep.ui.main.FirstFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.ViewHolder> {

    private ArrayList<Sound> soundsList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private LayoutInflater layoutInflater;
    private int[] playingStatuses;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_box, null));
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sound sound = soundsList.get(position);
        holder.layoutSongBoxTxt.setText(sound.getTitle());

        holder.layoutSoundBoxCardview.setOnClickListener(v -> {
            if(playingStatuses[position] == 0) {
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_stop_24);
                playingStatuses[position] = 1;
            } else {
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_play_arrow_24);
                playingStatuses[position] = 0;
            }
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(sound);
            }
        });
    }

    @Override
    public int getItemCount() {
        return soundsList.size();
    }

    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView layoutSongBoxTxt;
        ShapeableImageView layoutSongBoxImg;
        CardView layoutSoundBoxCardview;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            layoutSongBoxTxt = itemView.findViewById(R.id.layout_song_box_text);
            layoutSongBoxImg = itemView.findViewById(R.id.layout_song_box_image);
            layoutSoundBoxCardview = itemView.findViewById(R.id.layout_song_box_cardview);
        }
    }

    public SoundsAdapter() {
        soundsList = new ArrayList<>();
    }
    public void add(Sound sound) {
        soundsList.add(sound);
    }

    public void addAll(ArrayList<Sound> sounds) {
        soundsList.addAll(sounds);
    }
    public void remove(Sound sound) {
        soundsList.remove(sound);
    }
    public void removeAll(){
        soundsList.clear();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Sound sound);
    }

    public void createPlayingIndex(int size){
        playingStatuses = new int[size];
        initializePlayingIndex();
    }

    public void initializePlayingIndex() {
        Arrays.fill(playingStatuses, 0);
    }
}
