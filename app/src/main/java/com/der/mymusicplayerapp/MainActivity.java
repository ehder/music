package com.der.mymusicplayerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.der.mymusicplayerapp.adapters.ViewPageAdapter;
import com.der.mymusicplayerapp.database.FavoritesOperations;
import com.der.mymusicplayerapp.fragments.AllSongFragment;
import com.der.mymusicplayerapp.fragments.FavSongFragment;
import com.der.mymusicplayerapp.model.Song;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        AllSongFragment.CreateDataParse,
        FavSongFragment.CreateDataParsed {

    private Menu menu;

    private final int MY_PERMISSION_REQUEST = 100;
    private DrawerLayout mDrawerLayout;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private SeekBar seekbarController;
    private TextView tvCurrentTime, tvTotalTime, tvSongTitle;

    private ArrayList<Song> songList;
    private int currentPosition;
    private Song currentSong;

    private boolean checkFlag = false,favFlag = true, repeatFlag = false, playContinueFlag = false;
    private int songListSize;

    private String searchText = "";

    private ImageView mPrevious, mPlay, mNext, fav, repeat, loop;

    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        grantedPermission();
    }

    private void init(){

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

        tvSongTitle = findViewById(R.id.song_title);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        viewPager = findViewById(R.id.songs_viewPages);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        tabLayout = findViewById(R.id.tabs);

        seekbarController = findViewById(R.id.seekbar_controller);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);

        mPlay = findViewById(R.id.play);
        mPrevious = findViewById(R.id.previous);
        mNext = findViewById(R.id.next);
        repeat = findViewById(R.id.repeat);
        loop = findViewById(R.id.loop);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        mPlay.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        repeat.setOnClickListener(this);
        loop.setOnClickListener(this);
        
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        about();
                        break;
/*                    case R.id.nav_theme_color:
                        theme();
                        break;*/
                    case R.id.nav_rate:
                        rateApp();
                        break;
/*                    case R.id.nav_sleep_timer:
                        timer();
                        break;*/
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
/*        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                queryText();
                setPagerLayout();
                return true;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                return true;
/*            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;*/
            case R.id.menu_favorites:
                if (checkFlag)
                    if (mediaPlayer != null) {
                        if (favFlag) {
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                            item.setIcon(R.drawable.ic_favorite_filled);

                            Song favList = new Song(songList.get(currentPosition).getTitle(),
                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
                            Song favSong = new Song();
                            favSong.setTitle(songList.get(currentPosition).getTitle());
                            favSong.setSubTitle(songList.get(currentPosition).getSubTitle());
                            favSong.setPath(songList.get(currentPosition).getSubTitle());
                            favSong.setFavSong(true);

                            FavoritesOperations favoritesOperations = new FavoritesOperations(this);
                            favoritesOperations.addSongToFav(favList);
                            setPagerLayout();
                            favFlag = false;
                        } else {
                            item.setIcon(R.drawable.favorite_icon);
                            favFlag = true;
                        }
                    }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.play:
                if (checkFlag){
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        mPlay.setImageResource(R.drawable.play);
                    }else if (!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                        mPlay.setImageResource(R.drawable.pause);
                    }
                }else {
                    Toast.makeText(this, "Please select the Song to play.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.previous:
                        //No Repeat
                    if (currentPosition -1 > -1){
                        attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                        currentPosition = currentPosition - 1;
                    }else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                break;
            case R.id.next:
                        //No Repeat
                    if (currentPosition + 1 < songList.size()){
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition = currentPosition + 1;
                    }else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                break;

            case R.id.loop:
                if (!playContinueFlag) {
                    playContinueFlag = true;
                    //Toast.makeText(this, "Loop Added", Toast.LENGTH_SHORT).show();
                    loop.setImageResource(R.drawable.ic_loop_red);
                } else {
                    playContinueFlag = false;
                    //Toast.makeText(this, "Loop Removed", Toast.LENGTH_SHORT).show();
                    loop.setImageResource(R.drawable.ic_loop_black);
                }
                break;
            case R.id.repeat:

                if (repeatFlag) {
                    //Toast.makeText(this, "Replaying Removed..", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.ic_repeat_black);
                    mediaPlayer.setLooping(false);
                    repeatFlag = false;
                } else {
                    //Toast.makeText(this, "Replaying Added..", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.ic_repeat_red);
                    mediaPlayer.setLooping(true);
                    repeatFlag = true;
                }
                break;

        }

    }

    private void attachMusic(String name, String path){

        mPlay.setImageResource(R.drawable.play);
        tvSongTitle.setText(name);
        menu.getItem(0).setIcon(R.drawable.favorite_icon);
        checkFlag = true;
        favFlag = true;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            setControlsAndMusicPlay();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlay.setImageResource(R.drawable.play);
                if (playContinueFlag) {
                    //Toast.makeText(MainActivity.this, "next song...", Toast.LENGTH_SHORT).show();
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else {
                        //Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                        currentPosition = 0;
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                }
            }
        });


    }

    private void setControlsAndMusicPlay(){
        seekbarController.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playCycle();

        if (mediaPlayer.isPlaying()) {
            mPlay.setImageResource(R.drawable.pause);
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void playCycle() {
        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));

            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();
                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        } else {
            setPagerLayout();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setPagerLayout();
                    } else {
                        Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        finish();
                    }
                }
        }
    }

    private void setPagerLayout(){

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), getLifecycle(), getContentResolver());
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                //TODO Not Finish...
                if (position == 0){
                    //tab.setIcon(R.drawable.song_list);
                    tab.setText("All");
                }else if(position == 1){
                    //tab.setIcon(R.drawable.fav_song);
                    tab.setText("Favourites");
                }

            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Toast.makeText(MainActivity.this, tab.getPosition() == 0 ? "all song" : "fav song", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private String getTimeFormatted(long milliSeconds){

        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }

    private void about(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("about")
                .setMessage(R.string.about_text)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "thank for using app", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void rateApp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please rate this app")
                .setMessage(R.string.rate_text)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "thank for using app", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void theme(){

    }

    private void timer(){

    }

    @Override
    public void onDataPass(String name, String path) {
        attachMusic(name, path);
    }

    //AllSongFragment ကလာတဲ့ Music List ကို ဒီ မှာ လာ pass
    //FavSongFragment ကလာတဲ့ Music List ကို ဒီ မှာ လာ pass
    @Override
    public void fullSongList(ArrayList<Song> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }

    @Override
    public void currentSong(Song song) {
        this.currentSong = song;
    }

    @Override
    public void getLength(int length) {
        songListSize = length;
    }

}