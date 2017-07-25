package cn.com.bjx.takephototest;

import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * @File FileName
 * @Function 把takePhoto 的逻辑处理,与view在这里处理.
 * @Author lwj.
 * @Time 2017/7/21.
 * @Copyright 2017 Polaris.
 */

public class TakePhotoHelper { // 功能等同于EditUserPortraitDialog 的强化版功能.

    //    private View rootView;
    //    private RadioGroup rgCrop, rgCompress, rgFrom, rgCropSize, rgCropTool, rgShowProgressBar, rgPickTool, rgCompressTool, rgCorrectTool, rgRawFile;
    //    private EditText etCropHeight, etCropWidth, etLimit, etSize, etHeightPx, etWidthPx;
    // 是否裁剪.
    private boolean cropOrNot;
    // 裁剪的宽高
    private int cropWidth;
    private int cropHeight;
    // 使用自带裁剪工具.
    private boolean withOwnCrop;
    // 等比缩放.
    private boolean selectAndUniformScale;

    public TakePhotoHelper() {
        this(true, 200, 200, true, true);
    }

    /**
     * 多参构造方法控制.
     *
     * @param cropOrNot             是否crop
     * @param cropWidth             crop的宽度
     * @param cropHeight            crop的高度
     * @param withOwnCrop           使用自带的裁剪工具/第三方的裁剪工具.
     * @param selectAndUniformScale 同比缩放.
     */
    public TakePhotoHelper(boolean cropOrNot, int cropWidth, int cropHeight,
                           boolean withOwnCrop, boolean selectAndUniformScale) {
//        this.rootView = rootView;
        this.cropOrNot = cropOrNot;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.withOwnCrop = withOwnCrop;
        this.selectAndUniformScale = selectAndUniformScale;
        init();
    }

    /**
     * 等同于newInstance
     *
     * @return TakePhotoHelper.
     */
    public static TakePhotoHelper of() {
        return new TakePhotoHelper();
    }

    // 这个类的过渡不需要用户感知,可将配置在这里就设置好.
    private void init() {

    }

    public void onClick(View view, TakePhoto takePhoto) {
        // 选择头像的时候,1 张,不从 Document 取,从自带相册取.
        // 选择多张的时候,默认从 Gallery 取.
        onClick(view, takePhoto, 1, false);
    }

    /**
     * 点击选择:相册/照相机
     *
     * @param view
     * @param takePhoto
     * @param limit
     * @param fromDocumentOrGallery 来源类型,文件/相册.document/gallery
     */
    public void onClick(View view, TakePhoto takePhoto, int limit, boolean fromDocumentOrGallery) {
        // /storage/emulated/0 ====================>/temp/1500618909538.jpg
        File file = new File(Environment.getExternalStorageDirectory(),
                "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        // 配置压缩.
        configCompress(takePhoto);
        // 配置takePhoto选项.
        configTakePhotoOption(takePhoto);
        switch (view.getId()) {
            case R.id.tv_select_photo:
                if (limit > 1) {
                    if (cropOrNot) {
                        takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                    } else {
                        takePhoto.onPickMultiple(limit);
                    }
                    return;
                }
                if (fromDocumentOrGallery) { // 从文件选择
                    if (cropOrNot) { // 是否裁剪.
                        takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
                    } else {
                        takePhoto.onPickFromDocuments();
                    }
                    return;
                } else {    // 从相册选择.
                    if (cropOrNot) {
                        takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                    } else {
                        takePhoto.onPickFromGallery();
                    }
                }
                break;
            case R.id.tv_camera:
                if (cropOrNot) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromCapture(imageUri);
                }
                break;
            default:
                // eg:取消按钮.
                break;
        }
    }

    private void configCompress(TakePhoto takePhoto) {
        // 102400B 约等于 1M
        configCompress(takePhoto, true, 102400, 200, 200, true, true, true);
    }

    // 配置压缩.rg的选择置换为boolean为控制判断,由程序员控制,不需要对用户暴露.
    // 先给出参数最全的方法,随后少参重载.

    /**
     * 配置压缩参数.
     *
     * @param takePhoto       TakePhoto的实现类.
     * @param compressOrNot   是否压缩
     * @param maxSize         最大尺寸(B)
     * @param width           压缩宽(px)
     * @param height          压缩高(px)
     * @param showProgressBar 压缩时是否显示进度对话框
     * @param enableRawFile   是否保留未裁剪的原文件.
     * @param compressWithOwn 使用自带压缩工具.
     */
    private void configCompress(TakePhoto takePhoto, boolean compressOrNot, int maxSize, int width, int height
            , boolean showProgressBar, boolean enableRawFile, boolean compressWithOwn) {
        if (!compressOrNot) {
            takePhoto.onEnableCompress(null, false); // false:不显示压缩dialog.
            return;
        }
        CompressConfig config;
        // 直接使用自带takePhoto自带配置.===>更流畅.
        if (compressWithOwn) {
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile); // reserve:保留,储备.是否保留原图.
        }
        takePhoto.onEnableCompress(config, showProgressBar); // showProgressBar展示压缩进度条.
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        configTakePhotoOption(takePhoto, true, true);
    }

    // 配置takePhoto选项.
    private void configTakePhotoOption(TakePhoto takePhoto, boolean pickWithOwnGallery, boolean correctImage) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        // 使用takePhoto自带相册.
        if (pickWithOwnGallery) {
            builder.setWithOwnGallery(true);
        }
        // 纠正照片.
        if (correctImage) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());
    }


    // 获得裁剪选项.
    private CropOptions getCropOptions() {
        if (!cropOrNot) return null;
        CropOptions.Builder builder = new CropOptions.Builder();
        if (selectAndUniformScale) {
            // 宽/高===>宽高等比缩放.
            builder.setAspectX(cropWidth).setAspectY(cropHeight);
        } else {
            // 宽x高===>宽高独立缩放.
            builder.setOutputX(cropWidth).setOutputY(cropWidth);
        }
        builder.setWithOwnCrop(withOwnCrop);
        return builder.create();
    }
}
