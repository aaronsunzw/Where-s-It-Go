package com.aaronsng.wheresitgo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Record;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aaron on 08-May-17.
 */

public class SearchRecordListViewAdapter extends BaseAdapter implements Filterable{
    Context context;
    ArrayList<Record> recordList;
    ArrayList<Record> recordListFiltered;
    private LayoutInflater inflater = null;
    private String serverUrl;
    private RecordFilter recordFilter;

    public SearchRecordListViewAdapter(Context context, ArrayList<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recordListFiltered = recordList;
        getFilter();
    }

    @Override
    public int getCount() {
        return recordListFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return recordListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        view = inflater.inflate(R.layout.item_listview_record,null);

        TextView textViewRecordTitle = (TextView) view.findViewById(R.id.text_view_record_title);
        TextView textViewRecordDesc = (TextView) view.findViewById(R.id.text_view_record_desc);

        System.out.println("Category get title : "+ recordListFiltered.get(position).getRecord_title());//dun set 1st
        textViewRecordTitle.setText(recordListFiltered.get(position).getRecord_title());
        if (recordListFiltered.get(position).getRecord_description()!="null"){
            textViewRecordDesc.setText(recordListFiltered.get(position).getRecord_description());
        }else
            textViewRecordDesc.setText("No record description");
        //Server Url
        StoredObject so = new StoredObject(view.getContext().getApplicationContext());
        if (so.getServerUrl().contentEquals("")){
            serverUrl = Config.url;
        }else{
            serverUrl = so.getServerUrl();
        }

        ImageView image_view_record= (ImageView) view.findViewById(R.id.image_view_record);
        Log.e(Config.log_id,serverUrl+"public/image/user_photos/"+recordListFiltered.get(position).getRecord_image());
        Picasso.with(context)
                .load(serverUrl+"public/image/user_photos/"+recordListFiltered.get(position).getRecord_image())
                .error(R.drawable.ic_menu_gallery)
                .resize(50, 50)
                .centerCrop()
                .into(image_view_record);


        return view;
    }

    @Override
    public Filter getFilter() {
        if (recordFilter == null) {
            recordFilter = new RecordFilter();
        }
        return recordFilter;
    }

    private class RecordFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Record> tempList = new ArrayList<Record>();

                // search content in record list
                for (Record record : recordList) {
                    if (record.getRecord_title().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(record);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = recordList.size();
                filterResults.values = recordList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recordListFiltered = (ArrayList<Record>) results.values;
            notifyDataSetChanged();
        }
    }
}
