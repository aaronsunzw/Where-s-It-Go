package com.aaronsng.wheresitgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import  com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.CommonOperation;
import  com.aaronsng.wheresitgo.common.Config;
import  com.aaronsng.wheresitgo.common.StoredObject;
import  com.aaronsng.wheresitgo.model.User;
import  com.aaronsng.wheresitgo.volley.CreateCategoryRequest;
import  com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;

import java.util.HashMap;
import java.util.regex.Pattern;

public class CreateCategoryActivity extends AppCompatActivity implements CreateCategoryRequest.Listener{
    ProgressDialog progressDialog;
    private static final String alphanumeric =
            "[\\w\\s]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        setTitle("Create Category");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog=  new ProgressDialog(CreateCategoryActivity.this);
        StoredObject so = new StoredObject(getApplicationContext());
        EditText editTextCategoryTitle = (EditText) findViewById(R.id.edit_text_category_title);
        EditText editTextCategoryDescription= (EditText) findViewById(R.id.edit_text_category_desc);



    }




    public void btnCreateCategory(View abc) {
        User user= new StoredObject(getApplicationContext()).getUserDetails();

        boolean wrongInput = false;
        findViewById(R.id.edit_text_category_title).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_text_category_desc).setVisibility(View.INVISIBLE);
        EditText editTextCategoryTitle= (EditText) findViewById(R.id.edit_text_category_title);
        EditText editTextCategoryDescription= (EditText) findViewById(R.id.edit_text_category_desc);

        if (editTextCategoryTitle.getText().toString().matches("")) {
            findViewById(R.id.edit_text_category_title).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
            }
            catch (Exception e)
            {
            }
            wrongInput = true;
        }

        if (!Pattern.compile(alphanumeric).matcher(editTextCategoryTitle.getText().toString()).matches()) {
            findViewById(R.id.edit_text_category_title).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }

//        if (!Pattern.compile(alphanumeric).matcher(editTextCategoryDescription.getText().toString()).matches()) {
//            findViewById(R.id.edit_text_category_desc).setVisibility(View.VISIBLE);
//            try{
//                progressDialog.dismiss();
//            }
//            catch (Exception e)
//            {
//            }
//            wrongInput = true;
//
//        }
        if(wrongInput)
            return;

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("user_id", user.getUser_id());
        parameter.put("username", user.getUsername());
        parameter.put("password", user.getPassword());
        parameter.put("category_title", editTextCategoryTitle.getText().toString().trim());
        parameter.put("category_description", editTextCategoryDescription.getText().toString().trim());

        StoredObject so = new StoredObject(this.getApplicationContext());
        //Server Url
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(Request.Method.POST,parameter, this, urlHead);
        categoryRequest.setTag(Config.VolleyID);
        categoryRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(categoryRequest);

        try{
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess() {
        StoredObject so = new StoredObject(getApplicationContext());
        onBackPressed();
        Toast toast = Toast.makeText(getApplicationContext(),"Category successfully created." , Toast.LENGTH_LONG);
        toast.show();

        try{
            progressDialog.dismiss();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void onFailure(Config.RequestStatusCode code) {
        try{
            progressDialog.dismiss();
        }
        catch (Exception e) {

        }
        CoordinatorLayout snackbarCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.snackbarCoordinatorLayout);
        Snackbar snackbar;
        if(code==Config.RequestStatusCode.AUTH_FAILED){

            new CommonOperation().showSnackBar(snackbarCoordinatorLayout,"Authentication Failed");
        }
        if(code==Config.RequestStatusCode.GENERAL_ERROR){


            new CommonOperation().showSnackBar(snackbarCoordinatorLayout, "Network Error");
        }
    }
}
