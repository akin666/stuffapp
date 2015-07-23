package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBText;

public class TypeEditActivity extends Activity {
    private DBConnection connection;
    private ListView list = null;
    private UIType.Manager typeManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_edit);

        connection = new DBConnection(this);
        typeManager = new UIType.Manager(this,connection);

        list = (ListView) findViewById(R.id.list);

        refresh();
    }

    private void refresh() {
        if( connection == null || list == null ) {
            return;
        }

        try {
            list.setAdapter(typeManager);
        }
        catch(Exception e) {
            Common.toast(this, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    public void add(View view) {
        typeManager.save(new Type());
    }

    public void dismiss(View view) {
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

                // Save the database item..
                DBText.save(connection, text);
                if( target.equals(Type.class.getName()) ) {
                    Type type = (Type)intent.getParcelableExtra( Text.EXTRA );
                    if( type != null ) {
                        type.setName(text);
                        typeManager.save(type);
                    }
                    else {
                        Common.log("Error, Extra TYPE object was null");
                    }
                }
            }
        }
    }
}
