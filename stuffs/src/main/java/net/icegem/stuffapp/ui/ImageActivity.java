package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.icegem.stuffapp.Helpers;
import net.icegem.stuffapp.R;

public class ImageActivity extends Activity {
    public static final String ACTION = "Image_Action";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CROP = 2;
    private Uri uri;

    ImageView picture = null;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        picture = (ImageView)findViewById(R.id.picture);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if( extras != null ) {
            bitmap = (Bitmap)extras.get("data");
            uri = (Uri)extras.get("uri");
        }

        setupPicture();
    }

    public void saveState() {
        Intent intent = getIntent();
        if( bitmap != null ) {
            intent.putExtra("data", bitmap);
        }
        if( uri != null ) {
            intent.putExtra("uri", uri);
        }
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

    private void setupPicture() {
        if( bitmap == null ) {
            int width = 100;
            int height = 100;

            if( picture.getWidth() > 100 ) {
                width = picture.getWidth();
            }
            if( picture.getHeight() > 100 ) {
                height = picture.getHeight();
            }

            Common.log("The area is of size: " + picture.getWidth() + "x" + picture.getHeight() );

            bitmap = Helpers.emptyBitMap( getString( R.string.no_image ) , width , height );
        }
        else {
            Common.log("The image is of size: " + bitmap.getWidth() + "x" + bitmap.getHeight() );
        }

        picture.setImageBitmap(bitmap);
    }

    @Override
    public void finish() {
        save();
        super.finish();
    }

    public void save() {
        Intent intent = new Intent();

        intent.putExtra( "data" , bitmap );
        intent.putExtra( "uri" , uri );

        intent.setAction(ACTION);

        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if(extras.containsKey("width") ){
                cropIntent.putExtra("outputX", extras.getInt("width"));
            }
            if(extras.containsKey("height") ){
                cropIntent.putExtra("outputY", extras.getInt("height"));
            }
            if(extras.containsKey("aspectX") ){
                cropIntent.putExtra("aspectX", extras.getFloat("aspectX"));
            }
            if(extras.containsKey("aspectY") ){
                cropIntent.putExtra("aspectY", extras.getFloat("aspectY"));
            }

            // indicate image type and Uri
            cropIntent.setDataAndType(uri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            Common.toastLong(this, "This device doesn't support the crop action!: " + e.getMessage());
        }
    }

    public void crop(View view) {
        performCrop();
    }

    public void camera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void dismiss(View view) {
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if( intent == null ) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            bitmap = (Bitmap)extras.get("data");

            uri = intent.getData();
            saveState();

            setupPicture();

            return;
        }

        if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
            // get the returned data
            Bundle extras = intent.getExtras();
            // get the cropped bitmap
            bitmap = extras.getParcelable("data");
            saveState();

            setupPicture();
        }
    }
}
