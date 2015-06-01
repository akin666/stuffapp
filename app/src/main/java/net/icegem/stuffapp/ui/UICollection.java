package net.icegem.stuffapp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.icegem.stuffapp.R;
import net.icegem.stuffapp.data.Collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 1.6.2015.
 */
public class UICollection {
    public static class RowAdapter  extends net.icegem.stuffapp.ui.RowAdapter<Collection> implements Filterable {
        public RowAdapter(Context context, List<Collection> items) {
            super(context, R.layout.row_collection_view, items);
        }

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults result = new FilterResults();
                    result.values = original;

                    if (constraint != null && (!constraint.equals(""))) {
                        final ArrayList<Collection> array = new ArrayList<Collection>();
                        for (final Collection item : original) {
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
                    list = (List<Collection>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        private class Tag {
            public TextView name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final Tag tag;

            if(convertView == null) {
                view = inflater.inflate(resourceId, parent, false);
                tag = new Tag();
                tag.name = (TextView)view.findViewById(R.id.name);
                view.setTag(tag);
            }
            else {
                view = convertView;
                tag = (Tag)view.getTag();
            }

            Collection collection = (Collection)getItem(position);

            tag.name.setText( collection.getName().toString() );

            return view;
        }
    }
}
