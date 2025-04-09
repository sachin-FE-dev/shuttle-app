package com.hackathon.metropolisshuttle;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Kavya Shravan on 03/04/25.
 */
public class Utils {

    public static String getUtcTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Format to ISO 8601
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void saveString(Context context, String data, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void showProgressDialog(ProgressDialog progressDialog) {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
