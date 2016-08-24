package com.example.tacademy.miniproject.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.example.tacademy.miniproject.request.FacebookSignupRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookSignupFragment extends Fragment {


    public static final String ARG_FACEBOOK_USER = "facebookUser";
    public static FacebookSignupFragment newInstance(FacebookUser user) {
        FacebookSignupFragment f = new FacebookSignupFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_FACEBOOK_USER, user);
        f.setArguments(b);
        return f;
    }

    FacebookUser user;
    public FacebookSignupFragment() {
    }

    @BindView(R.id.edit_username)
    EditText nameView;
    @BindView(R.id.edit_email)
    EditText emailView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (FacebookUser)getArguments().getSerializable(ARG_FACEBOOK_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_signup, container, false);
        ButterKnife.bind(this, view);
        nameView.setText(user.getName());
        emailView.setText(user.getEmail());
        return view;
    }

    @OnClick(R.id.btn_sign_up)
    public void onSignUp(View view) {
        String username = nameView.getText().toString();
        String email = emailView.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email)) {
            FacebookSignupRequest request = new FacebookSignupRequest(getContext(), username, email);
            NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<User>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<User>> request, NetworkResult<User> result) {
                    PropertyManager.getInstance().setFacebookId(user.getId());
                    ((SimpleLoginActivity)getActivity()).moveMainActivity();
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<User>> request, int errorCode, String errorMessage, Throwable e) {
                    Toast.makeText(getContext(), "sign up fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
