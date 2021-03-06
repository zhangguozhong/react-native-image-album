
package com.library.photo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RNImageAlbumModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static Callback doCallback = null;

    public RNImageAlbumModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNImageAlbum";
    }

    @ReactMethod
    public void saveImageWithUrl(final String url, Callback callback) {
        doCallback = callback;
        if (this.validateUrl(url)) {
            PermissionUtils.permissionsCheck(getCurrentActivity(),new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },new PermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    new Task().execute(url);
                }
                @Override
                public void onPermissionDenied(String[] deniedPermissions) {
                    Toast.makeText(getCurrentActivity(),"读写权限不够,无法下载！",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateUrl(String url) {
        //验证传入的imageUrl是否合法
        if (url == null || url.isEmpty()) {
            if (doCallback != null) {
                doCallback.invoke("url参数为空");
                return false;
            }
        }
        return true;
    }

    private Bitmap GetImageInputStream(String imageUrl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param path 本地路径
     */
    private void SaveImage(Bitmap bitmap, String path) {
        File appDir = new File(path);
        if (!appDir.exists()) { //文件夹不存在，则创建它
            appDir.mkdir();
        }

        final String fileName = System.currentTimeMillis() + ".jpg";
        final File file = new File(path, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);

            bos.flush();
            bos.close();
            MediaScannerConnection.scanFile(getReactApplicationContext(), new String[] { file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    if(uri == null) {
                        if (doCallback != null) {
                            doCallback.invoke("添加图片错误");
                        }
                    } else {
                        try {
                            MediaStore.Images.Media.insertImage(getReactApplicationContext().getContentResolver(),
                                    path, fileName, null);//保存到图库
                            if (doCallback != null) {
                                doCallback.invoke("成功保存图片到相册");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        getReactApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));//广播刷新图库
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (doCallback != null) {
                doCallback.invoke(e.getMessage());
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask<String, Integer, Void> {
        protected Void doInBackground(String... params) {
            Bitmap bitmap = GetImageInputStream((String) params[0]);
            if (bitmap != null) {
                SaveImage(bitmap,Environment.getExternalStorageDirectory().getPath() + "/" + getReactApplicationContext().getPackageName());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
        }
    }
}