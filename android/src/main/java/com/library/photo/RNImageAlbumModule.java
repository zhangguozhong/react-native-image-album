
package com.library.photo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RNImageAlbumModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private Callback successCallback = null;
    private static String suffix;

    public RNImageAlbumModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNImageAlbum";
    }

    @ReactMethod
    public void saveToAlbum(String imageUrl, Callback callback) {
        this.successCallback = callback;
        suffix = this.getSuffix(imageUrl);

        if (this.validateImageUrl(imageUrl)) {
            new Task().execute(imageUrl);
        }
    }


    private String getSuffix(String imageUrl) {
        String suffix = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
        if(suffix.contains("?")) {
            suffix = suffix.substring(0, suffix.indexOf("?"));
        }
        return suffix;
    }


    private boolean validateImageUrl(String imageUrl) {
        //验证传入的imageUrl是否合法
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (this.successCallback != null) {
                this.successCallback.invoke("imageUrl参数为空");
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
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" + System.currentTimeMillis() + "." + suffix);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            Bitmap.CompressFormat type;
            switch (suffix.toLowerCase()) {
                case "jpg":
                    type = Bitmap.CompressFormat.JPEG;
                    break;
                case "jpeg":
                    type = Bitmap.CompressFormat.JPEG;
                    break;
                case "png":
                    type = Bitmap.CompressFormat.PNG;
                    break;
                case "webp":
                    type = Bitmap.CompressFormat.WEBP;
                    break;
                default:
                    type = null;
            }

            if( type != null ) {
                bitmap.compress(type,100, bos);
            }
            fileOutputStream.close();
            bos.flush();
            bos.close();

//            MediaScannerConnection.scanFile(mContext, new String[] { imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                public void onScanCompleted(String path, Uri uri)
//                {
//                    if( uri == null ){
//                        saveMessage = "添加图片错误";
//                    }
//                }
//            });

            if (this.successCallback != null) {
                this.successCallback.invoke("成功保存图片到相册");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (this.successCallback != null) {
                this.successCallback.invoke(e.getMessage());
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