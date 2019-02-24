package com.aaronsng.wheresitgo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.activity.CreateCategoryActivity;
import com.aaronsng.wheresitgo.activity.CreateRecordActivity;
import com.aaronsng.wheresitgo.activity.DrawerMainActivity;
import com.aaronsng.wheresitgo.activity.RecordDetailActivity;
import com.aaronsng.wheresitgo.adapter.RecordListViewAdapter;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.Record;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.DeleteCategoryRequest;
import com.aaronsng.wheresitgo.volley.DeleteRecordRequest;
import com.aaronsng.wheresitgo.volley.RetrieveAllRecordsRequest;
import com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryRecordsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryRecordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryRecordsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RetrieveAllRecordsRequest.Listener, DeleteCategoryRequest.Listener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REFRESH_RECORD = 124;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView noRecordTextView;
    TextView categoryTitleTextView;
    TextView categoryDescriptionTextView;
    private View mView;
    Category category;
    ListView recordListView;
    FloatingActionButton addRecordFAB;
    Button btnDeleteCategory;

    private OnFragmentInteractionListener mListener;
    SwipeRefreshLayout swipeRefreshLayout ;
    ArrayList<Record> recordList = new ArrayList<Record>();
    ProgressDialog progressDialog;
    Category targetCategory;

    public CategoryRecordsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryRecordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryRecordsFragment newInstance(String param1, String param2) {
        CategoryRecordsFragment fragment = new CategoryRecordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        retrieveAllRecords();
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    private void retrieveAllRecords() {
        category = (Category) getArguments().get("Category");
        HashMap<String, String> parameter = new HashMap<String, String>();
        StoredObject so = new StoredObject(getActivity());

        parameter.put("username", so.getUserDetails().getUsername());
        parameter.put("password",so.getUserDetails().getPassword());
        parameter.put("category_id", category.getCategory_id().toString());

        //Server Url
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        RetrieveAllRecordsRequest recordRequest = new RetrieveAllRecordsRequest(Request.Method.POST,parameter, this, urlHead);
        recordRequest.setTag(Config.VolleyID);
        recordRequest.setShouldCache(false);
        VolleySingleton.getInstance(getActivity());
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(recordRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_category_records, container, false);
        noRecordTextView = (TextView)mView.findViewById(R.id.textViewEmptyRecords);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_layout_records);
        swipeRefreshLayout.setOnRefreshListener(this);

        targetCategory = (Category) getArguments().get("Category");
        category = (Category) getArguments().get("Category");
        categoryTitleTextView =  (TextView) mView.findViewById(R.id.text_view_category_title);
        categoryDescriptionTextView =  (TextView) mView.findViewById(R.id.text_view_category_desc);
        categoryTitleTextView.setText(category.getCategory_title().toString());
        if (category.getCategory_description()!="null"){
            categoryDescriptionTextView.setText(category.getCategory_description().toString());
        }else{
            categoryDescriptionTextView.setText("No description");
        }

        addRecordFAB = (FloatingActionButton) mView.findViewById(R.id.fab_add_record);
        addRecordFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRecordActivity.class);
                StoredObject so = new StoredObject(getActivity().getApplicationContext());
                intent.putExtra("category",(Category) getArguments().get("Category"));
                startActivity(intent);

            }// end onClick
        });

        btnDeleteCategory = (Button) mView.findViewById(R.id.button_delete_category);
        btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteCategory(mView);
            }
        });

        return mView;
    }

    @Override
    public void onSuccess(ArrayList<Record> recordArrayList) {
        swipeRefreshLayout.setRefreshing(false);

        if(recordArrayList.size()>0) {
            noRecordTextView.setVisibility(View.GONE);
            RecordListViewAdapter adapter = new RecordListViewAdapter(getActivity(), recordArrayList);
            Log.d(Config.log_id, "onSuccess");
            swipeRefreshLayout.setRefreshing(false);
            this.recordList.clear();
            recordListView = (ListView) (swipeRefreshLayout.findViewById(R.id.list_records));
            recordListView.setAdapter(null);
            recordListView.setAdapter(adapter);
            recordList = recordArrayList;

            recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Record targetRecord = recordList.get(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("targetRecord", targetRecord);
                    Intent intent = new Intent(getActivity(), RecordDetailActivity.class);
                    intent.putExtra("targetRecord", targetRecord);
                    startActivity(intent);
                }
            });

        }else
        {
            noRecordTextView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onRefresh() {
        retrieveAllRecords();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REFRESH_RECORD:
                mListener.onRefreshRecord();
                break;

        }
    }



//    public void imageButtonAddRecord(View v){
//        Intent intent = new Intent(this.getContext(), CreateRecordActivity.class);
//        StoredObject so = new StoredObject(this.getContext().getApplicationContext());
//        intent.putExtra("category",(Category) getArguments().get("Category"));
//        startActivity(intent);
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        public void onSnackBarEvent(String text);
        public void onRefreshRecord();
    }



    @Override
    public void onResume() {
        super.onResume();
        try{
            getActivity().setTitle("Category Records");
            if(recordListView != null){
                recordListView.setAdapter(null);
            }
            retrieveAllRecords();

        }catch (Exception e){
            Log.e("ON_RESUME:", e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void btnDeleteCategory(View view){
        progressDialog.setMessage("Deleting category...");
        progressDialog.setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete this category? \nDeleting this category means that all records associated will be lost.\nPress OK to continue.");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                progressDialog.setCancelable(false);
                progressDialog.show();
                dialog.dismiss();
                deleteCategory();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteCategory(){
        User user = new StoredObject(getActivity().getApplicationContext()).getUserDetails();
        String category_id = targetCategory.getCategory_id().toString();
        String user_id = targetCategory.getUser_id().toString();

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("username", user.getUsername());
        parameter.put("password", user.getPassword());
        parameter.put("category_id", category_id);
        parameter.put("user_id", user_id);

        //Server Url
        StoredObject so = new StoredObject(this.getActivity().getApplicationContext());
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        DeleteCategoryRequest deleteCategoryRequest = new DeleteCategoryRequest(Request.Method.POST, parameter, this, urlHead);
        deleteCategoryRequest.setTag(Config.VolleyID);
        deleteCategoryRequest.setShouldCache(false);
        VolleySingleton.getInstance(getContext()).addToRequestQueue(deleteCategoryRequest);

    }

    @Override
    public void onSuccess() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Category successfully deleted." , Toast.LENGTH_LONG);
        toast.show();
        Intent i = new Intent(getActivity().getApplicationContext(),DrawerMainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
            mListener.onSnackBarEvent("Network Error, Please re-try again");
        }
        catch (Exception e){

        }
    }
}
