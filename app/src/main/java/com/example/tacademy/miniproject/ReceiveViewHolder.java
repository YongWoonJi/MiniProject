package com.example.tacademy.miniproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-08-11.
 */
public class ReceiveViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_message)
    TextView messageView;
    public ReceiveViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }
}
