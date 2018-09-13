package com.lyc.idverification.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lyc.idverification.app.App;
import com.lyc.idverification.camera.CameraActivity;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import static com.lyc.idverification.camera.CameraActivity.CAMERA_ACTIVITY_TYPE;
import static com.lyc.idverification.camera.CameraActivity.RESULT_CODE;

public class testActivity extends AppCompatActivity {
    Button mBtnOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        mBtnOpen = findViewById(R.id.btn_open);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermission = new RxPermissions(testActivity.this);
            rxPermission.requestEach(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,//SD卡写入
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE//读外部存储的权限
            )
                    .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) throws Exception {
                            if (permission.granted) {
                                // 用户已经同意该权限

                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                Toast.makeText(testActivity.this, "您已关闭了SD卡写入以及读取权限，暂无法使用！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(testActivity.this, "您已关闭了SD卡写入以及读取权限，暂无法使用！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = App.getInstance().getExternalCacheDir().getPath() + "/ID_CARD_POSITIVE.png";
                //navToCamera 1、Context 2、图片返回的类型标志 3、图片拍照时候提示框 4、你要拍完图片放在哪里
                CameraActivity.navToCamera(testActivity.this, 1, R.mipmap.camera_front, path);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        String imagePath = data.getStringExtra(RESULT_CODE);
        int imageType = data.getIntExtra(CAMERA_ACTIVITY_TYPE,0);
        if (!TextUtils.isEmpty(imagePath)){
            Toast.makeText(testActivity.this,"图片地址:"+imagePath+" 返回图片类型："+imageType,Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
