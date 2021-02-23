package com.wallring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wallring.Interface.ItemClickListener;
import com.wallring.Model.Image;
import com.wallring.Trial_files.DetailActivity;
import com.wallring.Trial_files.ImageModel;
import com.wallring.ViewHolder.ImageViewHolder;

import java.util.ArrayList;

public class ImageList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference imageList;
    ArrayList<ImageModel> data = new ArrayList<>();


    String categoryId = "";
    String user = "";

    FirebaseRecyclerAdapter<Image, ImageViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // Firebase
        database = FirebaseDatabase.getInstance();
        imageList = database.getReference("Images");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent()!=null){
            categoryId = getIntent().getStringExtra("Category_ID");
            user = getIntent().getStringExtra("User_ID");
        }
        if(!categoryId.isEmpty() && categoryId != null){
            loadListImage(categoryId);
        }



    }

    private void loadListImage(final String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Image, ImageViewHolder>(Image.class,
                R.layout.image_item,
                ImageViewHolder.class,
                imageList.orderByChild("categoryID").equalTo(categoryId)) {


            @Override
            protected void populateViewHolder(ImageViewHolder viewHolder, Image model, int i) {

                ImageModel im = new ImageModel();
                im.setName(model.getName()); im.setUrl(model.getImage());

                data.add(im);

                viewHolder.image_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.image_image);



                final Image local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Start new Activity
                        Intent imageDetail = new Intent(ImageList.this, DetailActivity.class);
                        // Send Image Id to new activity
                        imageDetail.putExtra("ImageId", adapter.getRef(position).getKey());
                        imageDetail.putExtra("CategoryId", categoryId);
                        imageDetail.putExtra("pos", position);
                        imageDetail.putExtra("data", data);
                        imageDetail.putExtra("user", user);


                        startActivity(imageDetail);
                    }

                });

            }

        };

        // set adapter
        recyclerView.setAdapter(adapter);

    }
}
