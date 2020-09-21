package com.nachiket.ecsblocker.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nachiket.ecsblocker.R;
import com.nachiket.ecsblocker.activities.MainActivity;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

/**
 * Status bar & ringtone/vibration notification
 */
public class Notifications {
    // Notification on call blocked
    public static void onCallBlocked(Context context, String address) {
        if (!com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context, com.nachiket.ecsblocker.utils.Settings.BLOCKED_CALL_STATUS_NOTIFICATION)) {
            return;
        }
        String message = context.getString(R.string.call_is_blocked);
        int icon = R.drawable.ic_block;
        String action = MainActivity.ACTION_JOURNAL;
        Uri ringtone = getRingtoneUri(context,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_CALL_SOUND_NOTIFICATION,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_CALL_RINGTONE);
        boolean vibration = com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_CALL_VIBRATION_NOTIFICATION);
        notify(context, address, message, message, icon, action, ringtone, vibration);
    }

    // Notification on SMS blocked
    public static void onSmsBlocked(Context context, String address) {
        if (!com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context, com.nachiket.ecsblocker.utils.Settings.BLOCKED_SMS_STATUS_NOTIFICATION)) {
            return;
        }
        String message = context.getString(R.string.message_is_blocked);
        int icon = R.drawable.ic_block;
        String action = MainActivity.ACTION_JOURNAL;
        Uri ringtone = getRingtoneUri(context,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_SMS_SOUND_NOTIFICATION,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_SMS_RINGTONE);
        boolean vibration = com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context,
                com.nachiket.ecsblocker.utils.Settings.BLOCKED_SMS_VIBRATION_NOTIFICATION);
        notify(context, address, message, message, icon, action, ringtone, vibration);
    }

    // Notification on SMS received
    public static void onSmsReceived(Context context, String address, String smsBody) {
        String message = context.getString(R.string.message_is_received);
        int icon = R.drawable.ic_status_sms;
        String action = MainActivity.ACTION_SMS_CONVERSATIONS;
        Uri ringtone = getRingtoneUri(context,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_SOUND_NOTIFICATION,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_RINGTONE);
        boolean vibration = com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_VIBRATION_NOTIFICATION);
        notify(context, address, message, smsBody, icon, action, ringtone, vibration);
    }

    public static void onSmsDelivery(Context context, String address, String message) {
        if (!com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context, com.nachiket.ecsblocker.utils.Settings.DELIVERY_SMS_NOTIFICATION)) {
            return;
        }
        int icon = R.drawable.ic_status_sms;
        String action = MainActivity.ACTION_SMS_CONVERSATIONS;
        Uri ringtone = getRingtoneUri(context,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_SOUND_NOTIFICATION,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_RINGTONE);
        boolean vibration = com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context,
                com.nachiket.ecsblocker.utils.Settings.RECEIVED_SMS_VIBRATION_NOTIFICATION);
        notify(context, address, message, message, icon, action, ringtone, vibration);
    }

    private static void notify(Context context, String title, String message, String ticker,
                               @DrawableRes int icon, String action, Uri ringtone,
                               boolean vibration) {

        // turn off sound and vibration if phone is in silent mode
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                ringtone = null;
                vibration = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                ringtone = null;
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                break;
        }

        // pending intent for activating app on click in status bar
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        builder.setTicker(ticker);
        builder.setContentText(message);
        builder.setSmallIcon(icon);
        builder.setColor(getColor(context, R.attr.colorAccent));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_block));
        builder.setPriority(PRIORITY_MAX);
        builder.setAutoCancel(true);
        if (ringtone != null) {
            builder.setSound(ringtone);
        }
        if (vibration) {
            builder.setVibrate(new long[]{0, 300, 300, 300});
        }
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    // Returns notification ringtone uri (if settings do not allow it - returns null)
    private static Uri getRingtoneUri(Context context, String notificationProperty,
                                      String ringtoneProperty) {
        Uri ringtone = null;
        // if ringtone notification is allowed
        if (com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context, notificationProperty)) {
            // get ringtone uri
            String uriString = com.nachiket.ecsblocker.utils.Settings.getStringValue(context, ringtoneProperty);
            if (uriString != null) {
                ringtone = Uri.parse(uriString);
            } else {
                // if there isn't uri in setting - get default ringtone
                ringtone = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;
            }
        }
        return ringtone;
    }

    private static int getColor(Context context, @AttrRes int attrRes) {
        int styleRes = R.style.AppTheme_Dark;
        if (com.nachiket.ecsblocker.utils.Settings.getBooleanValue(context, com.nachiket.ecsblocker.utils.Settings.UI_THEME_DARK)) {
            styleRes = R.style.AppTheme_Light;
        }
        int colorRes = com.nachiket.ecsblocker.utils.Utils.getResourceId(context, attrRes, styleRes);
        return ContextCompat.getColor(context, colorRes);
    }
}
