package com.der.mymusicplayerapp.adapters;

import android.content.ContentResolver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.der.mymusicplayerapp.MainActivity;
import com.der.mymusicplayerapp.fragments.AllSongFragment;
import com.der.mymusicplayerapp.fragments.FavSongFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    private ContentResolver contentResolver;
    private static String title[] = {"All SONGS","CURRENT PLAYLIST", "FAVORITES"};

    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ContentResolver contentResolver) {
        super(fragmentManager, lifecycle);
        this.contentResolver = contentResolver;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return AllSongFragment.getInstance(position, contentResolver);
            case 1:
                return FavSongFragment.getInstance(position);
            default:
                return FavSongFragment.getInstance(position);
        }
    }

    @Override
    public int getItemCount() {
        return title.length;
    }


}
