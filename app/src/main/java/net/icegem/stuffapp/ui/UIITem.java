package net.icegem.stuffapp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 1.6.2015.
 */
public class UIITem {
    public static class RowAdapter  extends net.icegem.stuffapp.ui.RowAdapter<Item> implements Filterable {
        public RowAdapter(Context context, List<Item> items) {
            super(context, R.layout.row_item_view, items);
        }

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults result = new FilterResults();
                    result.values = original;

                    if (constraint != null && (!constraint.equals(""))) {
                        final ArrayList<Item> array = new ArrayList<Item>();
                        for (final Item item : original) {
                            if (item.contains(constraint.toString())) {
                                array.add(item);
                            }
                        }
                        result.values = array;
                    }
                    return result;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list = (List<Item>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        private class Tag {
            public TextView name;
            public TextView type;
            public TextView location;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final Tag tag;

            if(convertView == null) {
                view = inflater.inflate(resourceId, parent, false);
                tag = new Tag();
                view.setTag(tag);

                tag.name = (TextView)view.findViewById(R.id.name);
                tag.type = (TextView)view.findViewById(R.id.type);
                tag.location = (TextView)view.findViewById(R.id.location);
            }
            else {
                view = convertView;
                tag = (Tag)view.getTag();
            }

            Item item = (Item)getItem(position);

            tag.name.setText( item.getVolume().toString() );
            tag.type.setText( item.getType().toString() );
            tag.location.setText( item.getLocation() );

            return view;
        }
    }
}
