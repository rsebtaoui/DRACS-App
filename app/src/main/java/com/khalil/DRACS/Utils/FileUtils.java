package com.khalil.DRACS.Utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.khalil.DRACS.Activities.Activity_main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {

    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1;

    private FileUtils() {
    }

    public static void copyFileFromAssets(Activity activity, String fileName) {
        if (fileName.startsWith("https://drive.google.com/")) {
            handleGoogleDriveDownload(activity, fileName);
        } else {
            handleLocalFileCopy(activity, fileName);
        }
    }

    private static void handleGoogleDriveDownload(Activity activity, String fileName) {
        String fileId = extractGoogleDriveFileId(fileName);
        if (fileId == null) {
            Toast.makeText(activity, "رابط غير صالح", Toast.LENGTH_LONG).show();
            return;
        }

        File outputFile = prepareOutputFile(activity, "document_" + System.currentTimeMillis() + ".pdf");
        if (outputFile == null) {
            Toast.makeText(activity, "لا يمكن إنشاء مجلد التنزيلات", Toast.LENGTH_LONG).show();
            return;
        }

        downloadAndOpenFile(activity, fileId, outputFile);
    }

    private static void handleLocalFileCopy(Activity activity, String fileName) {
        try {
            String[] files = activity.getAssets().list("");
            boolean fileFound = false;
            if (files != null) {
                for (String file : files) {
                    if (file.equals(fileName)) {
                        fileFound = true;
                        break;
                    }
                }
            }

            if (!fileFound) {
                FirebaseCrashlytics.getInstance().log("File not found in assets: " + fileName);
                Toast.makeText(activity, "الملف غير متوفر حالياً", Toast.LENGTH_LONG).show();
                return;
            }

            File outputFile = prepareOutputFile(activity, fileName);
            if (outputFile == null) {
                Toast.makeText(activity, "لا يمكن إنشاء مجلد التنزيلات", Toast.LENGTH_LONG).show();
                return;
            }

            try (InputStream in = activity.getAssets().open(fileName);
                 OutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }

            if (outputFile.exists() && outputFile.length() > 0) {
                onDownloadComplete(activity, outputFile);
            } else {
                FirebaseCrashlytics.getInstance().log("File copy failed - file not created or empty: " + fileName);
                Toast.makeText(activity, "فشل حفظ الملف", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            FirebaseCrashlytics.getInstance().log("Error copying file: " + fileName);
            Toast.makeText(activity, "خطأ أثناء النسخ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(activity, "حدث خطأ غير متوقع: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * App-specific external downloads directory — no storage runtime permissions required.
     */
    private static File prepareOutputFile(Context context, String fileName) {
        File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir == null) {
            downloadDir = new File(context.getFilesDir(), "downloads");
        }
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            return null;
        }
        return new File(downloadDir, sanitizeFileName(fileName));
    }

    private static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "document_" + System.currentTimeMillis() + ".pdf";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static void downloadAndOpenFile(Activity activity, String fileId, File outputFile) {
        if (!ConnectionUtils.isNetworkAvailable(activity)) {
            Toast.makeText(activity, "تحتاج إلى اتصال بالإنترنت", Toast.LENGTH_LONG).show();
            return;
        }

        activity.runOnUiThread(() ->
                Toast.makeText(activity, "جاري التحميل...", Toast.LENGTH_SHORT).show());

        new Thread(() -> {
            try {
                HttpURLConnection connection = setupConnection(fileId);
                if (connection == null) {
                    showErrorOnMainThread(activity, "فشل الاتصال بالخادم");
                    return;
                }

                if (downloadFile(connection, outputFile)) {
                    activity.runOnUiThread(() -> onDownloadComplete(activity, outputFile));
                } else {
                    showErrorOnMainThread(activity, "فشل تحميل الملف");
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                FirebaseCrashlytics.getInstance().log("Error downloading file: " + fileId);
                showErrorOnMainThread(activity, "حدث خطأ أثناء التحميل");
            }
        }).start();
    }

    private static void onDownloadComplete(Activity activity, File outputFile) {
        Toast.makeText(activity, "تم تحميل الملف بنجاح", Toast.LENGTH_LONG).show();
        showDownloadNotification(activity, outputFile);
        openPdfFile(activity, outputFile);
    }

    private static HttpURLConnection setupConnection(String fileId) throws IOException {
        String downloadUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        return connection;
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
            return false;
        }
    }

    private static void showDownloadNotification(Activity activity, File outputFile) {
        Runnable showNotification = () -> {
            createNotificationChannel(activity);
            Intent intent = createPdfViewIntent(activity, outputFile);
            if (intent == null) {
                return;
            }

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, flags);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle("اكتمل التحميل")
                    .setContentText("تم حفظ الملف بنجاح")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        };

        if (activity instanceof Activity_main) {
            ((Activity_main) activity).runWithNotificationPermission(showNotification);
        } else {
            showNotification.run();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "قناة التحميل",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("قناة إشعارات التحميل");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private static Intent createPdfViewIntent(Context context, File file) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        } catch (IllegalArgumentException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(context, "لا يمكن الوصول إلى الملف", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private static void openPdfFile(Context context, File file) {
        Intent intent = createPdfViewIntent(context, file);
        if (intent == null) {
            return;
        }
        try {
            Intent chooser = Intent.createChooser(intent, "اختر تطبيق لفتح ملف PDF");
            context.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "لا يوجد تطبيق لفتح ملفات PDF", Toast.LENGTH_LONG).show();
            FirebaseCrashlytics.getInstance().recordException(e);
            openPdfViewerInPlayStore(context);
        }
    }

    private static void openPdfViewerInPlayStore(Context context) {
        try {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
            playStoreIntent.setData(Uri.parse("market://search?q=pdf+reader"));
            context.startActivity(playStoreIntent);
        } catch (ActivityNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse("https://play.google.com/store/search?q=pdf+reader"));
            context.startActivity(browserIntent);
        }
    }

    private static void showErrorOnMainThread(Activity activity, String message) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
    }

    private static String extractGoogleDriveFileId(String url) {
        if (url.contains("/d/")) {
            String[] parts = url.split("/d/");
            if (parts.length > 1) {
                return parts[1].split("/")[0];
            }
        } else if (url.contains("id=")) {
            String[] parts = url.split("id=");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }

    public static void openGoogleMaps(Context context, double latitude, double longitude, String label) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + label);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            mapIntent.setPackage(null);
            context.startActivity(mapIntent);
        }
    }

    public static void handleWebAction(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "لا يمكن فتح الرابط", Toast.LENGTH_LONG).show();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
