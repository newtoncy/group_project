package com.newtoncy.idcardcamera.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtoncy.idcardcamera.R;
import com.newtoncy.idcardcamera.cropper.CropImageView;
import com.newtoncy.idcardcamera.cropper.CropListener;
import com.newtoncy.idcardcamera.global.Constant;
import com.newtoncy.idcardcamera.utils.FileUtils;
import com.newtoncy.idcardcamera.utils.ImageUtils;
import com.newtoncy.idcardcamera.utils.PermissionUtils;


/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${拍照界面}
 */
public class CameraActivity extends Activity implements View.OnClickListener {

    public final static  int     TYPE_IDCARD_FRONT     = 1;//身份证正面
    public final static  int     TYPE_IDCARD_BACK      = 2;//身份证反面
    public final static  int     REQUEST_CODE          = 0X11;//请求码
    public final static  int     RESULT_CODE           = 0X12;//结果码
    private final        int     PERMISSION_CODE_FIRST = 0x13;//权限请求码
    private final static String  TAKE_TYPE             = "take_type";//拍摄类型标记
    private final static String  IMAGE_PATH            = "image_path";//图片路径标记
    private              int     mType;//拍摄类型
    private              boolean isToast               = true;//是否弹吐司，为了保证for循环只弹一次

    private CropImageView mCropImageView;
    private Bitmap        mCropBitmap;
    private CameraPreview mCameraPreview;
    private View          mLlCameraCropContainer;
    private ImageView     mIvCameraCrop;
    private ImageView     mIvCameraFlash;
    private View          mLlCameraOption;
    private View          mLlCameraResult;
    private TextView      mViewCameraCropBottom;
    private FrameLayout   mFlCameraOption;
    private View          mViewCameraCropLeft;

    /**
     * 跳转到拍照界面
     *
     * @param activity
     * @param type     拍摄类型
     */
    public static void toCameraActivity(Activity activity, int type) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(TAKE_TYPE, type);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 获取图片路径
     *
     * @param data
     * @return
     */
    public static String getImagePath(Intent data) {
        if (data != null) {
            return data.getStringExtra(IMAGE_PATH);
        }
        return "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*动态请求需要的权限*/
        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(this, PERMISSION_CODE_FIRST,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (checkPermissionFirst) {
            init();
        }
    }

    /**
     * 处理请求权限的响应
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求权限结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissions = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) { //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(this, "请手动打开该应用需要的权限", Toast.LENGTH_SHORT).show();
                        isToast = false;
                    }
                }
            }
        }
        isToast = true;
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限");
            init();
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许");
            finish();
        }
    }

    private void init() {
        setContentView(R.layout.activity_camera);
        mType = getIntent().getIntExtra(TAKE_TYPE, 0);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView();
        initListener();
    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);//相机预览
        mLlCameraCropContainer = findViewById(R.id.ll_camera_crop_container);//
        mIvCameraCrop = (ImageView) findViewById(R.id.iv_camera_crop);
        mIvCameraFlash = (ImageView) findViewById(R.id.iv_camera_flash);
        mLlCameraOption = findViewById(R.id.ll_camera_option);
        mLlCameraResult = findViewById(R.id.ll_camera_result);
        mCropImageView = findViewById(R.id.crop_image_view);
        mViewCameraCropBottom = (TextView) findViewById(R.id.view_camera_crop_bottom);
        mFlCameraOption = (FrameLayout) findViewById(R.id.fl_camera_option);
        mViewCameraCropLeft = findViewById(R.id.view_camera_crop_left);

        switch (mType) {
            case TYPE_IDCARD_FRONT:
                mIvCameraCrop.setImageResource(R.mipmap.crop_front);
                break;
            case TYPE_IDCARD_BACK:
                mIvCameraCrop.setImageResource(R.mipmap.camera_idcard_back);
                break;
        }

        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCameraPreview.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 500);
    }

    private void initListener() {
        mCameraPreview.setOnClickListener(this);
        mIvCameraFlash.setOnClickListener(this);
        findViewById(R.id.iv_camera_close).setOnClickListener(this);
        findViewById(R.id.iv_camera_take).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_ok).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_preview) {
            mCameraPreview.focus();
        } else if (id == R.id.iv_camera_close) {
            finish();
        } else if (id == R.id.iv_camera_take) {
            takePhoto();
        } else if (id == R.id.iv_camera_flash) {
            boolean isFlashOn = mCameraPreview.switchFlashLight();
            mIvCameraFlash.setImageResource(isFlashOn ? R.mipmap.camera_flash_on : R.mipmap.camera_flash_off);
        } else if (id == R.id.iv_camera_result_ok) {
            confirm();
        } else if (id == R.id.iv_camera_result_cancel) {
            mCameraPreview.setEnabled(true);
            mCameraPreview.startPreview();
            mIvCameraFlash.setImageResource(R.mipmap.camera_flash_off);
            setTakePhotoLayout();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        mCameraPreview.setEnabled(false);
        Camera camera = CameraUtils.getCamera();
        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] bytes, Camera camera) {
                final Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                camera.stopPreview();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int w = size.width;
                        final int h = size.height;
                        Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, w, h);
                        cropImage(bitmap);
                    }
                }).start();
            }
        });
    }

    /**
     * 裁剪图片
     */
    private void cropImage(Bitmap bitmap) {
        /*计算扫描框的坐标点*/
        float left = mViewCameraCropLeft.getWidth();
        float top = mIvCameraCrop.getTop();
        float right = mIvCameraCrop.getRight() + left;
        float bottom = mIvCameraCrop.getBottom();

        /*计算扫描框坐标点占原图坐标点的比例*/
        float leftProportion = left / mCameraPreview.getWidth();
        float topProportion = top / mCameraPreview.getHeight();
        float rightProportion = right / mCameraPreview.getWidth();
        float bottomProportion = bottom / mCameraPreview.getBottom();

        /*旋转和镜像*/
        Matrix m = new Matrix();
        m.postRotate(270);
        m.postScale(-1,1);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
        /*自动裁剪*/
        mCropBitmap = Bitmap.createBitmap(bitmap,
                (int) (leftProportion * (float) bitmap.getWidth()),
                (int) (topProportion * (float) bitmap.getHeight()),
                (int) ((rightProportion - leftProportion) * (float) bitmap.getWidth()),
                (int) ((bottomProportion - topProportion) * (float) bitmap.getHeight()));

        /*设置成手动裁剪模式*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将手动裁剪区域设置成与扫描框一样大
                mCropImageView.setLayoutParams(new LinearLayout.LayoutParams(mIvCameraCrop.getWidth(), mIvCameraCrop.getHeight()));
                setCropLayout();
                mCropImageView.setImageBitmap(mCropBitmap);
            }
        });
    }

    /**
     * 设置裁剪布局
     */
    private void setCropLayout() {
        mIvCameraCrop.setVisibility(View.GONE);
        mCameraPreview.setVisibility(View.GONE);
        mLlCameraOption.setVisibility(View.GONE);
        mCropImageView.setVisibility(View.VISIBLE);
        mLlCameraResult.setVisibility(View.VISIBLE);
        mViewCameraCropBottom.setText("");
    }

    /**
     * 设置拍照布局
     */
    private void setTakePhotoLayout() {
        mIvCameraCrop.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mLlCameraOption.setVisibility(View.VISIBLE);
        mCropImageView.setVisibility(View.GONE);
        mLlCameraResult.setVisibility(View.GONE);
        mViewCameraCropBottom.setText(getString(R.string.touch_to_focus));

        mCameraPreview.focus();
    }

    /**
     * 点击确认，返回图片路径
     */
    private void confirm() {
        /*手动裁剪图片*/
        mCropImageView.crop(new CropListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.crop_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }

                /*保存图片到sdcard并返回图片路径*/
                if (FileUtils.createOrExistsDir(Constant.DIR_ROOT)) {
                    StringBuffer buffer = new StringBuffer();
                    String imagePath = "";
                    if (mType == TYPE_IDCARD_FRONT) {
                        imagePath = buffer.append(Constant.DIR_ROOT).append(Constant.APP_NAME).append(".").append("idCardFrontCrop.jpg").toString();
                    } else if (mType == TYPE_IDCARD_BACK) {
                        imagePath = buffer.append(Constant.DIR_ROOT).append(Constant.APP_NAME).append(".").append("idCardBackCrop.jpg").toString();
                    }

                    if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                        Intent intent = new Intent();
                        intent.putExtra(CameraActivity.IMAGE_PATH, imagePath);
                        setResult(RESULT_CODE, intent);
                        finish();
                    }
                }
            }
        }, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraPreview != null) {
            mCameraPreview.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
    }
}