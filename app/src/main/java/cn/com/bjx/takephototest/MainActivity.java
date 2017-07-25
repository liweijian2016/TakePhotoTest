package cn.com.bjx.takephototest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;

import cn.com.bjx.takephototest.base.takephoto.TakePhotoBaseActivity;

public class MainActivity extends TakePhotoBaseActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private EditUserPortraitDialog mDialog;
    // step 1:
    private TakePhotoHelper mTakePhotoHelper;
    private ImageView mHeadImg;
    private ImageView mImg1;
    private ImageView mImg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(rootView);
        initSystemBar(R.color.master_tone);
        // 初始化对话框及其监听.
        mHeadImg = f(R.id.mHeadImg);
        mImg1 = f(R.id.img1);
        mImg2 = f(R.id.img2);
        initListener();
        mTakePhotoHelper = TakePhotoHelper.of();
    }

    private void initListener() {
        mDialog = new EditUserPortraitDialog(this, new EditUserPortraitDialog.TakePhotoDialogListener() {
            @Override
            public void onAlbumOrGallerySelect(View view) {
                // step 真3:
                mTakePhotoHelper.onClick(view, getTakePhoto());
                mDialog.dismiss();
            }

            @Override
            public void onCameraTake(View view) {
                mTakePhotoHelper.onClick(view, getTakePhoto()); // view的id已经在helper中做出了处理.
                // 没必要在这里还做区分.
                mDialog.dismiss();
            }

            @Override
            public void onCancel(View view) {
                mDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mHeadImg:
                // step 3:
                if (mDialog != null) {
                    mDialog.show();
                }
                break;
        }
    }

    // step 2:

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        showImg(result.getImages()); // result中包含了图片,用于展示.
        ArrayList<TImage> images = result.getImages();
        Log.i(TAG, "path====>originalPath====>" + images.get(0).getOriginalPath()); // /storage/emulated/0/images/20170720_093602.jpg
        Log.i(TAG, "path====>compressPath====>" + images.get(0).getCompressPath()); // null
        Log.i(TAG, "path====>fromType====>" + images.get(0).getFromType()); // OTHER, CAMERA
        Glide.with(this)
                .load(new File(images.get(0).getOriginalPath()))
                .into(mHeadImg);
        Glide.with(this)
                .load(new File(images.get(0).getCompressPath()))
                .into(mImg1);
        String originalPath = images.get(0).getOriginalPath();
        String compressPath = images.get(0).getCompressPath();

//        Bitmap b1 = zoomImg(BitmapFactory.decodeFile(originalPath), 50, 50);
//        Bitmap b2 = zoomImg(BitmapFactory.decodeFile(compressPath), 130, 130);
        // 图片二次处理.
        Bitmap b1 = justDecodeBounds(originalPath, 1);
        Bitmap b2 = justDecodeBounds(compressPath, 1);
        Bitmap b3 = justDecodeBounds(originalPath, 4);
//        mHeadImg.setImageBitmap(b1);
//        mImg1.setImageBitmap(b2);
//        mImg2.setImageBitmap(b3);
        Toast.makeText(this, "takeSuccess", Toast.LENGTH_SHORT).show();
    }


    private Bitmap justDecodeBounds(String path, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 得到的Bitmap==null
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        Log.i(TAG, "originalWidth---->" + originalWidth + "originalHeight---->" + originalHeight);
        // 准备展示.
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = inSampleSize; // 缩放比.
        Bitmap output = BitmapFactory.decodeFile(path, options);
        return output;
    }


    // 缩放图片 static
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private void showImg(ArrayList<TImage> images) {
//        Intent intent=new Intent(this,ResultActivity.class);
//        intent.putExtra("images",images);
//        startActivity(intent);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Toast.makeText(this, "takeFail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        Toast.makeText(this, "takeCancel", Toast.LENGTH_SHORT).show();
    }
}
