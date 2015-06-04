package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.database.DBText;

public class CollectionEditActivity extends AppCompatActivity {

    private Collection collection = null;

    private EditText name;
    private EditText description;
    private EditText picture;
    private EditText link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_edit);

        Intent intent = getIntent();

        this.collection = intent.getParcelableExtra(Collection.class.getName());

        if(this. collection == null ) {
            this.collection = new Collection();
        }

        name = (EditText)findViewById(R.id.name);
        description = (EditText)findViewById(R.id.description);
        picture = (EditText)findViewById(R.id.picture);
        link = (EditText)findViewById(R.id.name);

        final Collection collection = this.collection;
        final Activity activity = this;
        name.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TextEditActivity.class);

                intent.putExtra(Text.class.getName() , collection.getName());
                intent.putExtra(Text.TARGET , Collection.COLUMN_NAME);

                activity.startActivityForResult(intent, 0);
            }
        });
        description.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TextEditActivity.class);

                intent.putExtra(Text.class.getName() , collection.getDescription());
                intent.putExtra(Text.TARGET , Collection.COLUMN_DESCRIPTION);

                activity.startActivityForResult(intent, 0);
            }
        });

        refresh();
    }

    public void refresh() {
        name.setText(collection.getName().toString());
        description.setText(collection.getDescription().toString());
        picture.setText(collection.getPicture());
        link.setText(collection.getLink());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save() {
        // From ui..
        collection.setPicture( picture.getText().toString() );
        collection.setLink( picture.getText().toString() );

        Intent intent = new Intent();

        intent.putExtra(Collection.class.getName(), collection);
        intent.setAction(Collection.EDIT_ACTION);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void save(View view) {
        Common.toastLong(this, getString(R.string.action_save_collection));

        save();

        finish();
    }

    public void cancel(View view) {
        Common.toastLong(this, getString(R.string.action_cancel));

        finish();
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

        // Translation action.
        if(action.equals(Text.EDIT_ACTION)) {
            if (resultCode == RESULT_OK) {
                String target = intent.getStringExtra(Text.TARGET);
                Text text = (Text)intent.getParcelableExtra( Text.class.getName() );

                if( target.equals(Collection.COLUMN_NAME) ) {
                    collection.setName(text);
                    name.setText(collection.getName().toString());
                }
                if( target.equals(Collection.COLUMN_DESCRIPTION) ) {
                    collection.setDescription(text);
                    description.setText(collection.getDescription().toString());
                }
            }
        }
    }
}
