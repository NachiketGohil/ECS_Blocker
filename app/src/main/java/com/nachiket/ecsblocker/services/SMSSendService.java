package com.nachiket.ecsblocker.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.nachiket.ecsblocker.utils.SMSSendHelper;

/**
 * SMS message sending service
 */
public class SMSSendService extends IntentService {
    private static final String MESSAGE = "MESSAGE";
    private static final String ADDRESSES = "ADDRESSES";

    public SMSSendService() {
        super(SMSSendService.class.getName());
    }

    // Starts the service
    public static void start(Context context, String message, String[] addresses) {
        Intent intent = new Intent(context, SMSSendService.class);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(ADDRESSES, addresses);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        // get message body and addresses
        String message = intent.getStringExtra(MESSAGE);
        String[] addresses = intent.getStringArrayExtra(ADDRESSES);
        if (message == null || addresses == null) {
            return;
        }

        // send SMS message
        SMSSendHelper smsSendHelper = new SMSSendHelper();
        for (String address : addresses) {
            smsSendHelper.sendSMS(this, address, message);
        }
    }
}
