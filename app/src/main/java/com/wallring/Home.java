package com.wallring;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wallring.Common.Common;
import com.wallring.Interface.ItemClickListener;
import com.wallring.Model.Category;
import com.wallring.Ringtone.Ringtone;
import com.wallring.ViewHolder.MenuViewHolder;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(Home.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gallery");
        setSupportActionBar(toolbar);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(getIntent().getStringExtra("name"));

        // Load menu

        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


    }

    private void loadMenu() {
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenu.setText((model.getName()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Get CategoryId and send to new activity
                        Intent imageList = new Intent(Home.this, ImageList.class);
                        imageList.putExtra("Category_ID", adapter.getRef(position).getKey());
                        imageList.putExtra("User_ID", getIntent().getStringExtra("user"));
                        imageList.putExtra("Name", getIntent().getStringExtra("name"));

                        startActivity(imageList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        } */
        Toasty.info(this, "You can  log out from menu!", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            // Handle the camera action
        } else if (id == R.id.nav_wallpaper) {

        } else if (id == R.id.nav_ringtone) {
            Intent ringtone = new Intent(Home.this, Ringtone.class);
            startActivity(ringtone);


        } else if (id == R.id.nav_saved) {
                Intent imageList = new Intent(Home.this, SavedImages.class);
                 imageList.putExtra("user_id", getIntent().getStringExtra("user"));
                //imageList.putExtra("Category_ID", adapter.getRef(position).getKey());
                //imageList.putExtra("User_ID", getIntent().getStringExtra("user"));
                startActivity(imageList);

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_log_out) {
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toasty.normal(this, "See you soon!", Toast.LENGTH_LONG, ContextCompat.getDrawable(
                    this, R.drawable.ic_favorite_border_black_24dp)).show();
            startActivity(signIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
