package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.icegem.stuffapp.Constants;
import net.icegem.stuffapp.GestureDetection;
import net.icegem.stuffapp.Helpers;
import net.icegem.stuffapp.R;

public class ImageManipulationActivity extends Activity
{
    public static final String ACTION = "Image_Manipulation";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CROP = 2;
    private static final int REQUEST_GALLERY = 3;

    private Uri uri;
    private HudView hud = null;
    boolean nosave = false;

    private View menu = null;
    private View requestMenu = null;
    private ToggleButton toggle = null;
    private TextView bgText = null;

    private GestureDetection gestures;

    private class HudView extends View
            implements
            GestureDetection.Pan.Listener,
            GestureDetection.Pinch.Listener,
            GestureDetection.Rotate.Listener {

        private boolean frameLockedState = true;

        private Uri uri = null;
        private Bitmap bitmap = null;

        public RectF crop = new RectF(0,0,0,0);
        public RectF dimensions = new RectF(0,0,0,0);

        private float zoom = 1.0f;
        private float scale = 1.0f;
        private float pinchDelta = 1.0f;

        private float rotation = 0.0f;
        private float rotationDelta = 0.0f;

        private PointF offset = new PointF(0,0);
        private PointF offsetDelta = new PointF(0,0);

        public HudView(Context context) {
            super(context);
        }

        public void frameLock( boolean state ) {
            frameLockedState = state;
        }

        public boolean hasImage() {
            return uri != null && !uri.equals(Uri.EMPTY);
        }

        public void setImage( Uri uri ) {
            this.uri = uri;

            if( uri != null && !uri.equals(Uri.EMPTY) ) {
                bitmap = Helpers.loadBitmap(getContext(), uri);

                crop.set( 0 , 0 ,bitmap.getWidth() , bitmap.getHeight() );
                dimensions.set( 0 , 0 ,bitmap.getWidth() , bitmap.getHeight() );

                Log.w(Constants.AppName,"W: " + crop.right + " H: " + crop.bottom );
            } else {
                bitmap = null;
                crop.set( 0 , 0 , 0 , 0 );
                dimensions.set( 0 , 0 , 0 , 0 );
            }
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if( !hasImage() ) {
                return;
            }


            float w = getWidth();
            float h = getHeight();

            {
                Matrix matrix = new Matrix();

                float tzoom = zoom;
                if( !frameLockedState ) {
                    tzoom *= pinchDelta;
                }

                matrix.postTranslate(-hud.dimensions.right / 2.0f, -hud.dimensions.bottom / 2.0f);
                matrix.postRotate(rotationDelta + rotation);
                matrix.postScale(tzoom, tzoom);

                matrix.postTranslate(w / 2.0f, h / 2.0f);
                matrix.postTranslate(offset.x + offsetDelta.x, offset.y + offsetDelta.y);

                canvas.setMatrix(matrix);
            }

            Paint black = new Paint();
            black.setColor(Color.BLACK);
            black.setStrokeWidth(1.5f);
            black.setAntiAlias(true);
            black.setAlpha(128);

            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
            blue.setStrokeWidth(1.5f);
            blue.setAntiAlias(true);

            Paint green = new Paint();
            green.setColor(Color.GREEN);
            green.setStrokeWidth(1.5f);
            green.setAntiAlias(true);

            Paint red = new Paint();
            red.setColor(Color.RED);
            red.setStrokeWidth(1.5f);
            red.setAntiAlias(true);

            Paint yellow = new Paint();
            yellow.setColor(Color.YELLOW);
            yellow.setStrokeWidth(1.5f);
            yellow.setAntiAlias(true);
            // draw a circle

            if( bitmap != null ) {
                Matrix matrix = new Matrix();

                float tscale = scale;
                if( !frameLockedState ) {
                    tscale *= pinchDelta;
                }
                matrix.postScale(tscale, tscale);

                Paint imagePaint = new Paint();
                imagePaint.setColor(Color.BLACK);
                imagePaint.setAntiAlias(true);

                canvas.drawBitmap(bitmap, matrix, imagePaint);
            }

            canvas.drawRect(crop, black);

            canvas.drawLine(
                    0, 0,
                    crop.right, crop.bottom,
                    red);
            canvas.drawLine(
                    crop.right, 0,
                    0, crop.bottom,
                    yellow);

            canvas.drawCircle(0, 0, 10.0f, blue);
            canvas.drawCircle(crop.right, crop.bottom, 5.0f, green);
        }


        @Override
        public void onCancel(GestureDetection.Pan pan) {
            offsetDelta.set(0,0);

            hud.invalidate();
        }

        @Override
        public void onBegin(GestureDetection.Pan pan) {
            offsetDelta.set(0, 0);
        }

        @Override
        public void onMove(GestureDetection.Pan pan) {
            PointF delta = pan.getDelta();
            offsetDelta.x = delta.x;
            offsetDelta.y = delta.y;

            hud.invalidate();
        }

        @Override
        public void onEnd(GestureDetection.Pan pan) {
            PointF delta = pan.getDelta();
            offset.x += delta.x;
            offset.y += delta.y;

            offsetDelta.set(0,0);

            hud.invalidate();
        }

        @Override
        public void onCancel(GestureDetection.Pinch pinch) {
            pinchDelta = 1.0f;

            hud.invalidate();
        }

        @Override
        public void onBegin(GestureDetection.Pinch pinch) {
            pinchDelta = 1.0f;
        }

        @Override
        public void onMove(GestureDetection.Pinch pinch) {
            pinchDelta = pinch.getDelta();

            hud.invalidate();
        }

        @Override
        public void onEnd(GestureDetection.Pinch pinch) {
            if( frameLockedState ) {
                scale *= pinch.getDelta();
            }
            else {
                zoom *= pinch.getDelta();
            }

            pinchDelta = 1.0f;

            hud.invalidate();
        }

        @Override
        public void onCancel(GestureDetection.Rotate rotate) {
            rotationDelta = 0.0f;

            hud.invalidate();
        }

        @Override
        public void onBegin(GestureDetection.Rotate rotate) {
            rotationDelta = 0.0f;
        }

        @Override
        public void onMove(GestureDetection.Rotate rotate) {
            rotationDelta = rotate.getDelta();

            hud.invalidate();
        }

        @Override
        public void onEnd(GestureDetection.Rotate rotate) {
            rotation += rotate.getDelta();
            rotationDelta = 0.0f;

            hud.invalidate();
        }

        public void reset() {
            scale = 1.0f;
            zoom = 1.0f;
            pinchDelta = 1.0f;

            rotation = 0.0f;
            rotationDelta = 0.0f;

            offset.set(0, 0);
            offsetDelta.set(0, 0);

            hud.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nosave = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_manipulation);

        Intent intent = getIntent();
        uri = intent.getData();

        ViewGroup layout = (ViewGroup)findViewById(R.id.hud);
        hud = new HudView(this);
        layout.addView(hud);

        bgText = (TextView)findViewById(R.id.bgtext);
        menu = (View)findViewById(R.id.menu);
        requestMenu = (View)findViewById(R.id.requestMenu);
        toggle = (ToggleButton)findViewById(R.id.framelock);

        gestures = new GestureDetection( hud );
        gestures.pan().set( hud );
        gestures.pinch().set( hud );
        gestures.rotate().set( hud );

        /// TODO! For some reason, setImageURI scales the image, investigate why.
        /// (something todo with screen DPI, jeez.. why at this abstraction level, stupid..)
        /// picture.setImageURI(uri);

        loadUri();

        hideMenu(false);
        hideRequestMenu(true);
    }

    private void hideMenu( boolean hidden ) {
        if( hidden ) {
            menu.setVisibility(View.INVISIBLE);
        }
        else {
            menu.setVisibility(View.VISIBLE);
        }
    }

    private void hideRequestMenu( boolean hidden ) {
        if( hidden ) {
            requestMenu.setVisibility(View.INVISIBLE);
        }
        else {
            requestMenu.setVisibility(View.VISIBLE);
        }
    }

    private void loadUri() {
        hud.setImage( uri );

        if( hud.hasImage() ) {
            bgText.setVisibility(View.INVISIBLE);
            hud.invalidate();
        } else {
            bgText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if( hasFocus ) {
            hud.invalidate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if( !nosave ) {
            save();
        }
        super.finish();
    }

    public void save() {
        Intent intent = new Intent();

        intent.setData(uri);
        intent.setAction(ACTION);

        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    public void save(View view) {
        nosave = false;
        finish();
    }

    public void reset(View view) {
        hud.reset();
    }

    public void requestOk(View view) {
        hideMenu(false);
        hideRequestMenu(true);
    }

    public void requestCancel(View view) {
        hideMenu(false);
        hideRequestMenu(true);
    }

    public void dismiss(View view) {
        nosave = true;
        finish();
    }

    public void crop(View view) {
        hideMenu(true);
        hideRequestMenu(false);
    }

    public void frameLock(View view) {
        if( hud != null && toggle != null ) {
            hud.frameLock(toggle.isChecked());
        }
    }

    public void brightness(View view) {
        hideMenu(true);
        hideRequestMenu(false);
    }

    public void resize(View view) {
        hideMenu(true);
        hideRequestMenu(false);
    }

    public void gallery(View view) {
        gallery();
    }

    public void camera(View view) {
        camera();
    }

    private void camera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        catch (ActivityNotFoundException e) {
            Common.toastLong(this, "This device doesn't support the camera action!: " + e.getMessage());
        }
    }

    private void gallery() {
        Intent intent = new Intent();

        Bundle extras = getIntent().getExtras();

        // call android default gallery
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // crop image
        if(extras.containsKey("width") ){
            intent.putExtra("outputX", extras.getInt("width"));
        }
        if(extras.containsKey("height") ){
            intent.putExtra("outputY", extras.getInt("height"));
        }
        if(extras.containsKey("aspectX") ){
            intent.putExtra("aspectX", extras.getFloat("aspectX"));
        }
        if(extras.containsKey("aspectY") ){
            intent.putExtra("aspectY", extras.getFloat("aspectY"));
        }

        try {
            intent.putExtra("return-data", true);
            startActivityForResult( Intent.createChooser(intent,"Complete action using"), REQUEST_GALLERY );
        }
        catch (ActivityNotFoundException e) {
            Common.toastLong(this, "This device doesn't support the camera action!: " + e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //super.onTouchEvent(event);
        return gestures.onTouchEvent(event);
    }

    public void setUri( Uri newUri ) {
        uri = newUri;
        Intent intent = getIntent();
        intent.setData(uri);

        loadUri();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if( intent == null ) {
            return;
        }

        if ( (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_GALLERY) && resultCode == RESULT_OK) {
            // Gallery app (samsung S4)
            // Photos app (samsung S4)
            // Dropbox app (samsung S4)
            setUri(intent.getData());

            return;
        }
    }
}