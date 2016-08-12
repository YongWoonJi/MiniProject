package com.example.tacademy.miniproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tacademy.miniproject.data.ContentData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-08-12.
 */
public class ContentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_content)
    TextView contentView;
    @BindView(R.id.image_picture)
    ImageView pictureView;

    public ContentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setContent(ContentData content) {
        contentView.setText(content.getContent());
        Glide.with(pictureView.getContext())
                .load(content.getImageUrl())
                .into(pictureView);
    }

}
