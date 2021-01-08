package com.shoppr.shoper.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CommonUtils {


    private static final String TAG = CommonUtils.class.getSimpleName();
    public static boolean accept;
    private static ProgressDialog dialogProgress;
    private static Dialog dialog;
    //2016-04-08T12:18:06.523Z
    public final static String SERVICE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DISPLAY_DATE = "dd MMM, hh:mm aaa";


    public final static String DISPLAY_DATE_FORMAT = "yyyy-MM-dd";
    public final static String DISPLAY_DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm aaa";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            @SuppressWarnings("HardCodedStringLiteral") Uri baseUri = Uri.parse("content://media/external/images/media"); //NON-NLS
            //noinspection HardCodedStringLiteral

            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                //noinspection HardCodedStringLiteral

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static <T> boolean CheckIfNull(T objectToCheck) {
        return objectToCheck == null ? true : false;
    }

    public static String getDateOfTimestamp(long timeStamp) {
        java.text.DateFormat objFormatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar objCalendar = Calendar.getInstance();

        objCalendar.setTimeInMillis(timeStamp * 1000);//edit
        String result = objFormatter.format(objCalendar.getTime());
        objCalendar.clear();
        return result;
    }

    /* public static String getImeiNumber(Activity activity) {
         TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
         if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
             // TODO: Consider calling
             //    ActivityCompat#requestPermissions
             // here to request the missing permissions, and then overriding
             //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
             //                                          int[] grantResults)
             // to handle the case where the user grants the permission. See the documentation
             // for ActivityCompat#requestPermissions for more details.
         }
         String imei_number = telephonyManager.getDeviceId();
         Log.e(TAG, "IMEI NUMBER<><<><><>:" + imei_number);
         return imei_number;
     }*/

    public static int getIntPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }

    public static void showAlertOk(String message, final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                context.finish();
                            }
                        });

        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ProgressDialog(String tittle, String message, Activity context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle(tittle);
        // set dialog message
        alertDialogBuilder.setMessage(message).setCancelable(false);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        // After some action
        alertDialog.dismiss();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void setTextOnView(@Nullable TextView tvView, @Nullable String value) {
        tvView.setText(value);
    }

    public static void savePreferencesString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void savePreferencesBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPreferencesBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getPreferencesString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void removePreferences(Activity context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
    }

    public static String getTime(String timestamp_in_string) {
        long dv = Long.valueOf(timestamp_in_string) * 1000L;// its need to be in millisecond
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(dv);
        String date = DateFormat.format("hh:mm a", cal).toString();
        return date;
    }

    public static String bitMapToString(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }



    public static String getDateDiffString(String startDate, String endDate) {
        java.text.DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date date1 = (Date) formatter.parse(startDate);
            Date date2 = (Date) formatter.parse(endDate);
            long diff = date2.getTime() - date1.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return String.valueOf(days);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFragment(Fragment fragment, boolean removeStack, FragmentActivity activity, int mContainer) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
        if (removeStack) {
            int size = fragmentManager.getBackStackEntryCount();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ftTransaction.replace(mContainer, fragment);
        } else {
            ftTransaction.replace(mContainer, fragment);
            ftTransaction.addToBackStack(null);
        }
        ftTransaction.commit();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Called for checking internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void disMissProgressDialog(Context mContext) {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
            dialogProgress = null;
        }
    }


    public static void showProgressDialog(Context context, String msg) {
        if (context != null) {
            dialogProgress = new ProgressDialog(context);
            dialogProgress.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //   dialogProgress.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.my_progress_indeterminate));
            dialogProgress.setMessage(msg);
            if (!dialogProgress.isShowing()) {
                try {
                    dialogProgress.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dialogProgress.setCancelable(false);

        }
    }

    public static void printLog(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastInCenter(Activity activity, String msg){
        Toast toast = Toast.makeText(activity,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String getDate(String serverDate) {
        try {
            long dt = Long.parseLong(serverDate);
            dt = dt * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(dt);
            String date = DateFormat.format("EEE, dd MMM yyyy", cal).toString();
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }

    public static String getDateTime(String serverDate) {
        try {
            long dt = Long.parseLong(serverDate);
            dt = dt * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(dt);
            String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }

    }

    public Date dateConverter(String date1) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateInString = date1;
        Date date;
        try {
            date = formatter.parse(dateInString);
            System.out.println(date);
            System.out.println(formatter.format(date));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static boolean isValidPhone(CharSequence target) {
        if (target == null) {
            return false;
        } else {

            return android.util.Patterns.PHONE.matcher(target)
                    .matches() && (target.length() >= 7 && target.length() <= 20);
        }
    }

    /*To hide keyboard*/
    public static void hideKeyPad(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    public static String getDateFormat(String serverDate) {

        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT, Locale.ENGLISH);
            originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat targetFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
            targetFormat.setTimeZone(TimeZone.getDefault());
            Date date = originalFormat.parse(serverDate);
            String formattedDate = targetFormat.format(date);

            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void setSnackbar(View view, String msg){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setDuration(4000);
    }

    public static void setNewSnackbar(View view, String msg){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setDuration(9000);
    }

    public static void setSnackbarWithActionButton(View view, String actionString){
        Snackbar snackbar = Snackbar
                .make(view, actionString/*"No internet connection!"*/, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

// Changing message text color
        snackbar.setActionTextColor(Color.RED);

// Changing action button text color
        View sbView = snackbar.getView();
            /*TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);*/
        snackbar.show();
    }



    public static long getTimestamp(String strDate, String serviceDateFormat){

        long timestamp=0;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(serviceDateFormat);
            Date date = (Date)formatter.parse(strDate);
            timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp/1000;

    }

    public static String getDateTimeFromTimestamp(long timestamp, String format) {
        try {

            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(timestamp);
            String date = DateFormat.format(format, cal).toString();
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getTimeFromTimestamp(long timestamp, String format) {
        try {

            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(timestamp);
            String date = DateFormat.format(format, cal).toString();
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }


    public static void clearNotification(int notificationID, Context mContext){
        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }


    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_SIZE=720;

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }


    public static String encodeTobase64(Bitmap image) {
        String encodedImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
            Log.d("base64Encode", encodedImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodedImage;
    }
}
