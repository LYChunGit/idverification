package com.lyc.idverification.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lyc.idverification.R;
import com.lyc.idverification.bottomdialog.BottomDialog;
import com.lyc.idverification.util.RxThreadPoolTool;
import com.lyc.idverification.util.SystemUtil;

public class CameraActivity extends Activity implements View.OnClickListener {
    public final static int REQUEST_CODE = 0X13;
    public final static String RESULT_IMAGER = "RESULT_IMAGER";//返回图片地址
    public final static String CAMERA_ACTIVITY_TYPE = "CAMERA_ACTIVITY_TYPE";//返回图片类型
    public final static String CAMERA_ACTIVITY_PATH = "CAMERA_ACTIVITY_PATH";//外部传入图片地址
    public final static String CAMERA_ACTIVITY_IMAGE_RESOURCE = "CAMERA_ACTIVITY_IMAGE_RESOURCE";//外部传入的相框视图
    private CustomCameraPreview mCustomCameraPreview;
    private View mContainerView;
    private ImageView mCropView;
    private View mOptionView;
    private int mCameraImageResource, mCameraType;
    private String mCameraPath;
    private RxThreadPoolTool mRxThreadPoolTool;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//        String imagePath = data.getStringExtra(RESULT_IMAGER);
//        int imageType = data.getIntExtra(CAMERA_ACTIVITY_TYPE,0);
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    public static void SelectImageAndCamera(final Context mContext, FragmentManager manager, final int mCameraType, final int cameraImageResource, final String PATH_IMAGE) {
        final BottomDialog mBottomSelectImageAndCameraDialog = new BottomDialog();

        mBottomSelectImageAndCameraDialog.setFragmentManager(manager)
                .setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                            RelativeLayout mRlOpenImage = (RelativeLayout) v.findViewById(R.id.rl_open_image);
                            RelativeLayout mRlOpenCamera = (RelativeLayout) v.findViewById(R.id.rl_open_camera);
                            mRlOpenImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    ((Activity) mContext).startActivityForResult(albumIntent, mCameraType);
                                    mBottomSelectImageAndCameraDialog.dismiss();
                                }
                            });
                            mRlOpenCamera.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    navToCamera(mContext, mCameraType, cameraImageResource, PATH_IMAGE);
                                    mBottomSelectImageAndCameraDialog.dismiss();
                                }
                            });

                    }
                })
                .setLayoutRes(R.layout.dialog_select_image_and_camera_layout)
//                        .setDimAmount(0.9f)
                .setTag("BottomDialog")
                .show();
    }

    /**
     * 跳转到拍照页面
     */
    public static void navToCamera(Context context, int mCameraType, int cameraImageResource, String PATH_IMAGE) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CAMERA_ACTIVITY_TYPE, mCameraType);
        intent.putExtra(CAMERA_ACTIVITY_IMAGE_RESOURCE, cameraImageResource);
        intent.putExtra(CAMERA_ACTIVITY_PATH, PATH_IMAGE);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCameraImageResource = getIntent().getIntExtra(CAMERA_ACTIVITY_IMAGE_RESOURCE, 0);//图片方框
        mCameraPath = getIntent().getStringExtra(CAMERA_ACTIVITY_PATH);//图片地址
        mCameraType = getIntent().getIntExtra(CAMERA_ACTIVITY_TYPE, 0);//图片类型
        mRxThreadPoolTool = new RxThreadPoolTool(RxThreadPoolTool.Type.CachedThread, 1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_camera);

        mCustomCameraPreview = (CustomCameraPreview) findViewById(R.id.camera_surface);
        mContainerView = findViewById(R.id.camera_crop_container);
        mCropView = (ImageView) findViewById(R.id.camera_crop);
        mOptionView = findViewById(R.id.camera_option);

        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams;

        layoutParams = new RelativeLayout.LayoutParams((int) maxSize, (int) screenMinSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mCustomCameraPreview.setLayoutParams(layoutParams);

        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        mContainerView.setLayoutParams(containerParams);
        mCropView.setLayoutParams(cropParams);
        mCropView.setImageResource(mCameraImageResource);
        mCustomCameraPreview.setOnClickListener(this);
        findViewById(R.id.camera_close).setOnClickListener(this);
        findViewById(R.id.camera_take).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.camera_surface) {
            mCustomCameraPreview.focus();
        } else if (i == R.id.camera_close) {
            this.finish();
        } else if (i == R.id.camera_take) {
            takePhoto(mCameraPath, mCameraType);
        }
    }

    private void takePhoto(final String imgPath, final int cameraType) {
        mOptionView.setVisibility(View.GONE);
        mCustomCameraPreview.setEnabled(false);
        mCustomCameraPreview.takePhoto(new Camera.PictureCallback() {
            public void onPictureTaken(final byte[] data, final Camera camera) {
                //子线程处理图片，防止ANR
                mRxThreadPoolTool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = null;
                        if (data != null) {
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            camera.stopPreview();
                        }
                        if (bitmap != null) {
                            //计算裁剪位置
                            float left = ((float) mContainerView.getLeft() - (float) mCustomCameraPreview.getLeft()) / (float) mCustomCameraPreview.getWidth();
                            float top = (float) mCropView.getTop() / (float) mCustomCameraPreview.getHeight();
                            float right = (float) mContainerView.getRight() / (float) mCustomCameraPreview.getWidth();
                            float bottom = (float) mCropView.getBottom() / (float) mCustomCameraPreview.getHeight();

                            //裁剪及保存到文件
                            Bitmap resBitmap = Bitmap.createBitmap(bitmap,
                                    (int) (left * (float) bitmap.getWidth()),
                                    (int) (top * (float) bitmap.getHeight()),
                                    (int) ((right - left) * (float) bitmap.getWidth()),
                                    (int) ((bottom - top) * (float) bitmap.getHeight()));
                            SystemUtil.compressImage(resBitmap, imgPath);//等比例缩小
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                            if (!resBitmap.isRecycled()) {
                                resBitmap.recycle();
                            }

                            //拍照完成，返回对应图片路径
                            Intent intent = new Intent();
                            intent.putExtra(RESULT_IMAGER, imgPath);
                            intent.putExtra(CAMERA_ACTIVITY_TYPE, cameraType);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        return;
                    }
                });
            }
        });
    }



}
