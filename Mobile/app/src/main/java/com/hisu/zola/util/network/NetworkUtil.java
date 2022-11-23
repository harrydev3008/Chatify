package com.hisu.zola.util.network;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.hisu.zola.R;
import com.hisu.zola.database.entity.Message;

import java.io.File;

public class NetworkUtil {
    public static boolean isConnectionAvailable(Context context) {
        boolean isConnected = true;

        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null)
            isConnected = false;

        return isConnected;
    }

    public static void downloadFile(Context context, Message message, String url, View view) {

        view.setClickable(false);
        String[] fileExtension = message.getText().split("\\.");
        String fileName = fileExtension[0] + "_" + message.getId() + "." + fileExtension[1];
        File checkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        if (checkFile.exists()) {
            view.setClickable(true);
            return;
        }

        Toast toast = Toast.makeText(context, context.getString(R.string.downloading_files), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(url));
        dmr.setDescription(message.getId());
        dmr.setTitle(fileName);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long enqueue = manager.enqueue(dmr);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (enqueue == reference) {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                    if (file.exists()) {
                        Toast complete = Toast.makeText(context, context.getString(R.string.downloaded), Toast.LENGTH_SHORT);
                        complete.setGravity(Gravity.CENTER, 0, 0);
                        complete.show();
                        view.setClickable(true);
                    }
                }
            }
        };

        context.registerReceiver(receiver, filter);
    }

    public static void openDocument(Context context, File file) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension.equalsIgnoreCase("") || mimetype == null) {
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_app_to_open_file)));
    }
}