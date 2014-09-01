package net.podkowik.spycam.listener;

import android.util.Log;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by christoph.podkowik on 11/08/14.
 */
public class FtpListener implements FTPDataTransferListener {

    private static final String TAG = "FTPDataTransferListener";

    public void started() {
        Log.d(TAG, " Upload Started ...");
    }

    public void transferred(int length) {
        Log.d(TAG, " transferred ..." + length);
    }

    public void completed() {
        Log.d(TAG, " completed ...");
    }

    public void aborted() {
        Log.d(TAG, " aborted ...");
    }

    public void failed() {
        Log.d(TAG, " failed ...");
    }

}
