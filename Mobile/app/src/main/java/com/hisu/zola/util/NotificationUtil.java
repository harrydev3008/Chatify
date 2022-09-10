package com.hisu.zola.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;

import java.time.LocalDateTime;

public class NotificationUtil {
    public static void pushNotification(Context context, String channelID, String msg) {

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.app_launcher_icon
        );

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(
                context, channelID)
                .setContentText(msg)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_noty)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(uri)
                .build();

        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(getNotificationID(), notification);
    }

    private static int getNotificationID() {
        return LocalDateTime.now().getNano();
    }
}