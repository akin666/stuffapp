package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.icegem.stuffapp.Item;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.Translated;

public class TranslatedEditActivity extends Activity {
    private Translated translated = null;
    private TranslatedRowAdapter adapter = null;
    private ListView itemlist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translated_edit_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        translated = bundle.getParcelable("translated");

        if( translated == null )
        {
            translated = new Translated();
        }

        refresh();
    }

    public void refresh()
    {
        if( translated != null ) {
            try {
                adapter = new TranslatedRowAdapter(this, R.layout.translated_row_layout, translated);
                itemlist = (ListView) findViewById(R.id.translation_list);
                itemlist.setAdapter(adapter);
            } catch (Exception e) {
                Common.toast(this, e.toString());
            }
        }
    }

    public void save()
    {
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putParcelable("translated", translated);
        intent.putExtras(bundle);
        intent.setAction( Translated.ACTION );

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void remove(View view) {
        Common.toast(this, "Clicked");
    }

    public void add(View view) {
        adapter.add();
    }

    public void save(View view) {
        Common.toastLong(this, getString(R.string.save_translation_edit));

        save();

        finish();
    }

    public void cancel(View view) {
        Common.toastLong(this, getString(R.string.cancel_translation_edit));

        finish();
    }
}
