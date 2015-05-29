package net.icegem.stuffapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import net.icegem.stuffapp.Constants;

/**
 * Created by mikael.korpela on 19.5.2015.
 */
public class Common
{
    public static void question( Context context, String title , String question , DialogInterface.OnClickListener yescl , DialogInterface.OnClickListener nocl )
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(question)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, yescl)
                .setNegativeButton(android.R.string.no, nocl).show();
    }

    public static void toast( Context context, String message )
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.w(Constants.AppName, message);
    }

    public static void toastLong( Context context, String message )
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.w(Constants.AppName, message);
    }

    public static void toast( Context context, Exception exception )
    {
        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
        Log.w(Constants.AppName, exception.toString());
    }

    public static void toastLong( Context context, Exception exception )
    {
        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
        Log.w(Constants.AppName,exception.toString());
    }

    public static void log( String message )
    {
        Log.w(Constants.AppName, message);
    }

    public static void log( Exception exception )
    {
        Log.w(Constants.AppName, exception.toString());
    }
}
