package com.example.hankwu.decodetoglsurface.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.example.hankwu.decodetoglsurface.GlobalInfo;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by charleswu-bc on 2015/10/29.
 */
public class Encoder {
    private static Encoder encoder = new Encoder();
    private MediaCodec mcencoder;
    public Surface mEncodeSuface;
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    private boolean isEnd = false;
    private MediaMuxer mediaMuxer =null;
    private boolean muxerStarted = false;
    private int videoTrackIdx ;
    private int framerate = 30;
    private int framelimit;
    public int w = GlobalInfo.getEncodeWidth();
    public int h = GlobalInfo.getEncodeHeight();

    private Handler mWriterHandler = null;
    private String TAG = "Encoder";

    public void Create(){
        try {
            mcencoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        }catch (Exception e){
            e.printStackTrace();
        }
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, w, h);//1152*1080
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, GlobalInfo.getBitrate());
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mcencoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mEncodeSuface = mcencoder.createInputSurface();

//        Looper.prepare();
        isEnd = false;
    }

    static public Encoder getEncoder(){
        return encoder;
    }

    boolean bStartEncoder = false;
    String record_path = "/mnt/hank/";
    public void run_encode(){
        if(isEnd)return;

        if(!bStartEncoder) {
            mcencoder.start();
            bStartEncoder = true;
        }
        if(mediaMuxer == null){
            try {
                Calendar c = Calendar.getInstance();
                String year = String.valueOf(c.get(Calendar.YEAR));
                String month = String.format("%02d", c.get(Calendar.MONTH));
                String day = String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
                String hr = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
                String minute = String.format("%02d", c.get(Calendar.MINUTE));
                mediaMuxer = new MediaMuxer(record_path+"VIA_"+year+month+day+"_"+hr+minute+".mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                framelimit = framerate*30000; //10 min
            }catch (Exception e){

            }
        }
        int outputBufferIdx = mcencoder.dequeueOutputBuffer(bufferInfo,0);

        if (framelimit >=0 && outputBufferIdx >= 0) {
            ByteBuffer outBuffer = mcencoder.getOutputBuffers()[outputBufferIdx];
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                bufferInfo.size = 0;
            }

            if (bufferInfo.size > 0) {
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                if (!muxerStarted) {
                    videoTrackIdx = mediaMuxer.addTrack(mcencoder.getOutputFormat());
                    mediaMuxer.start();
                    muxerStarted = true;
                }
                if(muxerStarted) {
                    if (framelimit-- == 0) {
                        stop_write();
                    } else {
                        //TODO: refine it
                        mediaMuxer.writeSampleData(videoTrackIdx,outBuffer,bufferInfo);
                    }
                }
            }

        }
    }

    public void stop_write(){
        if(mediaMuxer != null) {
            if(muxerStarted) {
                mediaMuxer.stop();
            }
            mediaMuxer.release();
            mediaMuxer = null;
            muxerStarted = false;
        }
    }

    public void Destroy(){
        isEnd = true;
        stop_write();
        if(mcencoder != null && bStartEncoder){
            mcencoder.stop();
            mcencoder.release();
            mcencoder = null;
        }

    }

    public void setmWriterHandler(Handler h) {
        mWriterHandler = h;
    }
}
