package com.example.app2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String membershipName = intent.getStringExtra("membership_name");
        int membershipId = intent.getIntExtra("membership_id", -1);

        if (membershipName != null && membershipId != -1) {
            NotificationHelper.sendNotification(
                    context,
                    "Payment Reminder",
                    "Just a friendly reminder that your payment for \"" + membershipName + "\" is due tomorrow.",
                    membershipId
            );
        }
        else {
            Log.e("NotificationReceiver", "Invalid data received.");
        }
    }
}
