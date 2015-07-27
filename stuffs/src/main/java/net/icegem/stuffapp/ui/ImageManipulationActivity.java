package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
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

public class ImageManipulationActivity extends Activity implements GestureDetection.OnGestureListener {

    private Uri uri;
    private ImageView picture = null;

    private GestureDetection gestures;

    private float scale = 1.0f;
    private float rotation = 0.0f;

    private Point dimensions = new Point(0,0);
    private PointF offset = new PointF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_manipulation);

        Intent intent = getIntent();
        uri = intent.getData();

        picture = (ImageView)findViewById(R.id.picture);
        /// TODO! For some reason, setImageURI scales the image, investigate why.
        //picture.setImageURI(uri);

        Bitmap bitmap = Helpers.loadBitmap( this , uri );
        picture.setImageBitmap( bitmap );

        dimensions = new Point( bitmap.getWidth() , bitmap.getHeight() );

        picture.setScaleType(ImageView.ScaleType.MATRIX);
        picture.setAdjustViewBounds(false);

        calculateMatrix(0.0f, 1.0f);

        gestures = new GestureDetection( this , picture );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void dismiss(View view) {
        finish();
    }

    public void calculateMatrix( float aRotation , float aScale ) {

        int w = picture.getWidth();
        int h = picture.getHeight();

        Matrix matrix = new Matrix();

        float angle = aRotation + this.rotation;
        float scale = aScale * this.scale;

        if( scale < (-Float.MAX_VALUE) ) {
            scale = -Float.MAX_VALUE;
        }

        matrix.postTranslate(-dimensions.x / 2.0f, -dimensions.y / 2.0f);
        matrix.postRotate(angle);
        matrix.postScale(scale, scale);

        matrix.postTranslate(w / 2.0f, h / 2.0f);

        picture.setImageMatrix( matrix );
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
    public void onGesture(GestureDetection detection) {
        calculateMatrix(detection.getAngle(), detection.getScale());
    }

    @Override
    public void endGesture(GestureDetection detection) {
        rotation += detection.getAngle();
        scale *= detection.getScale();

        if( scale < (-Float.MAX_VALUE) ) {
            scale = -Float.MAX_VALUE;
        }
    }
}