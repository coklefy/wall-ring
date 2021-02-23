package com.wallring.Trial_files;


import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wallring.Database.Database;
import com.wallring.Model.Request;
import com.wallring.Model.SavedImage;
import com.wallring.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    public ArrayList<ImageModel> data = new ArrayList<>();
    int pos;
    ImageModel imgSelected;
    int positionImage;
    ImageView myImage;
    String url;
    GlideBitmapDrawable glideBitmapDrawable;

    ImageLoader imageLoader;
    FirebaseDatabase database;
    DatabaseReference requests;

    String user, category;
    List<SavedImage> imageList;


    private static final  int WRITE_EXTERNAL_STORAGE_CODE = 1;

    Toolbar toolbar;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);

        setTitle(data.get(pos).getName());

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        imageList = new ArrayList<>();


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorDetail));
        }

        if(getIntent()!=null){
            category = getIntent().getStringExtra("CategoryId");
            user = getIntent().getStringExtra("user");
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            public void onPageSelected(int position) {
                positionImage = position;
                //noinspection ConstantConditions
                setTitle(data.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        View rootView = mSectionsPagerAdapter.getItem(positionImage).getView();

        myImage =  mViewPager.findViewWithTag(data.get(positionImage).getName());
        Bitmap bitmap = ((BitmapDrawable)myImage.getDrawable()).getBitmap();
       /* myImage.setDrawingCacheEnabled(true);
        myImage.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        myImage.layout(0, 0, myImage.getMeasuredWidth(), myImage.getMeasuredHeight());
        myImage.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(myImage.getDrawingCache());
        myImage.setDrawingCacheEnabled(false); */


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setHome) {
            setImgWallpaperHome(bitmap);
            mViewPager.setCurrentItem(positionImage-1);
            mViewPager.setCurrentItem(positionImage+1);
            return true;
        }

        /*
        if (id == R.id.action_setLock) {
            saveImageWallpaperLock(bitmap);
        } */

        if (id == R.id.action_save) {
            saveImage(bitmap);
            mViewPager.setCurrentItem(positionImage-1);
            mViewPager.setCurrentItem(positionImage+1);;
            return true;
        }

        if (id == R.id.action_share) {
            share(bitmap);
            mViewPager.setCurrentItem(positionImage-1);
            mViewPager.setCurrentItem(positionImage+1);
            return true;
        }

        if (id == R.id.action_favorite) {
            addAsFavourite();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void share(Bitmap bitmap){
        try{
            // get title and description and save in string s
            String s = data.get(positionImage).getName();

            File file = new File(getExternalCacheDir(), "samle.jpeg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            // Intent to share image and text
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, s);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpeg");
            mViewPager.setCurrentItem(mSectionsPagerAdapter.getItemPosition(mViewPager.findViewWithTag(data.get(positionImage).getName())));
            startActivity(Intent.createChooser(intent, "Share via"));
        }catch (Exception e){
            Log.w("Error", e.getMessage());
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private void setImgWallpaperHome(Bitmap bitmap){
        WallpaperManager myWallManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallManager.setBitmap(bitmap);
            Toasty.success(this, "Home wallpaper was set successfully!", Toast.LENGTH_SHORT).show();
        }catch (Exception e ){
            Toasty.error(this, "Home wallpaper failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImgWallpaperLock(Bitmap bitmap){
        WallpaperManager myWallManager = WallpaperManager.getInstance(getApplicationContext());
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                myWallManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
            }
            Toasty.success(this, "Home wallpaper was set successfully!", Toast.LENGTH_SHORT).show();

        }catch (Exception e ){
            Toasty.error(this, "Home wallpaper failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageWallpaperLock(Bitmap bitmap){
        // IF OS IS >= MARSHMALLOW WE NEED RUNTIME PERMISSION TO SAVE IMAGE
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                // SHOW POPUP TO GRANT PERMISSION
                requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE );
            }else{
                // PERMISSION ALREADY GRANTED
                setImgWallpaperLock(bitmap);

            }
        }else{
            // SYSTEM OS IS <= MARSHMALLOW, SAVE IAMGE
            setImgWallpaperLock(bitmap);
        }
    }

    private void saveImage(Bitmap bitmap){
        // IF OS IS >= MARSHMALLOW WE NEED RUNTIME PERMISSION TO SAVE IMAGE
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                // SHOW POPUP TO GRANT PERMISSION
                requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE );
            }else{
                // PERMISSION ALREADY GRANTED
                save(bitmap);

            }
        }else{
            // SYSTEM OS IS <= MARSHMALLOW, SAVE IAMGE
            save(bitmap);

        }
    }

    private void save(Bitmap bitmap){
        // path to external storage
        File  path = Environment.getExternalStorageDirectory();
        File dir = new File(path+"/Wallring/");
        dir.mkdirs();
        // image name
        String imageName = data.get(positionImage).getName()+".JPEG";
        File file = new File(dir, imageName);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toasty.success(this, "Image was saved successfully!", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toasty.error(this, "Image was not saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }


    private void addAsFavourite(){

       checkUserExits();

       /*imageList.add(new SavedImage("Dadwadwa"));

        Request request = new Request(imageList);

        Toast.makeText(this, "Add to saved Images", Toast.LENGTH_SHORT).show();*/

    }

    private void checkUserExits() {


        Query query = requests.orderByKey().equalTo(getIntent().getStringExtra("user"));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> myUrls = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            for(DataSnapshot image : user.getChildren()) {
                                String str = image.getValue().toString().substring(11);
                                String url = str.substring(0, str.indexOf("}"));
                                Log.w("Fuck", url);
                                SavedImage newImg = new SavedImage(url);
                                imageList.add(newImg);
                                myUrls.add(url);
                            }
                        }
                        if(myUrls.contains(data.get(positionImage).getUrl())){
                            Toasty.info(DetailActivity.this, "Image is already added as favourite!", Toast.LENGTH_SHORT).show();
                            imageList.clear();
                        }else {
                            imageList.add(new SavedImage(data.get(positionImage).getUrl()));
                            requests.child(getIntent().getStringExtra("user"))
                                    .setValue(imageList);
                            finish();
                            imageList.clear();
                            Toasty.success(DetailActivity.this, "Image was added as favourite!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        imageList.add(new SavedImage(data.get(positionImage).getUrl()));
                        requests.child(getIntent().getStringExtra("user"))
                                .setValue(imageList);
                        finish();
                        imageList.clear();
                        Toasty.success(DetailActivity.this, "Image was added as favourite!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{
                // if request code is cancelled the result arrays are empty
            }
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<ImageModel> data = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<ImageModel> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, data.get(position).getName(), data.get(position).getUrl());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position).getName();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        String name, url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        public Bitmap bitmap;

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.name = args.getString(ARG_IMG_TITLE);
            this.url = args.getString(ARG_IMG_URL);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
            Glide.with(getActivity()).load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            setBitmap(resource);
                            imageView.setImageBitmap(resource);
                        }
                    });
            imageView.setTag(this.name);

            return rootView;
        }


        private void setBitmap(Bitmap bitmap){
            this.bitmap = bitmap;
        }

        public Bitmap getBitMap(){
            return   this.bitmap;
        }

    }

}
