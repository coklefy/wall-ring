package com.wallring.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallring.Interface.ItemClickListener;
import com.wallring.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavedImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView image_image;
    public TextView image_name;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SavedImageViewHolder(@NonNull View itemView) {
        super(itemView);
        image_name = (TextView)itemView.findViewById(R.id.image_nameI);
        image_image = (ImageView)itemView.findViewById(R.id.image_imageI);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
