package com.der.mymusicplayerapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.der.mymusicplayerapp.R;
import com.der.mymusicplayerapp.adapters.SongAdapter;
import com.der.mymusicplayerapp.database.FavoritesOperations;
import com.der.mymusicplayerapp.model.Song;

import java.util.ArrayList;

public class FavSongFragment extends ListFragment {

    private FavoritesOperations favoritesOperations;

    public ArrayList<Song> songsList;
    public ArrayList<Song> newList;

    private CreateDataParsed createDataParsed;

    private ListView listView;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        FavSongFragment tabFragment = new FavSongFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        favoritesOperations = new FavoritesOperations(context);
        createDataParsed = (CreateDataParsed) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        setContent();
    }

    public void setContent(){
        songsList = new ArrayList<>();
        songsList = favoritesOperations.getAllFavSong();
        SongAdapter adapter = new SongAdapter(getContext(), songsList);

        if (!createDataParsed.queryText().equals("")) {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            //searchedList = true;
        } else {
            //searchedList = false;
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createDataParsed.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                createDataParsed.fullSongList(songsList, position);
                }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteOption(position);
                return true;
            }
        });


    }

    private void deleteOption(int position) {
        if (position != createDataParsed.getPosition())
            showDialog(songsList.get(position).getPath(), position);
        else
            Toast.makeText(getContext(), "You Can't delete the Current Song", Toast.LENGTH_SHORT).show();
    }

    private void showDialog(final String index, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete")
                .setCancelable(true)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoritesOperations.removeSong(index);
                        createDataParsed.fullSongList(songsList, position);
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public SongAdapter onQueryTextChange() {
        String text = createDataParsed.queryText();
        for (Song songs : songsList) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    public interface CreateDataParsed {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<Song> songList, int position);

        public int getPosition();

        public String queryText();
    }
}
