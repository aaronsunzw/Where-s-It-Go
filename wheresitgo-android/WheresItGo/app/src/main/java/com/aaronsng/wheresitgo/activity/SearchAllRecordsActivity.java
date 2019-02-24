package com.aaronsng.wheresitgo.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.adapter.SearchRecordListViewAdapter;
import com.aaronsng.wheresitgo.common.CommonOperation;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.Record;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.RetrieveAllRecordsRequest;
import com.aaronsng.wheresitgo.volley.SearchAllRecordsRequest;
import com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAllRecordsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener, SearchAllRecordsRequest.Listener{
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private SearchRecordListViewAdapter searchRecordListViewAdapter;
    ListView recordListView;
    SwipeRefreshLayout swipeRefreshLayout ;
    ArrayList<Record> recordList = new ArrayList<Record>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_records);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search All Records");
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_records_search);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        recordListView = (ListView) findViewById(R.id.search_all_records_list_view);

        searchAllRecords();
    }

    private void searchAllRecords() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        StoredObject so = new StoredObject(this.getApplicationContext());
        parameter.put("username", so.getUserDetails().getUsername());
        parameter.put("password",so.getUserDetails().getPassword());
        parameter.put("user_id",so.getUserDetails().getUser_id());

        //Server Url
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        SearchAllRecordsRequest recordRequest = new SearchAllRecordsRequest(Request.Method.POST,parameter, this, urlHead);
        recordRequest.setTag(Config.VolleyID);
        recordRequest.setShouldCache(false);
        VolleySingleton.getInstance(this);
        VolleySingleton.getInstance(this).addToRequestQueue(recordRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchRecordListViewAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSuccess(ArrayList<Record> recordArrayList) {
        swipeRefreshLayout.setRefreshing(false);

        if(recordArrayList.size()>0){
//            noRecordTextView.setVisibility(View.GONE);
            searchRecordListViewAdapter = new SearchRecordListViewAdapter(this, recordArrayList);
            this.recordList.clear();
            recordListView.setAdapter(null);
            recordListView.setAdapter(searchRecordListViewAdapter);
            this.recordList = recordArrayList;
            recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Record targetRecord = (Record) recordListView.getAdapter().getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("targetRecord", targetRecord);
                    Intent intent = new Intent(getApplicationContext(), RecordDetailActivity.class);
                    intent.putExtra("targetRecord", targetRecord);
                    startActivity(intent);
                    if (searchView.isShown()) {
                        searchMenuItem.collapseActionView();
                        searchView.setQuery("", false);
                    }
                }
            });
            recordListView.setTextFilterEnabled(false);

        }else{
//            noRecordTextView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFailure(Config.RequestStatusCode code) {
        try {
            Log.e(Config.log_id, "onFailure");
            swipeRefreshLayout.setRefreshing(false);

            if(code==Config.RequestStatusCode.AUTH_FAILED){

            }
            if(code==Config.RequestStatusCode.GENERAL_ERROR){

            }

        }
        catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchAllRecords();
    }
}
