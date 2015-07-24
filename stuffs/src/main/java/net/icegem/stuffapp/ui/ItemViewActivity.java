package net.icegem.stuffapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.data.Collection;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.Settings;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBItem;

public class ItemViewActivity extends AppCompatActivity {
    private DBConnection connection;
    private Item item = null;
    private int uid = 0;

    private WebView web = null;
    private TextView volume = null;
    private TextView code = null;
    private TextView type = null;
    private TextView location = null;
    private TextView description = null;
    ImageView picture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        connection = new DBConnection(this);

        web = (WebView) findViewById(R.id.webView);
        location = (TextView) findViewById(R.id.location);
        picture = (ImageView)findViewById(R.id.picture);

        volume = (TextView) findViewById(R.id.volume);
        code = (TextView) findViewById(R.id.code);
        type = (TextView) findViewById(R.id.type);
        description = (TextView) findViewById(R.id.description);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        item = bundle.getParcelable(Item.class.getName());

        if( item == null ) {
            throw new Resources.NotFoundException("No item specified. You should call ItemEditActivity instead.");
        }

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        if (item == null) {
            Common.toast(this, "Item does not exist.");
            return;
        }

        Uri uri = item.getPictureUri();
        if( uri != null ) {
            picture.setImageURI(uri);
        }

        Collection collection = item.getCollection();
        if( collection != null ) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(collection.getName().toString());
        }

        volume.setText(item.getVolume());
        code.setText(item.getCode());
        type.setText(item.getType().toString());
        location.setText(item.getLocation());
        description.setText(item.getDescription().toString());

        String code = item.getCode();
        if (code != null && (!code.isEmpty()))
        {
            String url = Settings.searchUrl + code;

            WebSettings settings = web.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            web.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                }
            });

            web.loadUrl(url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuID = menuitem.getItemId();

        switch( menuitem.getItemId() )
        {
            case R.id.action_settings :
            {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_edit :
            {
                Intent intent = new Intent(this, ItemEditActivity.class);
                intent.putExtra(Item.class.getName() , item);
                startActivityForResult(intent, 0);
                return true;
            }
            case R.id.action_delete :
            {
                Common.toast(this, "delete requested");

                Common.question(
                        this,
                        getString(R.string.delete),
                        getString(R.string.delete_item_sure),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DBItem.delete(connection, item);
                                finish();
                            }
                        },
                        null);

                return true;
            }
            case R.id.action_reset :
            {
                refresh();
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(menuitem);
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

        // Item action.
        if(action.equals(Item.EDIT_ACTION)) {
            if (resultCode == RESULT_OK) {
                Item item = (Item)intent.getParcelableExtra( Item.class.getName() );

                if( item != null ) {
                    DBItem.save(connection, item);
                    this.item = item;
                    refresh();
                }
            }
        }
    }
}
