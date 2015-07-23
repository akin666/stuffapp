package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.DialogInterface;
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

        if( bundle != null ) {
            text = bundle.getParcelable(Text.class.getName());
            target = bundle.getString(Text.TARGET);
            extra = bundle.getParcelable(Text.EXTRA);
        }
        
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

    public void add(View view) {
        adapter.add();
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
        intent.setAction(Text.EDIT_ACTION);

        // http://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    @Override
    public void finish() {
        save();
        super.finish();
    }

    public void dismiss(View view) {
        finish();
    }
}
