package net.icegem.stuffapp.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.icegem.stuffapp.MutablePair;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.TextChangeWatcher;
import net.icegem.stuffapp.Translated;

/**
 * Created by mikael.korpela on 15.5.2015.
 */
public class TranslatedRowAdapter extends BaseAdapter {
    private int resourceId;
    private LayoutInflater inflater;
    private Translated translated;

    public TranslatedRowAdapter(Context context, int resourceId, Translated translated)
    {
        this.inflater = LayoutInflater.from(context);
        this.translated = translated;
        this.resourceId = resourceId;
    }

    @Override
    public int getCount() {
        return translated.size();
    }

    @Override
    public Object getItem(int position) {
        return translated.at(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Tag {
        public TextView language;
        public TextView text;
        public View removeButton;

        public MutablePair<String,String> item;
        public TextChangeWatcher watcher;
        public int index;
    }

    public void add()
    {
        translated.add();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final Tag tag;

        // The next hassle tries to emulate two-way databinding..
        // it registers listeners, fills the forms, gets events and edits the translated items..
        // NOTE ui stuff etc are stored in TAG, the item is always fetched over and over again using indexes.
        if(convertView == null)
        {
            view = inflater.inflate(resourceId, parent, false);
            tag = new Tag();
            view.setTag(tag);

            // Populate tag
            tag.language = (TextView)view.findViewById(R.id.language);
            tag.text = (TextView)view.findViewById(R.id.text);
            tag.removeButton = (View)view.findViewById(R.id.remove);
            tag.item = translated.at(position);
            tag.watcher = new TextChangeWatcher() {
                @Override
                public void afterTextChanged(Editable s)
                {
                    tag.item.first = tag.language.getText().toString();
                    tag.item.second = tag.text.getText().toString();
                }
            };

            // Setup view
            tag.language.setText(tag.item.first);
            tag.text.setText(tag.item.second);

            tag.language.addTextChangedListener(tag.watcher);
            tag.text.addTextChangedListener(tag.watcher);


            // Delete button logic.
            tag.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( tag.item != null ) {
                            tag.item = null;
                            translated.remove(tag.index);
                            notifyDataSetChanged();
                        }
                    }
                }
            );
        }
        else
        {
            view = convertView;
            tag = (Tag)view.getTag();
            tag.item = translated.at(position);

            // unregister the listeners while we update the data..
            tag.language.removeTextChangedListener(tag.watcher);
            tag.text.removeTextChangedListener(tag.watcher);

            tag.language.setText( tag.item.first);
            tag.text.setText( tag.item.second );

            // reregister the listeners..
            tag.language.addTextChangedListener(tag.watcher);
            tag.text.addTextChangedListener(tag.watcher);
        }

        tag.index = position;

        return view;
    }
}
