package com.arcsoft.glsurfaceviewdemo;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;

import com.arcsoft.glsurfaceviewdemo.gles.DisplaySurfaceView;
import com.arcsoft.glsurfaceviewdemo.yuv_tool.YuvUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private final static String TAG = "MainActivity";
    DisplaySurfaceView mDisplaySurfaceView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
            return;
        }
        setContentView(R.layout.activity_main);
        mDisplaySurfaceView = findViewById(R.id.surfaceView);   // 通过id找surfceview
        readFile();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission(){
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new StorageConfirmationDialog().show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }
    public static class StorageConfirmationDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission_sdcard)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }
    public void readFile() {
        Log.e(TAG, "read file");
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/123.uyvy";
        Log.d(TAG, path);
        File file = new File(path);
        if (file.canRead()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                int length = fileInputStream.available();
                byte[] uyvy_bytes = new byte[length];
                fileInputStream.read(uyvy_bytes);
                Log.d(TAG, "read file size:" + length);
                byte[] nv21_bytes = YuvUtil.UYVY_2_NV21(uyvy_bytes, 1920, 1080);
                YuvUtil.blackening_nv12(100, 100, 300, 300, 1920, 1080, nv21_bytes);
                mDisplaySurfaceView.mGlRenderer.nv21 = nv21_bytes;
                mDisplaySurfaceView.mGlRenderer.mWidth = 1920;
                mDisplaySurfaceView.mGlRenderer.mHeight = 1080;
                mDisplaySurfaceView.requestRender();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "can not read file!");
        }
    }
}