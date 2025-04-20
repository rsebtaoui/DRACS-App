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
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Download Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for download notifications");
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
                Toast.makeText(context, "enable the notification to receive the file path", Toast.LENGTH_SHORT).show();
                // Request notification permission if it's not enabled
                requestNotificationPermission(context);
                return;  // Exit if permission is not granted
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW).setData(fileUri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(fileUri, "application/pdf");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download Complete")
                .setContentText("File saved to: " + fileUri.getPath())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Request Notification Permission (API level 33+)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void requestNotificationPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Copy file from assets to external storage (MediaStore)
    public static void copyFileFromAssets(Context context, String fileName) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1 && !hasRequiredPermissions(context)) {
            requestPermissions(context);
            return; // Exit if permissions are not granted
        }

        createNotificationChannel(context);

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

                Toast.makeText(context, "File saved to Downloads!", Toast.LENGTH_SHORT).show();
                showDownloadNotification(context, uri);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            }
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
}
