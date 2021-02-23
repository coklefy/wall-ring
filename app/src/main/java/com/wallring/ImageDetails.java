package com.wallring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wallring.Model.Image;

public class ImageDetails extends AppCompatActivity {

    TextView image_name, image_description;
    ImageView image_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String imageId="";

    FirebaseDatabase database;
    DatabaseReference images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        // Firebase
        database = FirebaseDatabase.getInstance();
        images = database.getReference("Images");

        // Init view
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        image_description = (TextView)findViewById(R.id.image_description);
        image_name = (TextView)findViewById(R.id.image_name);
        image_image = (ImageView)findViewById(R.id.img_image);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // Get Image Id from intent
        if(getIntent() != null){
            imageId = getIntent().getStringExtra("ImageId");
        }
        if(!imageId.isEmpty()){
            getDetailFood(imageId);
        }


    }

    private void getDetailFood(String foodId) {
        images.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Image image = dataSnapshot.getValue(Image.class);

                // set image
                Picasso.with(getBaseContext()).load(image.getImage())
                        .into(image_image);
                collapsingToolbarLayout.setTitle(image.getName());
                image_name.setText(image.getName());
                image_description.setText(image.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
