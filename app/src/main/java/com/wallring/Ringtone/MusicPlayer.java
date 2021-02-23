package com.wallring.Ringtone;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wallring.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;


public class MusicPlayer extends AppCompatActivity {

    private View parent_view;
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton btn_play;
    private TextView tv_song_current_duration, tv_song_total_duration;
    private CircularImageView image;
    private TextView songTextLabel, set_as_ringtone;


    private MediaPlayer mp;
    private Handler mHandler = new Handler();

    ArrayList<String> mySongs = new ArrayList<>();
    ArrayList<File> songsList;

    int position ;


    private MusicUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        songTextLabel = (TextView) findViewById(R.id.song_name);
        set_as_ringtone = (TextView) findViewById(R.id.set_as_ringtone);

        setMusicPlayerComponents();
    }

    private void requestPermissions() {

        // Check if the users Android version is equal to or higher than Android 6 (Marshmallow)
        // Since Android 6 you have to request permissions at runtime to provide a better security
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Check if the permission to write and read the users external storage is not granted
            // You need this permission if you want to share sounds via WhatsApp or the like
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // You can log this little text if you want to see if this method works in your Android Monitor
                //Log.i(LOG_TAG, "Permission not granted");

                // If the permission is not granted request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

            // Check if the permission to write the users settings is not granted
            // You need this permission to set a sound as ringtone or the like
            if (!Settings.System.canWrite(this)) {

                // Displays a little bar on the bottom of the activity with an OK button that will open a so called permission management screen
                Snackbar.make(findViewById(android.R.id.content), "The app needs access to your settings", Snackbar.LENGTH_INDEFINITE).setAction("OK",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Context context = v.getContext();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }

        }

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

    private void  playSong(int pos){
        if(mp.isPlaying()){
            mp.pause();
            btn_play.setImageResource(R.drawable.ic_action_play);
        }

        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_action_play);
            }
        });
        try{
            AssetFileDescriptor afd = getAssets().openFd(mySongs.get(pos));
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
        } catch (Exception e) {
                Log.w("Error", e.getMessage());
                Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }

        songTextLabel.setText(mySongs.get(pos));


    }

    private void setMusicPlayerComponents() {
        listAssetFiles("");
        requestPermissions();

        Intent i = getIntent();
        position = getIntent().getIntExtra("pos", 0);


        parent_view = findViewById(R.id.parent_view);
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar);
        btn_play = findViewById(R.id.btn_play);

        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration =  findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = findViewById(R.id.total_duration);
        image =  findViewById(R.id.image);

        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_action_play);
            }
        });

        playSong(position);

        utils = new MusicUtils();
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
        updateTimerAndSeekbar();

        set_as_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSystemAudio(getApplicationContext(),  RingtoneManager.TYPE_RINGTONE, getFile());
                Toasty.success(MusicPlayer.this, "Now your ringtone is "+getFile().getName()+"!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private File getFile() {
        File f = new File(getCacheDir() + "/" + mySongs.get(position));
        if (!f.exists()) {
            try {

                InputStream is = getAssets().open(mySongs.get(position));
                byte[] buffer = new byte[1024];
                is.read(buffer);
                is.close();


                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return f;
    }

    private static void changeSystemAudio(Context context, int type, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "HandOfBlood");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);
        values.put(MediaStore.Audio.Media.IS_PODCAST, false);

        final Uri baseUri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri toneUri = getUriForExistingTone(context, baseUri, file.getAbsolutePath());
        if (toneUri == null) {
            toneUri = context.getContentResolver().insert(baseUri, values);
        }
        RingtoneManager.setActualDefaultRingtoneUri(context, type, toneUri);
    }


    private static Uri getUriForExistingTone(Context context, Uri uri, String filePath) {

        Cursor cursor = null;
        try {

            cursor = context.getContentResolver()
                    .query(uri,
                            new String[] {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA},
                            MediaStore.MediaColumns.DATA + " = ?",
                            new String[] {filePath},
                            null, null);

            if (cursor != null && cursor.getCount() != 0) {

                cursor.moveToFirst();
                int mediaPos = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                return Uri.parse(uri.toString() + "/" + mediaPos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }



    private void buttonPlayerAction() {
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btn_play.setImageResource(R.drawable.ic_action_play);
                } else {
                    mp.start();
                    btn_play.setImageResource(R.drawable.icon_pause);
                    mHandler.post(mUpdateTimeTask);
                }
                rotateTheDisk();
            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_repeat: {
                toggleButtonColor((ImageButton) v);
                playSong(position);

                buttonPlayerAction();
                updateTimerAndSeekbar();

                //Snackbar.make(parent_view, "Repeat", Snackbar.LENGTH_SHORT).show();
                break;
            }
             case R.id.btn_shuffle: {
                toggleButtonColor((ImageButton) v);
                 final int min = 0;
                 final int max = mySongs.size();
                 final int random = new Random().nextInt((max - min) + 1) + min;
                 position = random;
                 playSong(position);

                 buttonPlayerAction();
                 updateTimerAndSeekbar();

                 //Snackbar.make(parent_view, "Shuffle", Snackbar.LENGTH_SHORT).show();
                 break;
            }
            case R.id.btn_prev: {
                toggleButtonColor((ImageButton) v);
                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                playSong(position);

                buttonPlayerAction();
                updateTimerAndSeekbar();

                //Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_next: {
                toggleButtonColor((ImageButton) v);
                position = ((position + 1) % mySongs.size());
                playSong(position);

                buttonPlayerAction();
                updateTimerAndSeekbar();

                //Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            //bt.setColorFilter(getResources().getColor(R.color.colorDarkOrange), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            //bt.setColorFilter(getResources().getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    private void rotateTheDisk() {
        if (!mp.isPlaying()) return;
        image.animate().setDuration(100).rotation(image.getRotation() + 2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateTheDisk();
                super.onAnimationEnd(animation);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Snackbar.make(parent_view, item.getTitle(), Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
