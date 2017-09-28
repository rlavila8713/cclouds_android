package com.xedrux.cclouds.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Isidro on 10/26/2015.
 */
public class DatabaseActions extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DatabaseActions(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
