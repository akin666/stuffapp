package net.icegem.stuffapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import net.icegem.stuffapp.Item;
import net.icegem.stuffapp.ItemDataSource;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.Settings;

public class ItemActivity extends AppCompatActivity {
    private ItemDataSource datasource;
    private Item item = null;
    private int uid = 0;

    private WebView web = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        datasource = new ItemDataSource(this);
        Intent intent = getIntent();

        uid = intent.getIntExtra("id", -1);

        if( uid == -1 )
        {
            throw new RuntimeException("ItemActivity requires item ID.");
        }

        tv = (TextView) findViewById(R.id.location);
        web = (WebView) findViewById(R.id.webView);

        refreshPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPage();
    }

    public void refreshPage() {
        try {
            item = datasource.getItem(uid);
        } catch (Exception e) {
            throw new RuntimeException("ItemActivity requires item ID.");
        }

        if (item == null) {
            Common.log(this, "Item does not exist.");
            return;
        }

        tv.setText(item.getLocation());

        String itemID = item.getID();
        if (itemID != null && (!itemID.isEmpty()))
        {
            String url = Settings.searchUrl + itemID;

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
                intent.putExtra("id" , item.getUID() );
                startActivity(intent);
                return true;
            }
            case R.id.action_delete :
            {
                Common.log(this, "delete requested");

                Common.question(
                        this,
                        getString(R.string.delete),
                        getString(R.string.delete_item_sure),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                datasource.deleteItem(uid);
                                finish();
                            }
                        },
                        null);

                return true;
            }
            case R.id.action_reset :
            {
                refreshPage();
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(menuitem);
    }
}
