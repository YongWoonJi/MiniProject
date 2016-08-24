package com.example.tacademy.miniproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tacademy.miniproject.data.ContentData;
import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.manager.NetworkManager;
import com.example.tacademy.miniproject.manager.NetworkRequest;
import com.example.tacademy.miniproject.request.UploadRequest;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContentAddActivity extends AppCompatActivity {

    @BindView(R.id.edit_message)
    EditText messageView;

    @BindView(R.id.image_picture)
    ImageView pictureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_add);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("savedfile");
            if (!TextUtils.isEmpty(path)) {
                mSavedFile = new File(path);
                uploadFile = new File(path);
            }
            path = savedInstanceState.getString("contentfile");
            if (!TextUtils.isEmpty(path)) {
                mContentFile = new File(path);
                Glide.with(this)
                        .load(mContentFile)
                        .into(pictureView);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSavedFile != null) {
            outState.putString("savedfile", mSavedFile.getAbsolutePath());
        }
        if (mContentFile != null) {
            outState.putString("contentfile", mContentFile.getAbsolutePath());
        }

        if (uploadFile != null) {
            outState.putString("uploadfile", mSavedFile.getAbsolutePath());
        }
    }

    private static final int RC_GET_IMAGE = 1;

    private static final int RC_CAMERA = 2;

    File mSavedFile, mContentFile;

    @OnClick(R.id.btn_pickup)
    public void onUpload(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getSaveFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, RC_CAMERA);
    }


    private Uri getSaveFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        ), "my_image");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mSavedFile = new File(dir, "my_picture_" + System.currentTimeMillis() + ".jpg");
        return Uri.fromFile(mSavedFile);
    }

    @OnClick(R.id.btn_get_image)
    public void onGetImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RC_GET_IMAGE);
    }

    File uploadFile = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    uploadFile = new File(path);
                    Glide.with(this)
                            .load(uploadFile)
                            .into(pictureView);
                }
            }
        } else if (requestCode == RC_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                mContentFile = mSavedFile;
                uploadFile = mSavedFile;
                Glide.with(this)
                        .load(mSavedFile)
                        .into(pictureView);
                Log.i("MyMy", "실행함");
            }
        }
    }

    @OnClick(R.id.btn_upload)
    public void onUpload() {
        String content = messageView.getText().toString();
        if (!TextUtils.isEmpty(content) && uploadFile != null) {
            UploadRequest request = new UploadRequest(this, content, uploadFile);
            NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<ContentData>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<ContentData>> request, NetworkResult<ContentData> result) {
                    Toast.makeText(ContentAddActivity.this, "success", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<ContentData>> request, int errorCode, String errorMessage, Throwable e) {
                    Toast.makeText(ContentAddActivity.this, "fail..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
