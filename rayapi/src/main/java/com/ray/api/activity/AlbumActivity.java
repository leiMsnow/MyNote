package com.ray.api.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ray.api.R;
import com.ray.api.adapter.AlbumAdapter;
import com.ray.api.model.PhotoData;
import com.ray.api.model.PhotoFolder;
import com.ray.api.utils.SDCardUtils;
import com.ray.api.utils.ScreenUtils;
import com.ray.api.utils.ToastUtil;
import com.ray.api.view.BaseProgressDialog;
import com.ray.api.view.PopupWindowListView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 相册选择
 */
public class AlbumActivity extends BaseActivity implements View.OnClickListener, PopupWindowListView.IPhotoDirSelectListener, AlbumAdapter.IPhotoSelectListener {


    private GridView mGVPhotos;
    private View mBottomView;
    private TextView mTVDirName;
    private TextView mTVDisplay;
    private PopupWindowListView mPopupWindowListView;
    //记录所有的文件夹路径
    private HashSet<String> mDirPaths;
    //所有图片文件集合
    private List<PhotoFolder> mPhotosFolders;

    //记录某个文件夹最大图片数量
    private int mPicSize = 0;
    //记录最大图片数量的文件夹路径
    private File mPicDir;
    //记录当前文件夹的图片集合
    private List<PhotoData> mPhotoDatas;

    private AlbumAdapter mAdapter;
    //被选中的图片集合
    private List<String> mSelectedImages;
    //图片最大选中数量
    private final int mMaxSelectCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        setContentView(R.layout.activity_album);

        mGVPhotos = (GridView) findViewById(R.id.gv_photos);
        mBottomView = findViewById(R.id.rl_bottom_panel);
        mTVDirName = (TextView) findViewById(R.id.tv_choose_dir);
        mTVDisplay = (TextView) findViewById(R.id.tv_display);
    }

    @Override
    protected void initData() {

        if (!SDCardUtils.isSDCardEnable()) {
            ToastUtil.getInstance(mContext).showToast(R.string.no_sdcard);
            return;
        }

        mProgressDialog = new BaseProgressDialog(mContext);
        mProgressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                //只查询jpeg和png的图片
                Cursor cursor = contentResolver.query(imageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + " = ? or " +
                                MediaStore.Images.Media.MIME_TYPE + " = ?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED
                );
                if (cursor != null) {
                    mDirPaths = new HashSet<String>();
                    mPhotosFolders = new ArrayList<PhotoFolder>();
                    while (cursor.moveToNext()) {
                        //获取图片路径
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        //获取该图片的父路径
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        PhotoFolder photoFolder = null;
                        //利用HashSet防止多次扫描同一个文件夹
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            //初始化photoFolder
                            photoFolder = new PhotoFolder();
                            photoFolder.setFirstPhotoPath(path);
                            photoFolder.setDirectory(dirPath);
                        }
                        if (parentFile.list() == null)
                            continue;
                        int picSize = parentFile.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String fileName) {
                                if (fileName.endsWith(".jpg") ||
                                        fileName.endsWith(".png") ||
                                        fileName.endsWith(".jpeg")) {
                                    return true;
                                }
                                return false;
                            }
                        }).length;
                        photoFolder.setPhotoCount(picSize);
                        mPhotosFolders.add(photoFolder);

                        if (picSize > mPicSize) {
                            mPicSize = picSize;
                            mPicDir = parentFile;
                        }
                    }
                    cursor.close();
                    //扫描完成，释放HashSet
                    mDirPaths = null;
                    //通知Handler扫描图片完成
                    mHandler.sendEmptyMessage(0);
                }

            }
        }).start();
    }

    @Override
    protected void initListener() {
        mTVDirName.setOnClickListener(this);
        mTVDisplay.setOnClickListener(this);
    }

    //初始化popup弹窗
    private void initDirPopupWindow() {
        mPopupWindowListView = new PopupWindowListView(LayoutInflater.from(mContext).inflate(R.layout.popupwindow_list_view, null),
                RelativeLayout.LayoutParams.MATCH_PARENT, ((int) (ScreenUtils.getScreenHeight(mContext) * 0.8)), mPhotosFolders);
        mPopupWindowListView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        mPopupWindowListView.setiPhotoDirSelectListener(this);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();
            mAdapter = new AlbumAdapter(mContext, R.layout.item_gridview_album, mPhotoDatas);
            mGVPhotos.setAdapter(mAdapter);
            mSelectedImages = new ArrayList<String>();
            //绑定数据
            dataBindView();
            initDirPopupWindow();
        }
    };

    //绑定data到view
    private void dataBindView() {
        if (mPicDir == null) {
            ToastUtil.getInstance(mContext).showToast(R.string.photo_empty);
            return;
        }
        List<String> imgs = Arrays.asList(mPicDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        mPhotoDatas = new ArrayList<PhotoData>();
        for (int i = 0; i < imgs.size(); i++) {
            PhotoData photo = new PhotoData();
            photo.setIndex(i);
            photo.setUri(imgs.get(i));
            mPhotoDatas.add(photo);
        }
        mAdapter.setSelectPhotoListener(this);
        mAdapter.setDirPath(mPicDir.getAbsolutePath());
        mAdapter.replaceAll(mPhotoDatas);
        mTVDirName.setVisibility(View.VISIBLE);
        mTVDirName.setText(mPicDir.getName());

    }

    @Override
    public void onClick(View view) {

        if (view == mTVDirName) {
            mPopupWindowListView.setAnimationStyle(R.style.anim_popup_dir);
            mPopupWindowListView.showAsDropDown(mBottomView, 0, 0);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.3f;
            getWindow().setAttributes(lp);
        } else if (view == mTVDisplay) {
            ToastUtil.getInstance(mContext).showToast("hhh");
        }

    }

    @Override
    public void onPhotoDirSelected(PhotoFolder photoFolder) {
        mPopupWindowListView.dismiss();
        mPicDir = new File(photoFolder.getDirectory());
        mPicSize = photoFolder.getPhotoCount();
        dataBindView();
    }

    @Override
    public void onPhotoSelected(PhotoData photoData) {
        mAdapter.getItem(photoData.getIndex()).setSelect(!photoData.isSelect());
        mAdapter.notifyDataSetChanged();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mAdapter.getItem(i).isSelect()) {
                mTVDisplay.setEnabled(true);
                break;
            } else {
                mTVDisplay.setEnabled(false);
            }
        }
    }
}
