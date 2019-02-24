package com.aaronsng.wheresitgo.fragment;

import android.content.Context;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.activity.DrawerMainActivity;
import com.aaronsng.wheresitgo.adapter.CategoryListViewAdapter;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.DeleteRecordRequest;
import com.aaronsng.wheresitgo.volley.RetrieveAllCategoriesRequest;
import  com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryHomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryHomeFragment extends ListFragment implements RetrieveAllCategoriesRequest.Listener, SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REFRESH_CATEGORY = 124;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView noCategoryTextView;
    private View mView;

    private OnFragmentInteractionListener mListener;
    SwipeRefreshLayout swipeRefreshLayout ;
    ArrayList<Category> categoryList = new ArrayList<Category>();
    ProgressDialog progressDialog;



    public CategoryHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryHomeFragment newInstance(String param1, String param2) {
        CategoryHomeFragment fragment = new CategoryHomeFragment();
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        retrieveCategory();
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

    }

    private void retrieveCategory() {

        HashMap<String, String> parameter = new HashMap<String, String>();
        StoredObject so = new StoredObject(getActivity());
        parameter.put("user_id", so.getUserDetails().getUser_id());
        parameter.put("username", so.getUserDetails().getUsername());
        parameter.put("password",so.getUserDetails().getPassword());
        //Server Url
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }
        RetrieveAllCategoriesRequest categoryRequest = new RetrieveAllCategoriesRequest(Request.Method.POST,parameter, this, urlHead);
        categoryRequest.setTag(Config.VolleyID);
        categoryRequest.setShouldCache(false);
        VolleySingleton.getInstance(getActivity());
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(categoryRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_category_home, container, false);
        noCategoryTextView = (TextView)mView.findViewById(R.id.textViewEmptyCategory);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return mView;
    }

    @Override
    public void onSuccess(ArrayList<Category> categoryArrayList){
        swipeRefreshLayout.setRefreshing(false);

        if(categoryArrayList.size()>0) {
            noCategoryTextView.setVisibility(View.GONE);
            CategoryListViewAdapter adapter = new CategoryListViewAdapter(getActivity(), categoryArrayList);
            Log.d(Config.log_id, "onSuccess");
            swipeRefreshLayout.setRefreshing(false);
            this.categoryList.clear();
            setListAdapter(null);
            setListAdapter(adapter);
            this.categoryList = categoryArrayList;

        }else
        {
            noCategoryTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Class fragmentClass = CategoryRecordsFragment.class;
        Bundle args = new Bundle();
        args.putSerializable("Category", categoryList.get(position));
        try {
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(args);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.drawer_content, fragment);
            fragmentTransaction.addToBackStack("CATEGORY_RECORD");
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        retrieveCategory();
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
            case REFRESH_CATEGORY:
                mListener.onRefreshCategory();
                break;

        }
    }

    public void onFailure(Config.RequestStatusCode code) {
        try {
            Log.e(Config.log_id, "onFailure");
            swipeRefreshLayout.setRefreshing(false);

            if(code==Config.RequestStatusCode.AUTH_FAILED){

            }
            if(code==Config.RequestStatusCode.GENERAL_ERROR){

            }
            mListener.onSnackBarEvent("Network Error");
        }
        catch (Exception e){

        }
    }

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
        public void onRefreshCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            getActivity().setTitle("My Categories");
            if(getListView() != null){
                getListView().setAdapter(null);
            }
            retrieveCategory();
        }catch (Exception e){

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




}
