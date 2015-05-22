package net.icegem.stuffapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import net.icegem.stuffapp.ui.Common;

/**
 * Created by mikael.korpela on 21.5.2015.
 */
public class Barcode {

    private static final String SCANNER_ACTION = "com.google.zxing.client.android.SCAN";
    private static final String SCANNER_NAME = "com.google.zxing.client.android";

    private static String lastResult = null;

    public static void postResult(String result)
    {
        lastResult = result;
    }

    public static String getLastResult()
    {
        String result = lastResult;
        lastResult = null;
        return result;
    }
    
    public static boolean onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if( intent == null )
        {
            return false;
        }

        String action = intent.getAction();
        if( action == null )
        {
            return false;
        }

        // Barcode scanner action.
        if(action.equals(SCANNER_ACTION)) {
            if (requestCode == 0) {
                if (resultCode == Activity.RESULT_OK) {
                    String contents = intent.getStringExtra("SCAN_RESULT");
                    if (contents != null) {
                        postResult(contents);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static void read( Activity activity )
    {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(SCANNER_ACTION);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            activity.startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
            // Failed, Lets try to show "scanner dowload page"
            Common.longLog(activity, activity.getString(R.string.barcodereader_not_found));

            Uri marketUri = Uri.parse("market://details?id=" + SCANNER_NAME);
            try {
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                activity.startActivity(marketIntent);
            }
            catch (Exception e)
            {
                Common.longLog(activity, "Failed to suggest app.: " + e.getMessage());
            }
        }
    }
}
