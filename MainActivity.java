package com.example.qi.tt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.qi.tt.permission.PermissionUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
 private FragmentActivity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                PermissionUtil.requestPermission(activity, new PermissionUtil.RequestPermissionCallBack() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void accept() {
                        Log.d(TAG, "-------------------------------------------accept: ");
                    }
                    @Override
                    public void refuse() {
                        Log.d(TAG, "-----------------------------------------------refuse: ");
                    }
                    @Override
                    public void noAsk() {
                        Log.d(TAG, "--------------------------------------------------------noAsk: ");
                    }
                },Manifest.permission.READ_PHONE_STATE);
            }




        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       boolean is =  ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
        Toast.makeText(MainActivity.this,"shouldShowRequestPermissionRationale"+is,Toast.LENGTH_SHORT).show();

    }
}
