package com.example.tacademy.miniproject;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tacademy.miniproject.data.ChatContract;
import com.example.tacademy.miniproject.data.User;
import com.example.tacademy.miniproject.manager.DBManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends Fragment {


    public ChatUserFragment() {
    }

    @BindView(R.id.listView)
    ListView listView;


    SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] from = {ChatContract.ChatUser.COLUMN_NAME, ChatContract.ChatUser.COLUMN_EMAIL, ChatContract.ChatMessage.COLUMN_MESSAGE};
        int[] to = {R.id.text_name, R.id.text_email, R.id.text_last_message};
        mAdapter = new SimpleCursorAdapter(getContext(), R.layout.view_chat_user, null, from, to, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_user, container, false);
        ButterKnife.bind(this, view);
        listView.setAdapter(mAdapter);
        return view;
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(int position, long id) {
        Cursor cursor = (Cursor)listView.getItemAtPosition(position);
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_SERVER_ID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_EMAIL)));
        user.setUserName(cursor.getString(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_NAME)));
        Intent intent = new Intent(getContext(),ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Cursor c = DBManager.getInstance().getChatUser();
        mAdapter.changeCursor(c);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.changeCursor(null);
    }
}
