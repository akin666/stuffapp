package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.database.DBCollection;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBItem;
import net.icegem.stuffapp.ui.AboutActivity;
import net.icegem.stuffapp.ui.Common;
import net.icegem.stuffapp.ui.ItemEditActivity;
import net.icegem.stuffapp.ui.SettingsActivity;

import java.util.List;
import java.util.Vector;


public class CollectionListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DBConnection connection = null;
    private ListView itemlist = null;
    private SearchView search = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection = new DBConnection(this);

        itemlist = (ListView) findViewById(R.id.ItemList);
        search = (SearchView) findViewById(R.id.search);
    }

    private void refresh() {
        if( connection == null || itemlist == null ) {
            return;
        }

        try {
            List<Collection> values = DBCollection.list(connection);
            ListAdapter adapter = new UICollection.RowAdapter(this, values);
            itemlist.setAdapter(adapter);
            final Activity parentActivity = this;
            itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Collection value = (Collection) parent.getItemAtPosition(position);
                    if (value == null) {
                        return;
                    }
                    try {
                        Intent intent = new Intent(parentActivity, CollectionActivity.class);
                        intent.putExtra(Collection.class.getName() , value);
                        startActivity(intent);
                    } catch (Exception e) {
                        Common.toast(parentActivity, e);
                    }
                }
            });

            itemlist.setTextFilterEnabled(true);
            setupSearch();
        }
        catch(Exception e) {
            Common.toast(this, e.toString());
        }
    }

    private void setupSearch() {
        search.setIconifiedByDefault(true);
        search.setOnQueryTextListener(this);
        search.setSubmitButtonEnabled(false);
        search.setQueryRefinementEnabled(false);
        search.setQueryHint(getString(R.string.search));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            itemlist.clearTextFilter();
        } else {
            itemlist.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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

    @Override
    protected void onResume() {
        super.onResume();

        refresh();

        String barcodeResult = Barcode.getLastResult();
        if( barcodeResult != null ) {
            barcode(barcodeResult);
        }
    }

    public void barcode(String result) {
        search.setQuery(result, false);
        search.clearFocus();
    }

    public void readBarcode(View view) {
        Common.toastLong(this, getString(R.string.start_barcodereader));
        Barcode.read(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Barcode.onActivityResult(requestCode, resultCode, intent);
    }
}
