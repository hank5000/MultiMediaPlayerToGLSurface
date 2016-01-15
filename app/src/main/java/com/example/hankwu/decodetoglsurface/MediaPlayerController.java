package com.example.hankwu.decodetoglsurface;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;

import com.via.rtsp.RTSPCodec;

import java.io.IOException;

/**
 * Created by HankWu on 16/1/9.
 */
public class MediaPlayerController {
    public static MediaPlayerController mediaPlayerControllerSingleton = new MediaPlayerController();
    private MediaPlayer[] mps = null;
    private RTSPCodec[] rps = null;
    private int number_of_play = 0;
    private boolean bSetSource = false;


    public void setSurfaceTextures(SurfaceTexture[] sts) throws Exception {

        if(!bSetSource) {
            throw new Exception("SetDataSource First!");
        }

        if(sts.length!=number_of_play) {
            throw new Exception("#SurfaceTexture!=#DataSource!");
        }

        for(int i=0;i<number_of_play;i++) {
            Surface surface = new Surface(sts[i]);
            if(mps[i]!=null) {
                mps[i].setSurface(surface);
            }
            if(rps[i]!=null) {
                rps[i].setSurface(surface);
            }
            //surface.release();
        }
    }

    public boolean isRTSPUrl(String s) {
        boolean rel = false;
        rel = s.toLowerCase().contains("rtsp");

        return rel;
    }

    public void setDataSources(String[] ss) throws IOException {
        number_of_play = ss.length;
        rps = new RTSPCodec[number_of_play];
        mps = new MediaPlayer[number_of_play];

        for(int i=0;i<number_of_play;i++) {
            if(isRTSPUrl(ss[i])) {
                rps[i] = new RTSPCodec();
                rps[i].setDataSource(ss[i]);
            } else {
                mps[i] = new MediaPlayer();
                mps[i].setDataSource(ss[i]);
            }
        }

        bSetSource = true;
    }

    public void prepare() throws IOException {
        for(int i=0;i<number_of_play;i++) {
            if(mps[i]!=null)
                mps[i].prepare();
        }
    }

    public void start() {
        for(int i=0;i<number_of_play;i++) {
            if(mps[i]!=null)
                mps[i].start();
            if(rps[i]!=null)
                rps[i].start();
        }
    }

    public void stop() {
        for(int i=0;i<number_of_play;i++) {
            if(mps[i]!=null)
                mps[i].stop();
            if(rps[i]!=null)
                rps[i].release();
        }
    }

    public void pause() {
        for(int i=0;i<number_of_play;i++) {
            if(mps[i]!=null)
                mps[i].pause();
        }
    }

    public void seekToZero() {
        for(int i=0;i<number_of_play;i++) {
            if(mps[i]!=null)
                mps[i].seekTo(0);
        }
    }

}
