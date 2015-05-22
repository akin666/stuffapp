package net.icegem.stuffapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

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

    public static void log( Context context, String message )
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        System.out.println(message);
    }

    public static void longLog( Context context, String message )
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        System.out.println(message);
    }

    public static void log( Context context, Exception exception )
    {
        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
        System.out.println(exception.toString());
    }

    public static void longLog( Context context, Exception exception )
    {
        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
        System.out.println(exception.toString());
    }

    public static void log( Exception exception )
    {
        System.out.println(exception.toString());
    }
}
