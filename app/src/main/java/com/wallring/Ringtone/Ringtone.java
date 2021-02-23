package com.wallring.Ringtone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wallring.Model.Song;
import com.wallring.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Ringtone extends AppCompatActivity {

    ListView myListViewForSongs;
    private ArrayList<Song>  items;
    private RecyclerView recycler;
    private SongAdapter mAdapter;
    private TextView tb_title, tb_duration, tv_time;

    ArrayList<String> mySongs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        //initializeViews();


        recycler = (RecyclerView) findViewById(R.id.recycler);

        runtimePermission();
        listAssetFiles("");
        display();
    }

    private boolean listAssetFiles(String path) {

        String [] list;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    // if (!listAssetFiles(path + "/" + file))
                    //    return false;
                    //else {
                    if(file.contains(".mp3")) {
                        Log.w("Assets", file);
                        mySongs.add(file);
                        // This is a file
                        // TODO: add file name to an array list
                    }
                    //}
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public void runtimePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.cancelPermissionRequest();
                    }
                }).check();
    }


    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for(File singFile : files){
            if(singFile.isDirectory() && !singFile.isHidden()){
                arrayList.addAll(findSong(singFile));
            }else{
                if(singFile.getName().endsWith(".mp3") ||
                singFile.getName().endsWith(".wav")){
                    arrayList.add(singFile);
                }
            }
        }

        return arrayList;
    }

    void display() {

        //final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new ArrayList<>();

        for (int i = 0; i < mySongs.size(); i++) {
            final int min = 25;
            final int max = 30;
            final int random = new Random().nextInt((max - min) + 1) + min;

            items.add(new Song(mySongs.get(i), "hptt://google.com",
                    random));
        }


        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new SongAdapter(getApplicationContext(), items, new SongAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Song song, int position) {
                Intent playIt = new Intent(Ringtone.this, MusicPlayer.class);
                playIt.putExtra("pos", position);
                startActivity(playIt);
            }
        });
        recycler.setAdapter(mAdapter);

    }
}
