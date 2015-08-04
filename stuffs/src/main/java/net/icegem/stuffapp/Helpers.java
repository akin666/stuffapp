package net.icegem.stuffapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static float distance(PointF point1, PointF point2) {
        return PointF.length(Math.abs(point1.x - point2.x) , Math.abs(point1.y - point2.y));
    }

    public static PointF middle( PointF p , PointF p2 ) {
        return new PointF(
                p.x + (PointF.length( p.x , p2.x ) / 2.0f) ,
                p.y + (PointF.length( p.y , p2.y ) / 2.0f )
        );
    }

    public static float clampAngle( float angle ) {
        while( angle < 0.0f ) {
            angle += 360.0f;
        }
        while( angle >= 360.0f ) {
            angle -= 360.0f;
        }

        // now  ]0,360]
        return angle;
    }

    public static float shortestDistanceAngle( float angle1 , float angle2 ) {
        angle1 = clampAngle( angle1 );
        angle2 = clampAngle( angle2 );

        if( angle1 > angle2 ) {
            float distance = angle1 - angle2;

            return (distance > 180.0f) ? (360.0f - distance) : (-distance);
        }

        float distance = angle2 - angle1;
        return (distance > 180.0f) ? (-(360.0f - distance)) : (distance);
    }

    public static float distanceBetweenAngles( float a , float b ) {
        a = clampAngle(a);
        b = clampAngle(b);

        // now both are ]0,360]
        float big = b;
        float small = a;
        if( a > b ) {
            big = a;
            small = b;
        }

        float distance = big - small;
        return (distance > 180.0f) ? (360.0f - distance) : distance;
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

    public static Bitmap emptyBitMap( int w , int h ) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap

        return bitmap;
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

    public static Point dimensionsBitmap(Context context , Uri uri) {
        int w = 0;
        int h = 0;

        try {
            InputStream input = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither=true;//optional
            onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();

            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                return null;

            w = onlyBoundsOptions.outWidth;
            h = onlyBoundsOptions.outHeight;
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            return null;
        }

        return new Point(w,h);
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
        return  Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    public static String createGUID() {
        UUID uid = UUID.randomUUID();
        return uid.toString();
    }

    public static Uri stringToUri( String string ) {
        if( string == null || string.isEmpty() ) {
            return null;
        }

        Uri uri = Uri.parse(string);

        return uri;
    }
}
