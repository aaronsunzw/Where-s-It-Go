package com.aaronsng.wheresitgo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaronsng.wheresitgo.model.Category;
import com.android.volley.toolbox.ImageLoader;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.model.Category;

import java.util.ArrayList;

/**
 * Created by aaron on 13-Apr-17.
 */

public class CategoryListViewAdapter extends BaseAdapter{

    Context context;
    ArrayList<Category> categoryList;
    private LayoutInflater inflater = null;

    public CategoryListViewAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoryList.size();
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

        view = inflater.inflate(R.layout.item_listview_category,null);

        TextView textViewCategoryTitle = (TextView) view.findViewById(R.id.text_view_category_title);
        TextView textViewCategoryDesc = (TextView) view.findViewById(R.id.text_view_category_desc);

        System.out.println("Category get title : "+ categoryList.get(position).getCategory_title());//dun set 1st
        textViewCategoryTitle.setText(categoryList.get(position).getCategory_title());
        if (categoryList.get(position).getCategory_description()!="null"){
            textViewCategoryDesc.setText(categoryList.get(position).getCategory_description());
        }else
            textViewCategoryDesc.setText("No description");


        return view;
    }



}
