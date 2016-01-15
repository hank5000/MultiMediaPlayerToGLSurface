package com.example.hankwu.decodetoglsurface.encode;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.example.hankwu.decodetoglsurface.GlobalInfo;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by HankWu_Office on 2016/1/15.
 */


public class Recorder {
    static public Recorder recorder = new Recorder();
    public MediaRecorder mRecorder = null;
    String record_path = "/mnt/hank/";
    boolean bInit = false;
    boolean bStart = false;
    Surface previewSurface = null;
    boolean bSetPreview = false;


    public boolean isSetPreview() {
        return bSetPreview;
    }
    public static Recorder getRecorder() {
        return recorder;
    }

    public void Create() throws IOException {
        mRecorder = new MediaRecorder();
        mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.format("%02d", c.get(Calendar.MONTH));
        String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
        String hr = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", c.get(Calendar.MINUTE));
        String save_path = record_path+"VIA_"+year+month+day+"_"+hr+minute+".mp4";

        mRecorder.setOutputFile(save_path);
        mRecorder.setVideoEncodingBitRate(GlobalInfo.getBitrate());
        mRecorder.setVideoFrameRate(30);
        mRecorder.setVideoSize(GlobalInfo.getEncodeWidth(), GlobalInfo.getEncodeHeight());
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mRecorder.setPreviewDisplay(previewSurface);
        mRecorder.prepare();


        bInit = true;
    }

    public void setPreviewSurface(Surface v) {
        previewSurface = v;
        bSetPreview = true;
    }

    public Surface getSurface() {
        return mRecorder.getSurface();
    }

    public boolean isInitialized()
    {
        return bInit;
    }

    public boolean isStarted() {
        return bStart;
    }

    public void start() {
        if(!bStart && bInit) {
            mRecorder.start();
            bStart = true;
        }
    }

    public void stop() {
        if(bInit && bStart)
            mRecorder.stop();
    }




}
