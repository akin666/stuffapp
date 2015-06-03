package net.icegem.stuffapp.ui;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.icegem.stuffapp.MutablePair;
import net.icegem.stuffapp.R;
import net.icegem.stuffapp.TextChangeWatcher;
import net.icegem.stuffapp.data.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 2.6.2015.
 */
public class UIText {
    public static class RowAdapter extends BaseAdapter {
        private int resourceId;
        private LayoutInflater inflater;
        private Text text;

        public RowAdapter(Context context, int resourceId, Text text) {
            this.inflater = LayoutInflater.from(context);
            this.text = text;
            this.resourceId = resourceId;
        }

        @Override
        public int getCount() {
            return text.size();
        }

        @Override
        public Object getItem(int position) {
            return text.at(position);
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

        public void add() {
            text.add();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final Tag tag;

            if(convertView == null) {
                view = inflater.inflate(resourceId, parent, false);
                tag = new Tag();
                view.setTag(tag);

                // Setup tag
                tag.language = (TextView)view.findViewById(R.id.language);
                tag.text = (TextView)view.findViewById(R.id.text);
                tag.removeButton = (View)view.findViewById(R.id.remove);

                // Watchers
                tag.watcher = new TextChangeWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        tag.item.first = tag.language.getText().toString();
                        tag.item.second = tag.text.getText().toString();
                    }
                };

                tag.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( tag.item != null ) {
                            tag.item = null;
                            text.remove(tag.index);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
            else {
                view = convertView;
                tag = (Tag)view.getTag();

                // unregister listeners for a moment
                tag.language.removeTextChangedListener(tag.watcher);
                tag.text.removeTextChangedListener(tag.watcher);
            }

            tag.index = position;
            tag.item = text.at(position);

            tag.language.setText(tag.item.first);
            tag.text.setText(tag.item.second);

            // register listeners after the values has been assigned.
            tag.language.addTextChangedListener(tag.watcher);
            tag.text.addTextChangedListener(tag.watcher);

            return view;
        }
    }
}
