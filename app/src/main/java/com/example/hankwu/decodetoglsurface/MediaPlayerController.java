package com.example.hankwu.decodetoglsurface;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by HankWu on 16/1/9.
 */
public class MediaPlayerController {
    public static MediaPlayerController mediaPlayerControllerSingleton = new MediaPlayerController();
    private MediaPlayer[] mps = null;
    private int number_of_play = 0;

    public void setSurfaceTextures(SurfaceTexture[] sts) {
        number_of_play = sts.length;
        mps = new MediaPlayer[number_of_play];

        for(int i=0;i<number_of_play;i++) {
            mps[i] = new MediaPlayer();

            Surface surface = new Surface(sts[i]);
            mps[i].setSurface(surface);
            surface.release();
        }
    }

    public void setDataSources(String[] ss) throws IOException {
        for(int i=0;i<number_of_play;i++) {
            mps[i].setDataSource(ss[i]);
        }
    }

    public void prepare() throws IOException {
        for(int i=0;i<number_of_play;i++) {
            mps[i].prepare();
        }
    }

    public void start() {
        for(int i=0;i<number_of_play;i++) {
            mps[i].start();
        }
    }

    public void stop() {
        for(int i=0;i<number_of_play;i++) {
            mps[i].stop();
        }
    }

    public void pause() {
        for(int i=0;i<number_of_play;i++) {
            mps[i].pause();
        }
    }

    public void seekToZero() {
        for(int i=0;i<number_of_play;i++) {
            mps[i].seekTo(0);
        }
    }

}
