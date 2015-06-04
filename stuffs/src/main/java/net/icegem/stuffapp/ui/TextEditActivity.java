package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;

import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Text;

public class TextEditActivity extends Activity {
    private Text text = null;
    private String target = null;
    private Parcelable extra = null;
    private UIText.RowAdapter adapter = null;
    private ListView list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        text = bundle.getParcelable(Text.class.getName());
        target = bundle.getString(Text.TARGET);
        extra = bundle.getParcelable(Text.EXTRA);

        if( text == null ) {
            text = new Text();
        }
        list = (ListView) findViewById(R.id.list);

        refresh();
    }

    public void refresh() {
        if( text != null ) {
            try {
                adapter = new UIText.RowAdapter(this, R.layout.row_text_edit, text);
                list.setAdapter(adapter);
            } catch (Exception e) {
                Common.toast(this, e);
            }
        }
    }

    public void save() {
        Intent intent = new Intent();

        intent.putExtra(Text.class.getName(), text);
        if( target != null ) {
            intent.putExtra(Text.TARGET, target);
        }
        if( extra != null ) {
            intent.putExtra(Text.EXTRA, extra);
        }
        intent.setAction( Text.EDIT_ACTION );

        setResult(Activity.RESULT_OK, intent);
        finish();
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
        Common.toastLong(this, getString(R.string.action_cancel));

        finish();
    }
}
