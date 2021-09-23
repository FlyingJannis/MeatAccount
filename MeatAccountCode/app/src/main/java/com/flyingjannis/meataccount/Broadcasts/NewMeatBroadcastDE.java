package com.flyingjannis.meataccount.Broadcasts;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.flyingjannis.meataccount.MainActivity;
import com.flyingjannis.meataccount.R;

public class NewMeatBroadcastDE extends BroadcastReceiver {

    public static String title = "Neues Fleisch!";
    public static String text = "Du hast neues Fleisch auf deinem Fleischkonto gutgeschrieben bekommen. Los sieh's dir an!";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, in, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "flyingJannis_newMeat")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }
}
