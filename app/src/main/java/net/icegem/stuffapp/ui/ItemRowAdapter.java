package net.icegem.stuffapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import net.icegem.stuffapp.Item;
import net.icegem.stuffapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikael.korpela on 12.5.2015.
 *
 * Hurrah for
 * http://ocddevelopers.com/2014/extend-baseadapter-instead-of-arrayadapter-for-custom-list-items/
 */
public class ItemRowAdapter extends BaseAdapter implements Filterable
{
    private int resourceId;
    private LayoutInflater inflater;
    private List<Item> originals;
    private List<Item> items;

    public ItemRowAdapter(Context context, int resourceId, List<Item> items)
    {
        super();

        this.inflater = LayoutInflater.from(context);
        this.originals = items;
        this.items = items;
        this.resourceId = resourceId;
    }

    // Needed for ListView filtering..
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults result = new FilterResults();
                result.values = originals;

                if (constraint != null && (!constraint.equals(""))) {
                    final ArrayList<Item> array = new ArrayList<Item>();
                    for (final Item item : originals) {
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
                items = (List<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Tag {
        public TextView name;
        public TextView location;
        public TextView type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final Tag tag;

        if(convertView == null)
        {
            view = inflater.inflate(resourceId, parent, false);
            tag = new Tag();
            tag.name = (TextView)view.findViewById(R.id.name);
            tag.location = (TextView)view.findViewById(R.id.location);
            tag.type = (TextView)view.findViewById(R.id.type);
            view.setTag(tag);
        }
        else
        {
            view = convertView;
            tag = (Tag)view.getTag();
        }

        Item item = items.get(position);

        tag.name.setText( item.getName() );
        tag.location.setText( item.getLocation() );
        tag.type.setText( item.getType() );

        return view;
    }
}