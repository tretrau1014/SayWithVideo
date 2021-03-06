package datn.bkdn.com.saywithvideo.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Utils {
    private static final String CURRENT_USER = "current_user";
    private static final String CURRENT_USER_EMAIL = "email_key";
    private static final String CURRENT_USER_NAME = "name_key";
    private static final String CURRENT_USER_ID = "id_key";

    public static String getPrimaryEmail(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PermissionUtils.getrequestGetAccounts((Activity) context);
        }
        String possibleEmail = null;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context).getAccounts();
        Log.d("maill", accounts.length + "");
        if (accounts.length > 0) {
            if (emailPattern.matcher(accounts[0].name).matches()) {
                possibleEmail = accounts[0].name;
            }
        }
        return possibleEmail;
    }

    public static boolean setCurrentUsername(Context context, String name, String email, String id) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        String mail = pref.getString(CURRENT_USER_EMAIL, "null");
        String sname = pref.getString(CURRENT_USER_NAME, "null");
        if (mail.equals("null") && sname.equals("null")) {
            SharedPreferences.Editor editor = pref.edit();
            //editor.clear();
            editor.putString(CURRENT_USER_EMAIL, email);
            editor.putString(CURRENT_USER_NAME, name);
            editor.putString(CURRENT_USER_ID, id);
            editor.apply();
            return true;
        }
        return false;
    }

    public static String getCurrentUserEmail(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        return pref.getString(CURRENT_USER_EMAIL, "null");
    }

    public static String getCurrentUserName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        return pref.getString(CURRENT_USER_NAME, "null");
    }

    public static String getCurrentUserID(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        return pref.getString(CURRENT_USER_ID, "null");
    }

    public static void clearPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }


    public static void updateCurrentUserName(Context context, String name) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CURRENT_USER_NAME, name);
        editor.apply();
    }

    public static void updateCurrentEmail(Context context, String email) {
        SharedPreferences pref = context.getSharedPreferences(CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CURRENT_USER_EMAIL, email);
        editor.apply();
    }
}
