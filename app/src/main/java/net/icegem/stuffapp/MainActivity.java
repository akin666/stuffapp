package net.icegem.stuffapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.icegem.stuffapp.ui.AboutActivity;
import net.icegem.stuffapp.ui.Common;
import net.icegem.stuffapp.ui.ItemEditActivity;
import net.icegem.stuffapp.ui.SettingsActivity;

import java.util.List;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings : {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_new : {
                Intent intent = new Intent(this, ItemEditActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_about : {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void readBarcode(View view) {
        Common.longLog(this, getString(R.string.start_barcodereader));

        Barcode.read( this );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        Barcode.onActivityResult(requestCode, resultCode, intent);
    }
}
