package com.khalil.DRACS.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

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
        if (fileName.startsWith("https://drive.google.com/")) {
            handleGoogleDriveDownload(context, fileName);
        } else {
            handleLocalFileCopy(context, fileName);
        }
    }

    private static void handleGoogleDriveDownload(Context context, String fileName) {
        String fileId = extractGoogleDriveFileId(fileName);
        if (fileId == null) {
            Toast.makeText(context, "رابط غير صالح", Toast.LENGTH_LONG).show();
            return;
        }

        // Handle permissions based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above - no storage permission needed for Downloads
            downloadFileForAndroid10AndAbove(context, fileId);
        } else {
            // Android 9 and below - need storage permissions
            if (!hasRequiredPermissions(context)) {
                requestPermissions((Activity) context);
                return;
            }
            downloadFileForLegacyAndroid(context, fileId);
        }
    }

    private static void downloadFileForAndroid10AndAbove(Context context, String fileId) {
        File outputFile = prepareOutputFile(context);
        if (outputFile == null) return;

        downloadAndOpenFile(context, fileId, outputFile);
    }

    private static void downloadFileForLegacyAndroid(Context context, String fileId) {
        File outputFile = prepareOutputFile(context);
        if (outputFile == null) return;

        downloadAndOpenFile(context, fileId, outputFile);
    }

    private static File prepareOutputFile(Context context) {
        File downloadDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10 and above
            downloadDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
        } else {
            // Use direct file access for Android 9 and below
            downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }

        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            Toast.makeText(context, "لا يمكن إنشاء مجلد التنزيلات", Toast.LENGTH_LONG).show();
            return null;
        }

        String outputFileName = "document_" + System.currentTimeMillis() + ".pdf";
        return new File(downloadDir, outputFileName);
    }

    private static void downloadAndOpenFile(Context context, String fileId, File outputFile) {
        // Check network connectivity first
        if (!ConnectionUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "تحتاج إلى اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            return;
        }

        // Show downloading toast
        ((Activity) context).runOnUiThread(() -> {
            Toast.makeText(context, "جاري التحميل...", Toast.LENGTH_SHORT).show();
        });

        new Thread(() -> {
            try {
                HttpURLConnection connection = setupConnection(fileId);
                if (connection == null) {
                    showErrorOnMainThread(context, "فشل الاتصال بالخادم");
                    return;
                }

                if (downloadFile(connection, outputFile)) {
                    handleSuccessfulDownload(context, outputFile);
                } else {
                    showErrorOnMainThread(context, "فشل تحميل الملف");
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                FirebaseCrashlytics.getInstance().log("Error downloading file: " + fileId);
                showErrorOnMainThread(context, "حدث خطأ أثناء التحميل");
                e.printStackTrace();
            }
        }).start();
    }

    private static HttpURLConnection setupConnection(String fileId) {
        try {
            String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            return connection;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            FirebaseCrashlytics.getInstance().log("Error setting up connection for file: " + fileId);
            e.printStackTrace();
            return null;
        }
    }

    private static boolean downloadFile(HttpURLConnection connection, File outputFile) {
        try (InputStream in = connection.getInputStream();
             OutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return outputFile.exists() && outputFile.length() > 0;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            FirebaseCrashlytics.getInstance().log("Error downloading file to: " + outputFile.getAbsolutePath());
            e.printStackTrace();
            return false;
        }
    }

    private static void handleSuccessfulDownload(Context context, File outputFile) {
        ((Activity) context).runOnUiThread(() -> {
            Toast.makeText(context, "تم تحميل الملف بنجاح", Toast.LENGTH_LONG).show();
            showDownloadNotification(context, outputFile);
            openPdfFile(context, outputFile);
        });
    }

    private static void showDownloadNotification(Context context, File outputFile) {
        try {
            // Handle notification permissions based on API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 and above - need notification permission
                if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    Toast.makeText(context, "يرجى تفعيل الإشعارات للاستمرار", Toast.LENGTH_LONG).show();
                    return;
                }
        }

        createNotificationChannel(context);
            Intent intent = createPdfViewIntent(context, outputFile);
            
            // Handle PendingIntent flags based on API level
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                flags
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("اكتمل التحميل")
                .setContentText("تم حفظ الملف في مجلد التنزيلات")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(context, "لا يمكن عرض الإشعار - يرجى التحقق من الأذونات", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private static Intent createPdfViewIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri;
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7 and above - use FileProvider
                fileUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
                );
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                // Android 6 and below - use direct file URI
                fileUri = Uri.fromFile(file);
            }
            
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "لا يمكن الوصول إلى الملف", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    private static void openPdfFile(Context context, File file) {
        Intent intent = createPdfViewIntent(context, file);
        if (intent == null) return;

        try {
            List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (!activities.isEmpty()) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "لم يتم العثور على عارض PDF", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(context, "لا يمكن فتح الملف - يرجى التحقق من الأذونات", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private static void showErrorOnMainThread(Context context, String message) {
        ((Activity) context).runOnUiThread(() -> {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        });
    }

    private static void handleLocalFileCopy(Context context, String fileName) {
        // Check for storage permissions
        if (!hasRequiredPermissions(context)) {
            requestPermissions((Activity) context);
            return;
        }

        try {
            // First check if the file exists in assets
            String[] files = context.getAssets().list("");
            boolean fileFound = false;
            for (String file : files) {
                if (file.equals(fileName)) {
                    fileFound = true;
                    break;
                }
            }

            if (!fileFound) {
                FirebaseCrashlytics.getInstance().log("File not found in assets: " + fileName);
                Toast.makeText(context, "لم يتم العثور على الملف: " + fileName, Toast.LENGTH_LONG).show();
                return;
            }

            // Get the Downloads directory
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadDir.exists()) {
                if (!downloadDir.mkdirs()) {
                    FirebaseCrashlytics.getInstance().log("Failed to create downloads directory");
                    Toast.makeText(context, "لا يمكن إنشاء مجلد التنزيلات", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Create the output file
            File outputFile = new File(downloadDir, fileName);

            // Try to open the input file first
            try {
                context.getAssets().open(fileName);
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                FirebaseCrashlytics.getInstance().log("Error opening asset file: " + fileName);
                Toast.makeText(context, "لا يمكن فتح الملف: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            // Copy the file
            try (InputStream in = context.getAssets().open(fileName);
                 OutputStream out = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[1024];
                int read;
                long totalBytes = 0;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    totalBytes += read;
                }

                // Verify the file was created
                if (outputFile.exists() && outputFile.length() > 0) {
                    Toast.makeText(context, "تم تحميل الملف بنجاح", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseCrashlytics.getInstance().log("File copy failed - file not created or empty: " + fileName);
                    Toast.makeText(context, "فشل حفظ الملف", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                FirebaseCrashlytics.getInstance().log("Error copying file: " + fileName);
                Toast.makeText(context, "خطأ أثناء النسخ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            FirebaseCrashlytics.getInstance().log("Unexpected error in handleLocalFileCopy: " + fileName);
            Toast.makeText(context, "حدث خطأ غير متوقع: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private static String extractGoogleDriveFileId(String url) {
        // Handle different Google Drive URL formats
        if (url.contains("/d/")) {
            // Format: https://drive.google.com/file/d/FILE_ID/view
            String[] parts = url.split("/d/");
            if (parts.length > 1) {
                String fileId = parts[1].split("/")[0];
                return fileId;
            }
        } else if (url.contains("id=")) {
            // Format: https://drive.google.com/open?id=FILE_ID
            String[] parts = url.split("id=");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }

    // Check if required permissions are granted
    private static boolean hasRequiredPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above - no storage permissions needed for Downloads
            return true;
        } else {
            // Android 9 and below - need storage permissions
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    // Request permissions
    private static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above - no storage permissions needed
            return;
        }

        // Android 9 and below - request storage permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "يجب منح أذونات التخزين لتحميل الملفات", Toast.LENGTH_LONG).show();
        }
        
        ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);
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
