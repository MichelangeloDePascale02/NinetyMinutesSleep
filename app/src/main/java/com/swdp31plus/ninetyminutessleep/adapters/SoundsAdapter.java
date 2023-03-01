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

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.ViewHolder> {

    private ArrayList<Sound> soundsList;
    private OnItemClickListener onItemClickListener;
    private MediaPlayer mp;
    private Context context;
    private LayoutInflater layoutInflater;

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
        holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_play_arrow_24);

        holder.layoutSoundBoxCardview.setOnClickListener(view -> {
            if (mp == null) {
                mp = MediaPlayer.create(context, sound.getSoundRes());
            }
            if (!mp.isPlaying()) {
                mp.start();
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_stop_24);
            } else {
                mp.stop();
                mp.reset();
                mp.release();
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        });
        holder.layoutSoundBoxCardview.setOnLongClickListener(view -> {
            /*Handler h = new Handler();
            if (mp == null) {
                mp = MediaPlayer.create(context, sound.getSoundRes());
            }
            if (!mp.isPlaying()) {
                Runnable stopPlaybackRun = () -> {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_play_arrow_24);
                };
                h.postDelayed(stopPlaybackRun, 5 * 1000);
                mp.start();
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_stop_24);
            } else {
                mp.stop();
                holder.layoutSongBoxImg.setImageResource(R.drawable.baseline_play_arrow_24);
            }*/

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            View dialogView = layoutInflater.inflate(R.layout.dialog_sound_timeout,null);
            // Set dialog title
            View titleView = layoutInflater.inflate(R.layout.dialog_generic_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_generic_title);
            titleText.setText(context.getString(R.string.warning_title));
            titleText.setTextSize(22);
            builder.setCustomTitle(titleView);

            builder.setView(dialogView);

            builder.create().show();

            return true;
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
}
