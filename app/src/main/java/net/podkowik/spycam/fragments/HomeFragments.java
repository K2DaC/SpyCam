package net.podkowik.spycam.fragments;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.podkowik.spycam.HomeActivity;
import net.podkowik.spycam.R;
import net.podkowik.spycam.services.RecorderService;

public class HomeFragments extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = "Recorder";
    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;

    public static HomeFragments newInstance() {
        HomeFragments fragment = new HomeFragments();
        return fragment;
    }
    public HomeFragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_fragments, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSurfaceView = (SurfaceView) getActivity().findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Intent intent = new Intent(getActivity(), RecorderService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startService(intent);
        Button btnStop = (Button) getActivity().findViewById(R.id.stopService);
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                getActivity().stopService(new Intent(getActivity(), RecorderService.class));
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

}
