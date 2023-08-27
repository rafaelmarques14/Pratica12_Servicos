package br.edu.ifpe.recife.tads.pratica12;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";
    public static final String URL_PATH = "urlpath";
    public static final String FILENAME = "filename";
    private static final String CHANNEL_ID = "download_channel";

    public DownloadService() {
        super("DownloadService");
    }
    @Override
    public void onHandleIntent(Intent intent) {
        Log.i(TAG, "DownloadService onHandleIntent");
        boolean success = false;
        String urlPath = intent.getStringExtra(URL_PATH);
        String fileName = intent.getStringExtra(FILENAME);
        String contentType = "";
        File output = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            contentType = connection.getContentType();
            stream = new BufferedInputStream(connection.getInputStream());
            fos = new FileOutputStream(output.getPath());
            Log.i(TAG, "DownloadService downdloading...");
            int next;
            byte [] BUF = new byte [512];
            while ((next = stream.read(BUF,0, 512)) != -1) {
                fos.write(BUF, 0, next);
            }
            success = true;
            Log.i(TAG, "DownloadService finished downloading.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        publishResults(fileName, output.getAbsolutePath(), contentType, success);
    }

    private void publishResults(String fileName, String filepath,
                                String contentType, boolean success) {
        Intent newIntent;
        String msg;
        if (success) {
            Uri uri = Uri.parse("file://" + filepath);
            newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setDataAndType(uri, contentType);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            msg = "Download completo: " + fileName;
        } else {
            newIntent = new Intent(this, MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            msg = "Download falhou: " + filepath;
        }
        showNotification(newIntent, msg);
    }
    private void showNotification(Intent intent, String msg) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Download Completo")
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Download Channel";
            String description = "Channel for download notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

}

