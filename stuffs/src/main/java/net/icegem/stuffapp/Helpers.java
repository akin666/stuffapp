package net.icegem.stuffapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by mikael.korpela on 22.7.2015.
 */
public class Helpers {

    // http://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
    public static Bitmap toBitMap( String encodedString ) {
        if( encodedString == null || encodedString.length() < 1 ) {
            return null;
        }
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String toString( Bitmap bitmap ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private static float getMaxFitMultiplier( String text , float maxWidth , Paint paint )
    {
        // Get the bounds of the text, using our testTextSize.
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        return maxWidth / bounds.width();
    }

    public static Bitmap emptyBitMap( String str ,  int w , int h ) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap

        Canvas canvas = new Canvas(bitmap);

        Paint white = new Paint();
        white.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
        white.setAntiAlias(true);

        Paint textColor = new Paint();
        textColor.setARGB(0xFF, 0x00, 0x00, 0x00);
        textColor.setAntiAlias(true);

        int min = w > h ? h : w;

        float textLeft = 10;
        float textRight = 10;
        float textWidth = min - (textLeft + textRight );

        float multiplier = getMaxFitMultiplier( str , textWidth , textColor );
        float textSize = textColor.getTextSize() * multiplier;

        textColor.setTextSize( textSize );

        canvas.drawCircle(w / 2 , h / 2 , min / 2.0f , white );
        canvas.drawText(str, textLeft, h / 2.0f + (textSize / 2), textColor);

        canvas.save();

        return bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if( (maxWidth <= 0) && (maxHeight <= 0) ) {
            return bitmap;
        }

        float scale = 1.0f;
        if( maxWidth <= 0 ) {
            scale = ((float)maxHeight) / height;
        }
        else if(maxHeight <= 0) {
            scale = ((float)maxWidth) / width;
        }
        else {
            float scaleW = ((float)maxWidth) / width;
            float scaleH = ((float)maxHeight) / height;

            scale = scaleH < scaleW ? scaleH : scaleW;
        }

        Matrix matrix = new Matrix();

        // Resize
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    public static Bitmap loadBitmap( Context context , Uri uri ) {
        ContentResolver cr = context.getContentResolver();
        try {
            return MediaStore.Images.Media.getBitmap( cr , uri );
        } catch (IOException e) {
        }
        return null;
    }

    public static Uri saveBitmap( Context context , Bitmap bitmap ) {
        String filename = null;
        File file = null;

        boolean passed = false;
        do {
            filename = createGUID() + ".png";
            file = new File(context.getExternalFilesDir(null), filename);
            try {
                passed = file.createNewFile();
            }
            catch (IOException e ) {
            }
        } while( !passed );

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            return null;
        }

        return Uri.fromFile(file);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        return  Bitmap.createBitmap(bitmap, x,y,width, height);
    }

    public static String createGUID() {
        UUID uid = UUID.randomUUID();
        return uid.toString();
    }
}
