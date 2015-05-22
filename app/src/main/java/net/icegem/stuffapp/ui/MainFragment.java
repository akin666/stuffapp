package net.icegem.stuffapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import net.icegem.stuffapp.Barcode;
import net.icegem.stuffapp.Item;
import net.icegem.stuffapp.ItemDataSource;
import net.icegem.stuffapp.MainActivity;
import net.icegem.stuffapp.R;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements SearchView.OnQueryTextListener {

    private ItemDataSource datasource = null;
    private ListView itemlist = null;
    private SearchView search = null;

    public MainFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        datasource = new ItemDataSource(activity);
    }

    private void refresh()
    {
        if( datasource == null || itemlist == null )
        {
            return;
        }

        try {
            List<Item> items = datasource.getItems();
            ListAdapter adapter = new ItemRowAdapter(getActivity(), R.layout.item_row_layout, items);
            itemlist.setAdapter(adapter);
            itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Item item = (Item) parent.getItemAtPosition(position);
                    if (item == null) {
                        return;
                    }

                    try {
                        Intent intent = new Intent(getActivity(), ItemActivity.class);
                        intent.putExtra("id", item.getUID());
                        startActivity(intent);
                    } catch (Exception e) {
                        Common.log(getActivity(), "Exception: " + e.toString());
                    }
                }
            });
            itemlist.setTextFilterEnabled(true);

            setupSearch();
        }
        catch(Exception e)
        {
            Common.log(getActivity(), e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        itemlist = (ListView) view.findViewById(R.id.ItemList);
        search = (SearchView) view.findViewById(R.id.search);

        return view;
    }

    private void setupSearch()
    {
        search.setIconifiedByDefault(true);
        search.setOnQueryTextListener(this);
        search.setSubmitButtonEnabled(false);
        search.setQueryRefinementEnabled(false);
        search.setQueryHint(getString(R.string.search));
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (TextUtils.isEmpty(newText)) {
            itemlist.clearTextFilter();
        } else {
            itemlist.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();

        String barcodeResult = Barcode.getLastResult();
        if( barcodeResult != null )
        {
            barcode(barcodeResult);
        }
    }

    public void barcode(String result) {
        search.setQuery(result, false);
        search.clearFocus();
    }
}
