package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.Helpers;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Item;
import net.icegem.stuffapp.data.Text;
import net.icegem.stuffapp.data.Type;
import net.icegem.stuffapp.database.DBConnection;
import net.icegem.stuffapp.database.DBType;

import java.util.List;

public class ItemEditActivity extends Activity {

    private DBConnection connection;
    private Item item = null;

    List<Type> types;
    TextView code = null;
    TextView description = null;
    Spinner type = null;
    TextView volume = null;
    TextView link = null;
    ImageView picture = null;
    TextView location = null;

    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        type = (Spinner)findViewById(R.id.type);
        volume = (TextView)findViewById(R.id.volume);
        link = (TextView)findViewById(R.id.link);
        picture = (ImageView)findViewById(R.id.picture);
        location = (TextView)findViewById(R.id.location);

        // Add click listener, so that we can edit the Text object in a different way..
        final Item item = this.item;
        final Activity activity = this;
        description.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TextEditActivity.class);

                intent.putExtra(Text.class.getName() , item.getDescription());
                intent.putExtra(Text.TARGET , Item.COLUMN_DESCRIPTION);

                activity.startActivityForResult(intent, 0);
            }
        });

        // Picture click
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ImageActivity.class);
                intent.putExtra("width" , 1080 );
                Uri uri = Helpers.stringToUri(item.getPicture());
                if( uri != null ) {
                    intent.setData(uri);
                }
                startActivityForResult(intent, 0);
                saveState();
            }
        });

        // Setup types..
        types = DBType.list(connection);
        ArrayAdapter<Type> adapter = new ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.setType( types.get(position) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if( types.size() < 1 ) {
            // FAULT! type size must be more than 0.
            Common.toastLong(this, getString(R.string.define_some_types));

            Intent intent2 = new Intent(activity, TypeEditActivity.class);
            activity.startActivity(intent2);
            finish();

            return;
        }

        if( item.getType() == null ) {
            item.setType( types.get(0) );
        }

        refresh();
    }

    private void setupType( Type type ) {
        for( int i = 0 ; i < types.size() ; ++i ) {
            if( types.get(i).getId() == type.getId() ) {
                this.type.setSelection( i );
                break;
            }
        }
    }

    private void setupPicture() {
        Uri uri = Helpers.stringToUri(item.getPicture());
        if( uri != null ) {
            picture.setImageURI(uri);
            return;
        }
        picture.setImageBitmap(Helpers.emptyBitMap(getString(R.string.no_image), 100, 100));
    }

    public void refresh()
    {
        Text description = item.getDescription();

        code.setText(item.getCode());
        if( description != null ) {
            this.description.setText(description.toString());
        }
        setupType(item.getType());

        volume.setText(item.getVolume());
        link.setText( item.getLink() );
        setupPicture();
        location.setText(item.getLocation());
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
        try {
            item.setCode(code.getText().toString());
            item.setVolume(volume.getText().toString());
            item.setLink(link.getText().toString());
            item.setLocation(location.getText().toString());

            Intent intent = new Intent();

            intent.putExtra(Item.class.getName(), item);
            intent.setAction(Item.EDIT_ACTION);

            setResult(Activity.RESULT_OK, intent);

            finish();
        } catch (Exception e) {
            Common.toastLong(this, "Failed to save item.: " + e.getMessage());
        }
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

    public void saveState() {
        getIntent().putExtra(Item.class.getName(), item);
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

                if( target == null ) {
                    return;
                }

                if( target.equals(Item.COLUMN_DESCRIPTION) ) {
                    item.setDescription(text);
                    description.setText(item.getDescription().toString());
                }
            }
            return;
        }

        // Image action.
        if(action.equals(ImageActivity.ACTION)) {
            if (resultCode == RESULT_OK) {
                item.setPicture( intent.getData().toString() );
                setupPicture();
                saveState();
            }
        }
    }

    public void barcode(String result) {
        if( code != null ) {
            code.setText(result);
        }
    }
}
