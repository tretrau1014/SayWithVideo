package datn.bkdn.com.saywithvideo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.firebase.client.utilities.Base64;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import datn.bkdn.com.saywithvideo.firebase.FireBaseContent;
import datn.bkdn.com.saywithvideo.firebase.FirebaseConstant;
import datn.bkdn.com.saywithvideo.firebase.FirebaseUser;

public class AppTools {

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hhmmss_ddMMyyyy", Locale.US);
        return format.format(date);
    }

    public static String downloadAudio(String audioId, Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestWriteExtenalStorage(activity);
            return getContentAudio(audioId,activity);
        } else {
            return getContentAudio(audioId,activity);
        }
    }

    public static String getContentAudio(String audioId, final Activity context) {
        long start = System.currentTimeMillis();
        String link = FirebaseConstant.BASE_URL + FirebaseConstant.AUDIO_CONTENT_URL + "/" + audioId + ".json";
        String json = datn.bkdn.com.saywithvideo.network.Tools.getJson(link);
        FireBaseContent c = new Gson().fromJson(json, FireBaseContent.class);
        String content = c.getContent();
        String folderPath = Constant.DIRECTORY_PATH + Constant.AUDIO;
        createFolder(folderPath);
         String path_audio = folderPath + audioId + ".m4a";
        try {

            Base64.decodeToFile(content, path_audio);
            long end = System.currentTimeMillis();
            Log.d("timeExecute",""+(end - start)/1000);
            return path_audio;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static FirebaseUser getInfoUser(String id) {
        String link = FirebaseConstant.BASE_URL + FirebaseConstant.USER_URL + id + ".json";
        String json = datn.bkdn.com.saywithvideo.network.Tools.getJson(link);
        return new Gson().fromJson(json, FirebaseUser.class);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void createFolder(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return;
        } else {
            file.mkdirs();
        }
    }
}
