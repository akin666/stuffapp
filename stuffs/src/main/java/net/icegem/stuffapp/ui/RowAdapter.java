package net.icegem.stuffapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by mikael.korpela on 1.6.2015.
 */
public abstract class RowAdapter<T> extends BaseAdapter {
    protected int resourceId;
    protected LayoutInflater inflater;
    protected List<T> original;
    protected List<T> list;

    public RowAdapter(Context context, int resourceId, List<T> original)
    {
        super();

        this.inflater = LayoutInflater.from(context);
        this.original = original;
        this.list = original;
        this.resourceId = resourceId;
    }

    public void notifyDataSetChanged() {
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
