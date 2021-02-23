package com.wallring.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallring.Interface.ItemClickListener;
import com.wallring.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView image_name;
    public ImageView image_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);

        image_name = (TextView)itemView.findViewById(R.id.image_name);
        image_image = (ImageView)itemView.findViewById(R.id.image_image);


        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
