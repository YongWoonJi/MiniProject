package com.example.tacademy.miniproject.request;

import android.content.Context;

import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.data.User;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-08-22.
 */
public class FacebookSignupRequest extends AbstractRequest<NetworkResult<User>>{

    Request mRequest;
    public FacebookSignupRequest(Context context, String username, String email) {
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("facebooksignup")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .build();
        mRequest = new Request.Builder()
                .url(url)
                .post(body)
                .tag(context)
                .build();
    }


    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<User>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return mRequest;
    }
}
