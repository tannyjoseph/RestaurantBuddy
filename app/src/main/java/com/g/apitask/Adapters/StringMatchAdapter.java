package com.g.apitask.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StringMatchAdapter extends ArrayAdapter implements Filterable{

    private List<String> allCodes;
    private List<String> originalCodes;
    String id;

    StringFilter filter;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StringMatchAdapter(Context context, int resource, List<String> keys) {
        super(context, resource, keys);

        allCodes=keys;
        originalCodes=keys;
        this.id = id;


    }

    public int getCount() {
        int i = 0;
        try {
            i = allCodes.size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i;
    }

    public Object getItem(int position) {
        return allCodes.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private class StringFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = originalCodes;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allCodes = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
    @Override
    public Filter getFilter()
    {
        return new StringFilter();
    }

}
