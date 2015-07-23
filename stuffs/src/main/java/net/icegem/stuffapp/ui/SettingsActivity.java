package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
//import android.preference.PreferenceActivity; nah, the only "Preference/Settings" we have is the language.. buttons that I need here, are not part of preferenceactivity..
// to me this whole notion, and the amount of documentation about preferenceactivity sounds ridiculous.
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBItem;
import net.icegem.stuffapp.database.DBText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private DBConnection connection;
    private TextView dbText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbText = (TextView)findViewById(R.id.db_text);

        connection = new DBConnection(this);

        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
    }

    public void importDB(View view) {
        // Get all data to DB..
        String jsonString = dbText.getText().toString();

        if( jsonString == null || jsonString.isEmpty() ) {
            return;
        }

        try {
            JSONObject root =  new JSONObject(jsonString);
            JSONArray array = root.getJSONArray("items");

            for( int i = 0 ; i < array.length() ; ++i ) {
                JSONObject object = array.getJSONObject(i);

                Item item = new Item(object);
                DBItem.save( connection , item );
            }

        } catch (JSONException e) {
            Common.toastLong(this, e);
            return;
        }

        dbText.setText("");
        Common.toastLong(this, getString(R.string.db_import_success));
    }

    public void exportDB(View view) {
        // Get all data from DB..
        /*
        List<Item> items = datasource.getItems();

        JSONArray array = new JSONArray();
        for(final Item item : items) {
            JSONObject object = item.toJSON();
            if( object != null ) {
                array.put(object);
            }
        }

        JSONObject root = new JSONObject();
        try {
            root.put("items" , array);
        } catch (JSONException e) {
            Common.toastLong(this, e);
            return;
        }
        dbText.setText(root.toString());
        Common.toastLong(this, getString(R.string.db_export_success));
        */
    }

    public void clearDB(View view) {
        final Context context = this;
        final String successMsg = getString(R.string.db_clear_success);

        Common.question(
                this,
                getString(R.string.db_clear_topic),
                getString(R.string.db_clear_message),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connection.clear();
                        Common.toastLong(context, successMsg);
                    }
                },
                null);
    }

    public void editLanguages(View view) {
        Intent intent = new Intent(this, TypeEditActivity.class);
        this.startActivity(intent);
    }

    public void editTypes(View view) {
        Intent intent = new Intent(this, TypeEditActivity.class);
        this.startActivity(intent);
    }
}
