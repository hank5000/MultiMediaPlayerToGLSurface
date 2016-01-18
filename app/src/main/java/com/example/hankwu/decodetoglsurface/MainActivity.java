package com.example.hankwu.decodetoglsurface;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.example.hankwu.decodetoglsurface.encode.Encoder;
import com.example.hankwu.decodetoglsurface.encode.Recorder;
import com.example.hankwu.decodetoglsurface.encode.SnapShot;

public class MainActivity extends Activity {
    VideoSurfaceView mVideoSurfaceView = null;
    static MainActivity act = new MainActivity();
    Handler handler = new Handler();
    SurfaceView surf = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // create a surfaceView to be a GL render target and snapshot it.
        surf = (SurfaceView)findViewById(R.id.sv1);
        SnapShot.snapShot.setSurface(surf.getHolder().getSurface());

        // Add GLSurfaceView to Activity
        LinearLayout ll = (LinearLayout) findViewById(R.id.hank);
        mVideoSurfaceView = new VideoSurfaceView(this);
        //Encoder.getEncoder().setmWriterHandler(handler);
        //Recorder.getRecorder().setPreviewSurface(mVideoSurfaceView.getHolder().getSurface());

        ll.addView(mVideoSurfaceView);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
