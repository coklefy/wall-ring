package com.wallring.Ringtone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.wallring.R;
import com.wallring.soundboard.DatabaseHandler;
import com.wallring.soundboard.SoundObject;

import java.io.File;
import java.util.ArrayList;

import static android.provider.Settings.System.canWrite;
import static com.wallring.R.layout.activity_player;

public class Player extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    Button btn_next, btn_previous, btn_pause, btn_set;
    TextView songTextLabel;
    SeekBar songSeekbar;
    String sname;
    static MediaPlayer myMediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateseekBar;

    ArrayList<SoundObject> soundList = new ArrayList<>();
    DatabaseHandler databaseHandler = new DatabaseHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_player);




        if (appUpdate()){

            databaseHandler.createSoundCollection(this);

            databaseHandler.updateFavorites();
        }

        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_set = (Button) findViewById(R.id.setRingtone);

        songTextLabel = (TextView) findViewById(R.id.songLabel);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);

        requestPermissions();

        addDataToArrayList();



        updateseekBar = new Thread() {

            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(1000);
                       // currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        sname = mySongs.get(position).getName().toString();

        String songName = i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position = bundle.getInt("pos", 0);

        Uri u = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

        myMediaPlayer.start();
        songSeekbar.setMax(myMediaPlayer.getDuration());

       // updateseekBar.start();

        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(myMediaPlayer.getDuration());

                if (myMediaPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position + 1) % mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                myMediaPlayer.start();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);

                Uri u = Uri.parse(mySongs.get(position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songTextLabel.setText(sname);

                myMediaPlayer.start();

            }
        });

        /*btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openOptionsMenu();

                myMediaPlayer.stop();
                myMediaPlayer.release();

                changeSystemAudio(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, mySongs.get(position));

            }

        });*/

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ringtone:
                if(myMediaPlayer.isPlaying()){
                    myMediaPlayer.stop();
                }
                myMediaPlayer.release();

                changeSystemAudio(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, mySongs.get(position));
                return true;
           /* case R.id.alarm:
                if(myMediaPlayer.isPlaying()){
                    myMediaPlayer.stop();
                }
                myMediaPlayer.release();

                changeSystemAudio(getApplicationContext(), RingtoneManager.TYPE_ALARM, mySongs.get(position));
                return true;

            case R.id.share:
                if(myMediaPlayer.isPlaying()){
                    myMediaPlayer.stop();
                }
                myMediaPlayer.release();

                shareSound();
                return true; */

            default:
                return false;
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
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


    private boolean appUpdate(){

        // We are saving the current app version into a preference file
        // There are two ways to get a handle to a SharedPreferences, we are creating a unique preference file that is not bound to a context
        // Check the android developer documentation if you want to find out more

        // Define a name for the preference file and a key name to save the version code to it
        final String PREFS_NAME = "VersionPref";
        final String PREF_VERSION_CODE_KEY = "version_code";
        // Define a value that is set if the key does not exist
        final int DOESNT_EXIST = -1;

        // Get the current version code from the package
        int currentVersionCode = 0;
        try{

            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e){

            Log.e("LOG", e.getMessage());
        }

        // Get the SharedPreferences from the preference file
        // Creates the preference file if it does not exist
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Get the saved version code or set it if it does not exist
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Create an editor to edit the shared preferences on app update
        SharedPreferences.Editor edit = prefs.edit();

        //Check for updates
        if (savedVersionCode == DOESNT_EXIST){

            databaseHandler.appUpdate();
            // First run of the app
            // Set the saved version code to the current version code
            edit.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
            edit.commit();
            return true;
        }
        else if (currentVersionCode > savedVersionCode){

            // App update
            databaseHandler.appUpdate();
            edit.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
            edit.commit();
            return true;
        }

        return false;
    }

    private void addDataToArrayList() {

        soundList.clear();

        // Get a cursor filled with all information from the MAIN_TABLE
        Cursor cursor = databaseHandler.getSoundCollection();

        // Check if the cursor is empty or failed to convert the data
        if (cursor.getCount() == 0) {

            Log.e("LOG", "Cursor is empty or failed to convert data");
            cursor.close();
        }

        // Prevent the method from adding SoundObjects again everytime the Activity starts
        if (cursor.getCount() != soundList.size()) {

            // Add each item of MAIN_TABLE to soundList and refresh the RecyclerView by notifying the adapter about changes
            while (cursor.moveToNext()) {

                String NAME = cursor.getString(cursor.getColumnIndex("soundName"));
                Integer ID = cursor.getInt(cursor.getColumnIndex("soundId"));

                soundList.add(new SoundObject(NAME, ID));

                //SoundAdapter.notifyDataSetChanged();
            }

            cursor.close();
        }
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


    private void shareSound(){
        try{

            // Check if the users device Android version is 5.1 or higher
            // If it is you'll have to use FileProvider to get the sharing function to work properly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){

                final String AUTHORITY = getApplicationContext().getPackageName() + ".fileprovider";

                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), AUTHORITY, mySongs.get(position));

                final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                // Define the intent to be of type audio/mp3
                intent.setType("audio/mp3");
                // Start a new chooser dialog where the user can choose an app to share the sound
                getApplicationContext().startActivity(Intent.createChooser(intent, "Share sound via..."));
            }
            else {
                final Intent intent = new Intent(Intent.ACTION_SEND);

                // Uri refers to a name or location
                // .parse() analyzes a given uri string and creates a Uri from it

                // Define a "link" (Uri) to the saved file
                Uri fileUri = Uri.parse(mySongs.get(position).getAbsolutePath());
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                // Define the intent to be of type audio/mp3
                intent.setType("audio/mp3");
                // Start a new chooser dialog where the user can choose an app to share the sound
                getApplicationContext().startActivity(Intent.createChooser(intent, "Share sound via..."));
            }

        } catch (Exception e){

            // Log error if process failed
            Log.w("Share", e.getMessage());
            Toast.makeText(this, "Failed to share the sound"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void setRingtone(Context context, String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        String filterName = path.substring(path.lastIndexOf("/") + 1);
        contentValues.put(MediaStore.MediaColumns.TITLE, filterName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length());
        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            String id = cursor.getString(0);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            context.getContentResolver().update(uri, contentValues, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
            Uri newuri = ContentUris.withAppendedId(uri, Long.valueOf(id));
            try {
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newuri);
                Toast.makeText(context, "Set as Ringtone Successfully.", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            cursor.close();
        }
    }

    private  void showBrightnessPermissionDialog(final Context context,Uri newUri) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        final AlertDialog alert = builder.create();
        builder.setMessage("Please give the permission to change brightness. \n Thanks ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                        alert.dismiss();
                    }
                });
        Log.w("Hello4", "E vuni demek");
        RingtoneManager.setActualDefaultRingtoneUri(
                getApplication(),
                RingtoneManager.TYPE_RINGTONE,
                newUri
        );

        alert.show();
    }

    @Override
    public void onBackPressed() {
        myMediaPlayer.stop();
        Intent returnToHome = new Intent(this, Ringtone.class);
        startActivity(returnToHome);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playing_ringtone, menu);
        return true;
    }
}
