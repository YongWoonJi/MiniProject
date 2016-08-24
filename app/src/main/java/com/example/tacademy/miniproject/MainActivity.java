package com.example.tacademy.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.login.SimpleLoginActivity;
import com.example.tacademy.miniproject.manager.NetworkManager;
import com.example.tacademy.miniproject.manager.NetworkRequest;
import com.example.tacademy.miniproject.manager.PropertyManager;
import com.example.tacademy.miniproject.request.LogOutRequest;
import com.facebook.login.LoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabhost)
    FragmentTabHost tabHost;

    public static final String EXTRA_TAB_INDEX = "tabindex";
    public static final int TAB_MAIN = 0;
    public static final int TAB_CHAT = 1;
    public static final int TAB_CONTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("main").setIndicator("Main"), MainFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("chat").setIndicator("Chat"), ChatUserFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("content").setIndicator("Content"), ContentFragment.class, null);

        int index = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
        tabHost.setCurrentTab(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            LogOutRequest request = new LogOutRequest(this);
            NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                    PropertyManager.getInstance().setEmail("");
                    PropertyManager.getInstance().setPassword("");
                    PropertyManager.getInstance().setFacebookId("");
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(MainActivity.this, SimpleLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<String>> request, int errorCode, String errorMessage, Throwable e) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().cancelAll(this);
    }
}
