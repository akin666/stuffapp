package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.Item;
import net.icegem.stuffapp.ItemDataSource;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.Settings;
import net.icegem.stuffapp.Translated;

public class ItemEditActivity extends Activity {

    private ItemDataSource datasource;
    private Item item = null;
    private int uid = 0;
    TextView itemId = null;
    TextView itemLocation = null;
    TextView itemType = null;
    TextView itemName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        datasource = new ItemDataSource(this);

        Intent intent = getIntent();

        uid = intent.getIntExtra("id", -1);

        if( uid == -1 )
        {
            try
            {
                item = new Item("","");
                uid = item.getUID();
            }
            catch (Exception e)
            {
            }
        }
        else
        {
            try
            {
                item = datasource.getItem(uid);
            }
            catch (Exception e)
            {
            }
        }

        refreshPage();
    }

    public void refreshPage()
    {
        if( item != null ) {
            itemId = (TextView)findViewById(R.id.item_id);
            itemLocation = (TextView)findViewById(R.id.item_location);
            itemName = (TextView)findViewById(R.id.item_name);
            itemType = (TextView)findViewById(R.id.type);

            itemId.setText( item.getID() );
            itemLocation.setText( item.getLocation() );
            itemName.setText(item.getName());
            itemType.setText(item.getType());

            final Activity activity = this;
            itemName.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, TranslatedEditActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("translated", item.getNameObject());
                    intent.putExtras(bundle);

                    startActivityForResult(intent, 0);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String barcodeResult = Barcode.getLastResult();
        if( barcodeResult != null )
        {
            barcode(barcodeResult);
        }
    }

    public void readBarcode(View view) {
        Common.longLog(this, getString(R.string.start_barcodereader));

        Barcode.read( this );
    }

    public void save()
    {
        try {
            item.setId(itemId.getText().toString());
            item.setLocation(itemLocation.getText().toString());
            item.setType(itemType.getText().toString());

            datasource.save(item);
        } catch (Exception e) {
            Common.longLog(this, "Failed to save item.: " + e.getMessage());
        }
    }

    public void save(View view) {
        Common.longLog(this, getString(R.string.save_item_edit));

        save();

        finish();
    }

    public void cancel(View view) {
        Common.longLog(this, getString(R.string.cancel_item_edit));

        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if( Barcode.onActivityResult(requestCode, resultCode, intent) )
        {
            return;
        }

        if( intent == null )
        {
            return;
        }

        String action = intent.getAction();
        if( action == null )
        {
            return;
        }

        // Translation action.
        if(action.equals(Translated.ACTION)) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = intent.getExtras();

                Translated translated = bundle.getParcelable("translated");

                if( translated != null )
                {
                    item.setName(translated);
                    itemName.setText( item.getName() );
                }
            }
        }
    }

    public void barcode(String result) {
        if( itemId != null )
        {
            itemId.setText(result);
        }
    }
}
