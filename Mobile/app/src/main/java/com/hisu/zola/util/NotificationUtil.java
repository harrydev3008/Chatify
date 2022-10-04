package com.hisu.zola.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public class NotificationUtil {

    public static void newMessageNotification(
            Context context, String avatar,String channelID, String title, String msg
    ) {

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.app_launcher_icon
        );

        try {
            bitmap = Glide.with(context).asBitmap().load(avatar).submit().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("Notification_err", e.getMessage());
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setContentText(msg)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(uri)
                .build();

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(getNotificationID(), notification);
    }

    public static void otpNotification(
            Context context, String channelID, String title, String msg
    ) {

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.app_launcher_icon
        );

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setContentText(msg)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setLargeIcon(bitmap)
                .setAutoCancel(false)
                .setSound(uri)
                .build();

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(getNotificationID(), notification);
    }

    private static int getNotificationID() {
        return LocalDateTime.now().getNano();
    }
}