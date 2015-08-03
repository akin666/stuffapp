package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import net.icegem.stuffapp.Constants;
import net.icegem.stuffapp.GestureDetection;
import net.icegem.stuffapp.Helpers;
import net.icegem.stuffapp.R;

public class ImageManipulationActivity extends Activity
        implements
        GestureDetection.OnGestureListener ,
        GestureDetection.Pan.Listener,
        GestureDetection.Pinch.Listener
{
    private Uri uri;
    private ImageView picture = null;
    private ImageView hud = null;

    private Bitmap bitmap = null;

    private GestureDetection gestures;

    private float scale = 1.0f;
    private float scaleDelta = 1.0f;

    private float rotation = 0.0f;
    private float rotationDelta = 0.0f;

    private Point dimensions = new Point(0,0);
    private PointF offset = new PointF(0,0);
    private PointF offsetDelta = new PointF(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_manipulation);

        Intent intent = getIntent();
        uri = intent.getData();

        picture = (ImageView)findViewById(R.id.picture);
        hud = (ImageView)findViewById(R.id.hud);
        /// TODO! For some reason, setImageURI scales the image, investigate why.
        /// (something todo with screen DPI, jeez.. why at this abstraction level, stupid..)
        /// picture.setImageURI(uri);

        Bitmap bitmap = Helpers.loadBitmap( this , uri );
        picture.setImageBitmap( bitmap );

        dimensions = new Point( bitmap.getWidth() , bitmap.getHeight() );

        picture.setScaleType(ImageView.ScaleType.MATRIX);
        picture.setAdjustViewBounds(false);

        gestures = new GestureDetection( this , picture );

        gestures.pan().set( this );
        gestures.pinch().set( this );

        bitmap = Helpers.emptyBitMap( dimensions.x , dimensions.y );

        hud.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if( hasFocus ) {
            calculateMatrix();
        }
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

    public void save(View view) {
        finish();
    }

    public void reset(View view) {
        scale = 1.0f;
        scaleDelta = 1.0f;

        rotation = 0.0f;
        rotationDelta = 0.0f;

        offset.set(0,0);
        offsetDelta.set(0,0);

        calculateMatrix();
    }

    public void dismiss(View view) {
        finish();
    }

    public void calculateMatrix() {

        int w = picture.getWidth();
        int h = picture.getHeight();

        Matrix matrix = new Matrix();

        float tscale = scaleDelta * scale;
        matrix.postTranslate(-dimensions.x / 2.0f, -dimensions.y / 2.0f);
        matrix.postRotate(rotationDelta + rotation);
        matrix.postScale(tscale, tscale);

        matrix.postTranslate(w / 2.0f, h / 2.0f);
        matrix.postTranslate(offset.x + offsetDelta.x, offset.y + offsetDelta.y);

        picture.setImageMatrix(matrix);

        //hud.setImageBitmap(bitmap);
/*
        Canvas canvas = new Canvas(bitmap);

        Paint p = new Paint();
        p.setARGB(0x77 , 0x77 ,0x77 ,0x77);

        canvas.drawCircle(offset.x + ax, offset.y + ay, 10, p);
        canvas.save();
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //super.onTouchEvent(event);
        return gestures.onTouchEvent(event);
    }

    @Override
    public void beginGesture(GestureDetection detection) {
    }

    @Override
    public void beginMove(GestureDetection detection) {
    }

    @Override
    public void onGesture(GestureDetection detection) {
        /*
        PointF delta = detection.getMoveDelta();
        calculateMatrix(detection.getAngle(), detection.getScale() , delta.x , delta.y );
        */
    }

    @Override
    public void endGesture(GestureDetection detection) {
        /*
        rotation += detection.getAngle();
        scale *= detection.getScale();

        if( scale < (-Float.MAX_VALUE) ) {
            scale = -Float.MAX_VALUE;
        }
        calculateMatrix(0.0f, 1.0f, 0.0f , 0.0f);
        */
    }

    @Override
    public void endMove(GestureDetection detection) {
        /*
        PointF delta = detection.getMoveDelta();
        offset.x += delta.x;
        offset.y += delta.y;
        calculateMatrix(0.0f, 1.0f, 0.0f , 0.0f);
        */
    }

    @Override
    public void onCancel(GestureDetection.Pan pan) {

    }

    @Override
    public void onBegin(GestureDetection.Pan pan) {

    }

    @Override
    public void onMove(GestureDetection.Pan pan) {
        PointF delta = pan.getDelta();
        offsetDelta.x = delta.x;
        offsetDelta.y = delta.y;

        calculateMatrix();
    }

    @Override
    public void onEnd(GestureDetection.Pan pan) {
        PointF delta = pan.getDelta();
        offset.x += delta.x;
        offset.y += delta.y;

        offsetDelta.set(0,0);

        calculateMatrix();
    }

    @Override
    public void onCancel(GestureDetection.Pinch pinch) {
    }

    @Override
    public void onBegin(GestureDetection.Pinch pinch) {
    }

    @Override
    public void onMove(GestureDetection.Pinch pinch) {
        scaleDelta = pinch.getDelta();

        calculateMatrix();
    }

    @Override
    public void onEnd(GestureDetection.Pinch pinch) {
        scale *= pinch.getDelta();
        scaleDelta = 1.0f;

        calculateMatrix();
    }
}