package com.wallring.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallring.Interface.ItemClickListener;
import com.wallring.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenu;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenu = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);


        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
