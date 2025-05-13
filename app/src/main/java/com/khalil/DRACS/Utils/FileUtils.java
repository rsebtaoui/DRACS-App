package com.khalil.DRACS.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1;

    // Create Notification Channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "قناة التحميل", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("قناة إشعارات التحميل");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Show Download Notification
    public static void showDownloadNotification(Context context, Uri fileUri) {
        createNotificationChannel(context);

        // Check if we need to ask for notification permission (API level 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // Request notification permission if it's not enabled
                requestNotificationPermission(context);
                Toast.makeText(context, "يرجى تفعيل الإشعارات للاستمرار", Toast.LENGTH_LONG).show();
                return;  // Exit if permission is not granted
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW).setData(fileUri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(fileUri, "application/pdf");

        // Use FLAG_IMMUTABLE for Android 12 and above
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("اكتمل التحميل")
                .setContentText("تم حفظ الملف في: " + fileUri.getPath())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Add toast message
        Toast.makeText(context, "تم تحميل الملف بنجاح!", Toast.LENGTH_SHORT).show();
    }

    // Request Notification Permission (API level 33+)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void requestNotificationPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Toast.makeText(context, "يرجى تفعيل الإشعارات للاستمرار", Toast.LENGTH_LONG).show();
    }

    // Copy file from assets to external storage (MediaStore)
    public static void copyFileFromAssets(Context context, String fileName) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1 && !hasRequiredPermissions(context)) {
            requestPermissions(context);
            Toast.makeText(context, "يرجى منح أذونات التخزين للاستمرار", Toast.LENGTH_LONG).show();
            return; // Exit if permissions are not granted
        }

        createNotificationChannel(context);

        // Show starting toast
        Toast.makeText(context, "جاري بدء التحميل...", Toast.LENGTH_SHORT).show();

        // Generate a unique file name
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                 InputStream inputStream = context.getAssets().open(fileName)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    assert outputStream != null;
                    outputStream.write(buffer, 0, length);
                }

                showDownloadNotification(context, uri);

            } catch (IOException e) {
                Toast.makeText(context, "حدث خطأ أثناء التحميل", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "فشل في إنشاء الملف", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if required permissions are granted
    private static boolean hasRequiredPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
    private static void requestPermissions(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    public static void openGoogleMaps(Context context, double latitude, double longitude, String label) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + label);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mapIntent);
    }

    public static void handleAction(Context context, String actionType, String actionValue) {
        switch (actionType) {
            case "download":
                copyFileFromAssets(context, actionValue);
                break;
            case "map":
                String[] coords = actionValue.split(",");
                if (coords.length == 2) {
                    double lat = Double.parseDouble(coords[0]);
                    double lng = Double.parseDouble(coords[1]);
                    openGoogleMaps(context, lat, lng, "");
                }
                break;
            case "youtube":
                openYouTube(context, actionValue);
                break;
            default:
        }
    }

    private static void openYouTube(Context context, String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoId));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + videoId));
        }
        context.startActivity(intent);
    }
}
