package com.der.mymusicplayerapp.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.der.mymusicplayerapp.MainActivity;
import com.der.mymusicplayerapp.R;
import com.der.mymusicplayerapp.adapters.SongAdapter;
import com.der.mymusicplayerapp.model.Song;

import java.util.ArrayList;
import java.util.List;

public class AllSongFragment extends ListFragment {

    private static ContentResolver contentResolver1;

    private ArrayList<Song> songList;
    private ArrayList<Song> newList;

    private CreateDataParse createDataParse;
    private ContentResolver contentResolver;

    private ListView listView;

    public static Fragment getInstance(int position, ContentResolver mContentResolver) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);
        contentResolver1 = mContentResolver;
        return tabFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        createDataParse = (CreateDataParse) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
        setContent();
    }

    private void setContent(){
        boolean searchedList = false;
        songList = new ArrayList<>();
        newList = new ArrayList<>();
        getSongs();

        SongAdapter adapter = new SongAdapter(getContext(), songList);

        if (!createDataParse.queryText().equals("")){
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        }else {
            searchedList = false;
        }

        createDataParse.getLength(songList.size());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // onDataPass မှာ ခေါင်းစဉ် နဲ့  path File
                createDataParse.onDataPass(songList.get(position).getTitle(), songList.get(position).getPath());
                createDataParse.fullSongList(songList, position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getContext(), "You clicked :\n" + songList.get(position), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    public void getSongs(){

        songList = new ArrayList<>();

        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, MediaStore.Audio.Media.DATE_ADDED+" DESC");

        while (songCursor.moveToNext()) {

            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int extension = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);

            Song song = new Song();
            song.setTitle(songCursor.getString(songTitle));
            song.setSubTitle(songCursor.getString(songArtist));
            song.setPath(songCursor.getString(songPath));

            songList.add(song);
        }
    }

    public SongAdapter onQueryTextChange() {

        String text = createDataParse.queryText();
        for (Song songs : songList) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    public interface CreateDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<Song> songList, int position);

        public String queryText();

        public void currentSong(Song songsList);
        public void getLength(int length);
    }

}
