package com.mredrock.cyxbs.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.ui.activity.MainActivity;
import com.mredrock.cyxbs.ui.fragment.me.RemindFragment;

import static com.mredrock.cyxbs.ui.fragment.me.RemindFragment.SP_REMIND_EVERY_CLASS;
import static com.mredrock.cyxbs.ui.fragment.me.RemindFragment.SP_REMIND_EVERY_DAY;

/**
 * Created by simonla on 2016/10/10.
 * 下午11:06
 */

public class RemindReceiver extends BroadcastReceiver {

    public static final String TAG = "RemindReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 收到广播");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int mode1 = intent.getIntExtra(RemindFragment.INTENT_MODE, RemindFragment.INTENT_FLAG_BY_DAY);
        if (mode1 == RemindFragment.INTENT_FLAG_BY_CLASS && sp.getBoolean(SP_REMIND_EVERY_CLASS, false)) {
            byClass(context, intent);
            Log.d(TAG, "onReceive: receive by class! name: " + intent.
                    getStringExtra(RebootReceiver.EXTRA_COURSE_NAME) + " classroom: " +
                    intent.getStringExtra(RebootReceiver.EXTRA_COURSE_CLASSROOM)
            );
        }
        if (mode1 == RemindFragment.INTENT_FLAG_BY_DAY && sp.getBoolean(SP_REMIND_EVERY_DAY, false)) {
            byDay(context);
            Log.d(TAG, "onReceive: receive by day!");
        }
    }

    private void byDay(Context context) {
        Intent openMain = new Intent(context, MainActivity.class);
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle("小邮提醒您，这是明天的课表")
                    .setContentText("点击查看");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(openMain);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openMain,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openMain,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle("小邮提醒您，这是明天的课表")
                    .setContentText("点击查看");
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void byClass(Context context, Intent intent) {
        Intent openMain = new Intent(context, MainActivity.class);
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle(intent.getStringExtra(RebootReceiver.EXTRA_COURSE_NAME))
                    .setContentText(intent.getStringExtra(RebootReceiver.EXTRA_COURSE_CLASSROOM));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(openMain);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 61, openMain,
                    0);
            builder.setContentIntent(pendingIntent);
        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 62, openMain,
                    0);
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(intent.getStringExtra(RebootReceiver.EXTRA_COURSE_NAME))
                    .setContentText(intent.getStringExtra(RebootReceiver.EXTRA_COURSE_CLASSROOM));

        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}