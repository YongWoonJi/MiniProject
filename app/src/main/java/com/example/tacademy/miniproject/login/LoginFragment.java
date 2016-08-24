package com.example.tacademy.miniproject.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tacademy.miniproject.R;
import com.example.tacademy.miniproject.data.FacebookUser;
import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.data.User;
import com.example.tacademy.miniproject.manager.NetworkManager;
import com.example.tacademy.miniproject.manager.NetworkRequest;
import com.example.tacademy.miniproject.manager.PropertyManager;
import com.example.tacademy.miniproject.request.FacebookLoginRequest;
import com.example.tacademy.miniproject.request.LoginRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public LoginFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.edit_email)
    EditText emailView;

    @BindView(R.id.edit_password)
    EditText passwordView;

    @BindView(R.id.login_button)
    LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        PropertyManager.getInstance().setFacebookId("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                processAfterFacebookLogin();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void processAfterFacebookLogin() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            String token = accessToken.getToken();
            String regid = PropertyManager.getInstance().getRegistrationId();
            FacebookLoginRequest request = new FacebookLoginRequest(getContext(), token, regid);
            NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<Object>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<Object>> request, NetworkResult<Object> result) {
                    if (result.getCode() == 1) {
                        String facebookId = accessToken.getUserId();
                        PropertyManager.getInstance().setFacebookId(facebookId);
                        ((SimpleLoginActivity)getActivity()).moveMainActivity();
                    } else if (result.getCode() == 3){
                        FacebookUser user = (FacebookUser)result.getResult();
                        ((SimpleLoginActivity)getActivity()).changeFacebookSignup(user);
                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<Object>> request, int errorCode, String errorMessage, Throwable e) {
                    Toast.makeText(getContext(), "login fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.btn_login)
    public void onLogin(View view) {
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        String regid = PropertyManager.getInstance().getRegistrationId();
        LoginRequest request = new LoginRequest(getContext(), email, password, regid);
        NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<User>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<User>> request, NetworkResult<User> result) {
                User user = result.getResult();
                Toast.makeText(getContext(), "user id : " + user.getId(), Toast.LENGTH_SHORT).show();
                PropertyManager.getInstance().setEmail(email);
                PropertyManager.getInstance().setPassword(password);
                ((SimpleLoginActivity)getActivity()).moveMainActivity();
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<User>> request, int errorCode, String errorMessage, Throwable e) {
                Toast.makeText(getContext(), "error : " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_signup)
    public void onSignUp() {
        ((SimpleLoginActivity)getActivity()).changeSingup();
    }

}
