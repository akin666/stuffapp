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

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.database.DBCollection;
import net.icegem.stuffapp.database.DBConnection;

import java.util.List;


public class CollectionActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DBConnection connection = null;
    private ListView list = null;
    private SearchView search = null;
    UICollection.Manager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        connection = new DBConnection(this);

        list = (ListView) findViewById(R.id.list);
        search = (SearchView) findViewById(R.id.search);

        refresh();
    }

    private void refresh() {
        manager = new UICollection.Manager(this, connection);

        manager.orderBy(Collection.orderByName);

        try {
            list.setAdapter(manager);
            final Activity parentActivity = this;
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Collection value = (Collection) parent.getItemAtPosition(position);
                    if (value == null) {
                        return;
                    }
                    try {
                        Intent intent = new Intent(parentActivity, CollectionViewActivity.class);
                        intent.putExtra(Collection.class.getName() , value);
                        startActivity(intent);
                    } catch (Exception e) {
                        Common.toast(parentActivity, e);
                    }
                }
            });

            list.setTextFilterEnabled(true);
            setupSearch();
        }
        catch(Exception e) {
            Common.toast(this, e);
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
            list.clearTextFilter();
        } else {
            list.setFilterText(newText);
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
        getMenuInflater().inflate(R.menu.menu_collection, menu);
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
                Intent intent = new Intent(this, CollectionEditActivity.class);
                startActivityForResult(intent , 0);
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
        if( Barcode.onActivityResult(requestCode, resultCode, intent) ) {
            return;
        }

        if( intent == null ) {
            return;
        }

        String action = intent.getAction();
        if( action == null ) {
            return;
        }

        // Collection action.
        if(action.equals(Collection.EDIT_ACTION)) {
            if (resultCode == RESULT_OK) {
                Collection collection = (Collection)intent.getParcelableExtra( Collection.class.getName() );

                if( collection != null ) {
                    manager.save(collection);
                }
            }
        }
    }
}
