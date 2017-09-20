package com.clem.ipoca.core.asynctask;

import android.content.Context;
import android.util.Log;

import com.clem.ipoca.core.BuildConfig;
import com.clem.ipoca.core.storage.DBWriter;
import com.clem.ipoca.core.util.flattr.FlattrUtils;

import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.model.Flattr;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Fetch list of flattred things and flattr status in database in a background thread.
 */

public class FlattrStatusFetcher extends Thread {
    protected static final String TAG = "FlattrStatusFetcher";
    protected Context context;

    public FlattrStatusFetcher(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void run() {
        if (BuildConfig.DEBUG) Log.d(TAG, "Starting background work: Retrieving Flattr status");

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        try {
            List<Flattr> flattredThings = FlattrUtils.retrieveFlattredThings();
            DBWriter.setFlattredStatus(flattredThings).get();
        } catch (FlattrException e) {
            e.printStackTrace();
            Log.d(TAG, "flattrQueue exception retrieving list with flattred items " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) Log.d(TAG, "Finished background work: Retrieved Flattr status");
    }
}
