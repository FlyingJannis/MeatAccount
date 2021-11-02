package com.flyingjannis.meataccountPRO.broadcasts;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.flyingjannis.meataccountPRO.R;
import com.flyingjannis.meataccountPRO.activities.SettingsActivity;

public class ReminderBroadcastEN extends BroadcastReceiver {

    public static String title = "Hey, you' ve been vegetarian for the last few days?";
    public static String text = "Nice one! Look at the impact you\'ve had on the environment by giving it up!";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, SettingsActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, in, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "flyingJannis_reminder")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(300, builder.build());
    }
}
