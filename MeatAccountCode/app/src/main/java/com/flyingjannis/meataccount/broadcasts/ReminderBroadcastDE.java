package com.flyingjannis.meataccount.broadcasts;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.flyingjannis.meataccount.R;
import com.flyingjannis.meataccount.activities.SettingsActivity;

public class ReminderBroadcastDE extends BroadcastReceiver {

    public static String title = "Na, die letzten Tage vegetarisch gelebt?";
    public static String text = "Sehr gut! Sieh dir deinen Impact auf die Umwelt an, den du durch deinen Verzicht erzielt hast!";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, SettingsActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, in, PendingIntent.FLAG_IMMUTABLE);

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
