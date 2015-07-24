package net.icegem.stuffapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mikael.korpela on 1.6.2015.
 */
public abstract class RowAdapter<T> extends BaseAdapter {
    protected int resourceId;
    protected LayoutInflater inflater;
    protected List<T> original;
    protected List<T> list;
    private Comparator<T> comparator;

    public RowAdapter(Context context, int resourceId, List<T> original)
    {
        super();
        setup(original);

        this.inflater = LayoutInflater.from(context);
        this.resourceId = resourceId;
    }

    public void setup( List<T> original ) {
        this.original = original;
        this.list = original;
    }


    public void orderBy( Comparator<T> comparator ) {
        this.comparator = comparator;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if( comparator != null ) {
            Collections.sort(list, comparator);
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return original.size();
    }

    @Override
    public Object getItem(int position) {
        return original.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
