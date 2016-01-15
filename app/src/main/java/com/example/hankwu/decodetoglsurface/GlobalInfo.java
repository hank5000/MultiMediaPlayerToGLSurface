package com.example.hankwu.decodetoglsurface;

/**
 * Created by HankWu_Office on 2016/1/15.
 */
public class GlobalInfo {
    final static String[] path = {  "/mnt/hank/720.mp4",
                                    "/mnt/hank/720.mp4",
                                    "/mnt/hank/720.mp4",
                                    "/mnt/hank/720.mp4"};
    final static String[] rtsp_path = { "rtsp://192.168.12.52:554/user=admin&password=&channel=1&stream=0.sdp?",
                                        "rtsp://192.168.12.54:554/user=admin&password=&channel=1&stream=0.sdp?",
                                        "rtsp://192.168.12.59:554/user=admin&password=&channel=1&stream=0.sdp?",
                                        "rtsp://192.168.12.60:554/user=admin&password=&channel=1&stream=0.sdp?"};

    final static boolean isRTSPMode = true;
    public static String getPath(int index) {
        if(isRTSPMode) {
            return rtsp_path[index];
        } else {
            return path[index];
        }
    }
    
    final static String RECORD_MODE = "MediaRecorderMode";
    final static String MEDIACODEC_MUXER_MODE  = "MediaCodecMuxerMode";
    final static String DISABLE_MODE = "DisableMode";
    final static String[] EncodeMode = {DISABLE_MODE, RECORD_MODE,  MEDIACODEC_MUXER_MODE};
    /*
        0 => disable Encode
        1 => using MediaRecorder (only for Android 5.0+)
        2 => using MediaCodec + MediaMuxer (only for Android 4.3+)
     */
    final static String encodeMode = EncodeMode[0];
    final static int encodeWidth = 1920;
    final static int encodeHeight= 1080;
    final static int bitRate = 10000;
    final static int numberOfDecode = 1;
    final static int numberOfLayoutRow = 2;
    final static int numberOfLayoutColumn = 2;



    public static int getNumberOfDecode() {
        return numberOfDecode;
    }

    public static int getNumberOfLayoutRow() {
        return numberOfLayoutRow;
    }

    public static int getNumberOfLayoutColumn() {
        return numberOfLayoutColumn;
    }

    public static int getEncodeWidth() {
        return encodeWidth;
    }

    public static int getEncodeHeight() {
        return encodeHeight;
    }

    public static String getEncodeMode() {
        return encodeMode;
    }

    public static int getBitrate() {
        return bitRate;
    }

    public static boolean isRecordMode() {
        return encodeMode.equals(RECORD_MODE);
    }

    public static boolean isMediaCodecMuxerMode() {
        return encodeMode.equals(MEDIACODEC_MUXER_MODE);
    }

    public static boolean isEncodeEnable() {
        return !encodeMode.equals(DISABLE_MODE);
    }


}
