package com.aaronsng.wheresitgo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Record;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aaron on 15-Apr-17.
 */

public class RecordListViewAdapter extends BaseAdapter{
    Context context;
    ArrayList<Record> recordList;
    private LayoutInflater inflater = null;
    String serverUrl;

    public RecordListViewAdapter(Context context, ArrayList<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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

        System.out.println("Category get title : "+ recordList.get(position).getRecord_title());//dun set 1st
        textViewRecordTitle.setText(recordList.get(position).getRecord_title());
        if (recordList.get(position).getRecord_description()!="null"){
            textViewRecordDesc.setText(recordList.get(position).getRecord_description());
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
        Log.e(Config.log_id,serverUrl+"public/image/user_photos/"+recordList.get(position).getRecord_image());
        Picasso.with(context)
                .load(serverUrl+"public/image/user_photos/"+recordList.get(position).getRecord_image())
                .error(R.drawable.ic_menu_gallery)
                .resize(50, 50)
                .centerCrop()
                .into(image_view_record);


        return view;
    }
}
