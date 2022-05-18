package com.der.mymusicplayerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.der.mymusicplayerapp.R;
import com.der.mymusicplayerapp.model.Song;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> implements Filterable {

    private Context mContext;
    private ArrayList<Song> songList;


    public SongAdapter(@NonNull Context context,ArrayList<Song> songList) {
        super(context, 0, songList);
        this.mContext = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.items, parent, false);
        }
        Song currentSong = songList.get(position);
        TextView tvTitle = view.findViewById(R.id.tv_music_name);
        TextView tvSubtitle = view.findViewById(R.id.tv_music_subtitle);
        tvTitle.setText(currentSong.getTitle());
        tvSubtitle.setText(currentSong.getSubTitle());

        return view;
    }
}
