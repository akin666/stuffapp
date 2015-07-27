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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ImageActivity extends Activity {
    public static final String ACTION = "Image_Action";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CROP = 2;
    private static final int REQUEST_GALLERY = 3;

    private Uri uri;
    private ImageView picture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        picture = (ImageView)findViewById(R.id.picture);

        Intent intent = getIntent();
        uri = intent.getData();

        setupPicture();
    }

    public void saveState() {
        Intent intent = getIntent();
        intent.setData(uri);
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
        if( uri == null ) {
            int width = 100;
            int height = 100;

            if( picture.getWidth() > 100 ) {
                width = picture.getWidth();
            }
            if( picture.getHeight() > 100 ) {
                height = picture.getHeight();
            }

            picture.setImageBitmap(Helpers.emptyBitMap(getString(R.string.no_image), width, height));
        }
        else {
            System.gc();
            picture.setImageURI(uri);
        }
        System.gc();
    }

    @Override
    public void finish() {
        save();
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

    private void crop() {
        try {
            Intent cropIntent = new Intent(this, ImageManipulationActivity.class);
            //Intent cropIntent = new Intent("com.android.camera.action.CROP");

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

            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        catch (ActivityNotFoundException e) {
            Common.toastLong(this, "This device doesn't support the crop action!: " + e.getMessage());
        }
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

    public void crop(View view) {
        crop();
    }

    public void gallery(View view) {
        gallery();
    }

    public void camera(View view) {
        camera();
    }

    public void dismiss(View view) {
        finish();
    }

    public boolean setUri( Uri newUri ) {
        uri = newUri;
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if( intent == null ) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setUri(intent.getData());
            saveState();
            setupPicture();

            return;
        }

        if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
            setUri(intent.getData());
            saveState();
            setupPicture();

            return;
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // Gallery app (samsung S4)
            // Photos app (samsung S4)
            // Dropbox app (samsung S4)
            setUri(intent.getData());
            saveState();
            setupPicture();

            return;
        }
    }
}
