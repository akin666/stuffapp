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
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBCollection;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBItem;
import net.icegem.stuffapp.database.DBText;
import net.icegem.stuffapp.database.DBType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
            List<Type> types = DBType.list(connection);
            List<Collection> collections = DBCollection.list(connection);
            List<Item> items = DBItem.list(connection);

            {
                JSONObject root =  new JSONObject(jsonString);

                JSONArray typesjs = root.getJSONArray(Type.PLURAL);
                JSONArray collectionsjs = root.getJSONArray(Collection.PLURAL);
                JSONArray itemsjs = root.getJSONArray(Item.PLURAL);

                for (int i = 0; i < typesjs.length(); ++i) {
                    JSONObject object = typesjs.getJSONObject(i);
                    types.add(new Type(object));
                }
                for (int i = 0; i < collectionsjs.length(); ++i) {
                    JSONObject object = collectionsjs.getJSONObject(i);
                    collections.add(new Collection(object));
                }
                for (int i = 0; i < itemsjs.length(); ++i) {
                    JSONObject object = itemsjs.getJSONObject(i);
                    items.add(new Item(object));
                }
            }

            // Now... Merge the data to database..
            // This is rather.. complex.. maybe refuctor one day..
            {
                // Types
                HashMap<Integer , Type> typesMap = new HashMap<Integer, Type>();
                {
                    final List<Type> originals = DBType.list(connection);
                    for( Type type : types ) {
                        int id = type.getId();
                        boolean found = false;
                        // try to find in originals first
                        for( final Type original : originals ) {
                            if( type.equals(original) ) {
                                type.setId( original.getId() );
                                found = true;
                                break;
                            }
                        }
                        if( !found ) {
                            type.resetId();
                            DBType.save( connection , type );
                        }
                        typesMap.put( id , type );
                    }
                }
                // Collections
                HashMap<Integer , Collection> collectionMap = new HashMap<Integer, Collection>();
                {
                    final List<Collection> originals = DBCollection.list(connection);
                    for( Collection collection : collections ) {
                        int id = collection.getId();
                        boolean found = false;
                        // try to find in originals first
                        for( final Collection original : originals ) {
                            if( collection.equals(original) ) {
                                collection.setId( original.getId() );
                                found = true;
                                break;
                            }
                        }
                        if( !found ) {
                            collection.resetId();
                            DBCollection.save( connection , collection );
                        }
                        collectionMap.put( id , collection );
                    }
                }
                // Items
                {
                    for( Item item : items ) {
                        int id = item.getId();
                        boolean found = false;

                        // Correct the merged data
                        {
                            // - Types
                            item.setType(typesMap.get(item.getType().getId()));

                            // - Collection
                            item.setCollection(collectionMap.get(item.getCollection().getId()));
                        }

                        // Fetch originals..
                        final List<Item> originals = DBItem.listByCollection(connection, item.getCollection().getId());

                        // try to find in originals first
                        for( final Item original : originals ) {
                            if( item.equals(original) ) {
                                item.setId( original.getId() );
                                found = true;
                                break;
                            }
                        }
                        if( !found ) {
                            item.resetId();
                            DBItem.save( connection , item );
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Common.toastLong(this, e);
            return;
        }

        dbText.setText("");
        Common.toastLong(this, getString(R.string.db_import_success));

        // Reset.
        Intent intent = new Intent(SettingsActivity.this, CollectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void exportDB(View view) {
        // Get all data from DB..

        List<Type> types = DBType.list(connection);
        List<Collection> collections = DBCollection.list(connection);
        List<Item> items = DBItem.list(connection);

        JSONArray typesjs = new JSONArray();
        JSONArray collectionsjs = new JSONArray();
        JSONArray itemsjs = new JSONArray();

        {
            for (final Type item : types) {
                JSONObject object = item.toJSON();
                if (object != null) {
                    typesjs.put(object);
                }
            }
        }
        {
            for (final Collection item : collections) {
                JSONObject object = item.toJSON();
                if (object != null) {
                    collectionsjs.put(object);
                }
            }
        }
        {
            for (final Item item : items) {
                JSONObject object = item.toJSON();
                if (object != null) {
                    itemsjs.put(object);
                }
            }
        }

        JSONObject root = new JSONObject();
        try {
            root.put(Type.PLURAL , typesjs);
            root.put(Collection.PLURAL , collectionsjs);
            root.put(Item.PLURAL , itemsjs);
        } catch (JSONException e) {
            Common.toastLong(this, e);
            return;
        }
        dbText.setText(root.toString());
        Common.toastLong(this, getString(R.string.db_export_success));
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
