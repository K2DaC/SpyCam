package net.podkowik.spycam.services;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;

import net.podkowik.spycam.fragments.HomeFragments;
import net.podkowik.spycam.listener.FtpListener;

import it.sauronsoftware.ftp4j.FTPClient;

public class RecorderService extends Service {
    private static final String TAG = "RecorderService";
    private String mCurrentFileName = null;
    private SurfaceHolder mSurfaceHolder;
    private static Camera mServiceCamera;
    private MediaRecorder mMediaRecorder;

    private int mDelay = 1000;
    private int mPeriod = 300000;

    static final String FTP_HOST= "";
    static final String FTP_USER = "";
    static final String FTP_PASS  ="";


    /*
    * Timer will actually do three things
    * - stop recording
    * - upload the recorded file via ftp
    * - start new recording
    * */
    private Timer mTimer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                stopRecording();
                new Thread() {
                    @Override
                    public void run() {
                        uploadFile();
                    }
                }.start();
                startRecording(System.currentTimeMillis());
            }
        }, mDelay, mPeriod);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopRecording();
        mTimer.cancel();
        super.onDestroy();
    }

    private boolean startRecording(long time){
        try {
            mServiceCamera = Camera.open();
            mSurfaceHolder = HomeFragments.mSurfaceHolder;
            Camera.Parameters params = mServiceCamera.getParameters();
            mServiceCamera.setParameters(params);
            Camera.Parameters p = mServiceCamera.getParameters();
            final List<Size> listSize = p.getSupportedPreviewSizes();
            Size mPreviewSize = listSize.get(2);
            p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            p.setPreviewFormat(ImageFormat.NV21);
            mServiceCamera.setDisplayOrientation(90);
            mServiceCamera.setParameters(p);

            try {
                mServiceCamera.setPreviewDisplay(mSurfaceHolder);
                mServiceCamera.startPreview();
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            mServiceCamera.unlock();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mServiceCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mMediaRecorder.setOrientationHint(90);
            mCurrentFileName = "/sdcard/video" + time + ".mp4";
            mMediaRecorder.setOutputFile(mCurrentFileName);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mPreviewSize.width, mPreviewSize.height);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            return true;
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void stopRecording() {
        try {
            if (mServiceCamera != null) {
                mServiceCamera.reconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        if (mServiceCamera != null) {
            mServiceCamera.stopPreview();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }
        if (mServiceCamera != null) {
            mServiceCamera.release();
            mServiceCamera = null;
        }
    }


    private void uploadFile(){
        if (mCurrentFileName.equals(""))
            return;
        File f = new File(mCurrentFileName);
        FTPClient client = new FTPClient();
        try {
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.upload(f, new FtpListener());

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }



}