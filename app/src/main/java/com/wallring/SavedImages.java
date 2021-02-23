package com.wallring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wallring.Common.Common;
import com.wallring.Interface.ItemClickListener;
import com.wallring.Model.Category;
import com.wallring.Model.SavedImage;
import com.wallring.ViewHolder.ImageViewHolder;
import com.wallring.ViewHolder.MenuViewHolder;
import com.wallring.ViewHolder.SavedImageViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SavedImages extends AppCompatActivity{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference imageList;

    List<SavedImage> data ;
    List<String> myUrls = new ArrayList<>();

    String user = "";

    FirebaseRecyclerAdapter<SavedImage, SavedImageViewHolder> adapter;

    public SavedImages(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);
        FirebaseApp.initializeApp(SavedImages.this);

        // Firebase
        database = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_imageI);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent()!=null){
            user = getIntent().getStringExtra("user_id");
        }
        imageList = database.getReference("Requests").child(user);
        loadListImage(user);


    }

     private void loadListImage(final String user) {


         adapter = new FirebaseRecyclerAdapter<SavedImage, SavedImageViewHolder>(SavedImage.class,
                R.layout.saved_image_item,
                SavedImageViewHolder.class,
                imageList.orderByChild("image_URL")){


                @Override
                protected void populateViewHolder(SavedImageViewHolder viewHolder, SavedImage model, int i) {

                   viewHolder.image_name.setText("...");
                    Picasso.with(getBaseContext()).load(model.getImage_URL())
                    .into(viewHolder.image_image);


                        viewHolder.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        showAlertDialog(position);

                     }
            });
        }
         };

        // set adapter
        recyclerView.setAdapter(adapter);

    }

    private void showAlertDialog(final int position) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SavedImages.this);
        alertDialog.setTitle("Remove from favourites");
        alertDialog.setMessage("Are you sure?");
        
        alertDialog.setIcon(R.drawable.ic_save_black_24dp);


        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFromFavourites(position);
               // Toasty.success(SavedImages.this, "Removed", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void removeFromFavourites(int position) {

        adapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Photo is removed successfully!
                    Toasty.success(SavedImages.this, "Image removed from favourites!", Toast.LENGTH_SHORT).show();
                }
            }
            
        });

    }
}

