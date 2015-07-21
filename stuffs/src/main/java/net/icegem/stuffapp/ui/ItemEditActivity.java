package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBConnection;

public class ItemEditActivity extends Activity {

    private DBConnection connection;
    private Item item = null;
    private int uid = 0;

    TextView code = null;
    TextView description = null;
    TextView type = null;
    TextView volume = null;
    TextView link = null;
    TextView picture = null;
    TextView location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        connection = new DBConnection(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if( bundle != null ) {
            item = bundle.getParcelable(Item.class.getName());
        }

        if( item == null ) {
            item = new Item();
        }

        code = (TextView)findViewById(R.id.code);
        description = (TextView)findViewById(R.id.description);
        type = (TextView)findViewById(R.id.type);
        volume = (TextView)findViewById(R.id.volume);
        link = (TextView)findViewById(R.id.link);
        picture = (TextView)findViewById(R.id.picture);
        location = (TextView)findViewById(R.id.location);

        refresh();
    }

    public void refresh()
    {
        Text descriptiontmp = item.getDescription();
        Type typetmp = item.getType();

        code.setText( item.getCode() );
        if( descriptiontmp != null ) {
            description.setText(descriptiontmp.toString());
        }
        if( typetmp != null ) {
            type.setText( typetmp.toString() );
        }
        volume.setText( item.getVolume() );
        link.setText( item.getLink() );
        picture.setText( item.getPicture() );
        location.setText(item.getLocation());

        /*
        final Activity activity = this;
        itemName.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TextEditActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("translated", item.getNameObject());
                intent.putExtras(bundle);

                startActivityForResult(intent, 0);
            }
        });
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        String barcodeResult = Barcode.getLastResult();
        if( barcodeResult != null ) {
            barcode(barcodeResult);
        }
    }

    public void readBarcode(View view) {
        Common.toastLong(this, getString(R.string.start_barcodereader));

        Barcode.read( this );
    }

    public void save()
    {
        /*
        try {
            item.setId(itemId.getText().toString());
            item.setLocation(itemLocation.getText().toString());
            item.setType(itemType.getText().toString());

            datasource.save(item);
        } catch (Exception e) {
            Common.toastLong(this, "Failed to save item.: " + e.getMessage());
        }
        */
    }

    public void save(View view) {
        Common.toastLong(this, getString(R.string.save_item_edit));

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
                Bundle bundle = intent.getExtras();

                String target = bundle.getParcelable(Text.TARGET);
                Text text = bundle.getParcelable(Text.class.getName());

                if( text != null ) {
                    /*
                    item.setName(translated);
                    itemName.setText( item.getName() );
                    */
                }
            }
        }
    }

    public void barcode(String result) {
        if( code != null ) {
            code.setText(result);
        }
    }
}
