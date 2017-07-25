package cn.com.bjx.takephototest;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 编辑用户头像dialog
 * 2017年7月7日15:07:16 lwj===>略微修改.
 * <p>
 * Created by 周广亚 on 2017/02/20.
 */
public class EditUserPortraitDialog extends AlertDialog implements View.OnClickListener {
    private static final String TAG = "EditUserPortraitDialog";
    private Context context;
    private TakePhotoDialogListener mTakePhotoDialogListener;

    /**
     * @param context
     * @param listener dialog 监听
     */
    public EditUserPortraitDialog(Context context,
                                  TakePhotoDialogListener listener) {
        // 于此定义了一个样式,设置了Dialog的外边距.
        super(context, R.style.NoBgDialog);
        this.context = context;
        this.mTakePhotoDialogListener = listener;
        controlDialog(this);
    }

    /**
     * 控制dialog底部显示
     *
     * @param dialog dialog
     */
    private void controlDialog(AlertDialog dialog) {
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_common, null);
        this.setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        TextView tvDialogSelect = (TextView) view.findViewById(R.id.tv_select_photo);
        TextView tvDialogCamera = (TextView) view.findViewById(R.id.tv_camera);
        TextView tvDialogCancel = (TextView) view.findViewById(R.id.tv_common_dialog_cancel);
        tvDialogSelect.setOnClickListener(this);
        tvDialogCamera.setOnClickListener(this);
        tvDialogCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_photo:
                mTakePhotoDialogListener.onAlbumOrGallerySelect(v);
                break;
            case R.id.tv_camera:
                mTakePhotoDialogListener.onCameraTake(v);
                break;
            case R.id.tv_common_dialog_cancel:
                mTakePhotoDialogListener.onCancel(v);
                break;
        }
    }

    public interface TakePhotoDialogListener {
        /**
         * 相册选择
         */
        void onAlbumOrGallerySelect(View view);

        /**
         * 相机拍照
         *
         * @param view 控件
         */
        void onCameraTake(View view);

        /**
         * 取消选择
         *
         * @param view 控件
         */
        void onCancel(View view);
    }
}
