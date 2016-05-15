package com.uplb.lorenzo.collegeplanner.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationSetter extends BroadcastReceiver {
    public NotificationSetter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificationService.class);
        service.setAction(NotificationService.RESCHEDULE);
        context.startService(service);
    }
}
