package com.aaronsng.wheresitgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.regex.Pattern;

import  com.aaronsng.wheresitgo.R;
import  com.aaronsng.wheresitgo.common.Config;
import  com.aaronsng.wheresitgo.common.StoredObject;
import  com.aaronsng.wheresitgo.model.User;
import  com.aaronsng.wheresitgo.volley.RegisterRequest;
import  com.aaronsng.wheresitgo.volley.VolleySingleton;

public class RegisterActivity extends AppCompatActivity implements RegisterRequest.Listener{
    private static final String pwStrength =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";
    private static final String emailValid =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progressDialog=  new ProgressDialog(RegisterActivity.this);
        EditText editTextUsername = (EditText) findViewById(R.id.edit_text_register_username);
        EditText editTextLoginPassword= (EditText) findViewById(R.id.edit_text_register_password);
        EditText editTextName= (EditText) findViewById(R.id.edit_text_register_name);
        EditText editTextEmail= (EditText) findViewById(R.id.edit_text_register_email);

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
    public void btnRegisterUser(View abc)
    { try{

        progressDialog.setMessage("Thank you for your patience! Account registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    catch (Exception e)
    {

    }
        boolean wrongInput = false;
        findViewById(R.id.edit_text_register_email).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_text_register_username).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_text_register_password).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_text_register_name).setVisibility(View.INVISIBLE);
        EditText editTextUsername= (EditText) findViewById(R.id.edit_text_register_username);
        EditText editTextPassword= (EditText) findViewById(R.id.edit_text_register_password);
        EditText editTextName= (EditText) findViewById(R.id.edit_text_register_name);
        EditText editTextEmail= (EditText) findViewById(R.id.edit_text_register_email);
        //Feedback
        Toast toast = Toast.makeText(this.getApplicationContext(), "Place Holder", Toast.LENGTH_SHORT);
        String toastText = "";

        if (editTextUsername.getText().toString().matches("")) {
            findViewById(R.id.edit_text_register_username).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
                toastText += "-Username cannot be empty";

            }
            catch (Exception e)
            {
            }
            wrongInput = true;
        }
        if (!Pattern.compile(pwStrength).matcher(editTextPassword.getText().toString()).matches()) {
            findViewById(R.id.edit_text_register_password).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
                if(!toastText.contentEquals("")){
                    toastText += "\n-Password is not valid";
                }else{
                    toastText += "-Password is not valid";
                }

            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }
        if (editTextName.getText().toString().matches("")) {
            findViewById(R.id.edit_text_register_name).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
                if(!toastText.contentEquals("")){
                    toastText += "\n-Name cannot be empty";

                }else{
                    toastText += "-Name cannot be empty";
                }
            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }
        if (!Pattern.compile(emailValid).matcher(editTextEmail.getText().toString()).matches()) {
            findViewById(R.id.edit_text_register_email).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
                if(!toastText.contentEquals("")){
                    toastText += "\n-Email is not valid";

                }else{
                    toastText += "-Email is not valid";

                }
            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }

        if(wrongInput){
            toast.setText(toastText);
            toast.show();
            return;
        }


        HashMap<String, String> parameter = new HashMap<String, String>();


        parameter.put("name", editTextName.getText().toString());
        parameter.put("username", editTextUsername.getText().toString());
        parameter.put("email", editTextEmail.getText().toString());
        parameter.put("password", editTextPassword.getText().toString());

        //Server Url
        StoredObject so = new StoredObject(this.getApplicationContext());
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }
        RegisterRequest RegisterRequest = new RegisterRequest(Request.Method.POST,parameter, this, urlHead);
        RegisterRequest.setTag(Config.VolleyID);
        RegisterRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(RegisterRequest);


    }

    @Override
    public void onSuccess(User user) {
//        StoredObject so = new StoredObject(getApplicationContext());
//        so.storeObject(user);
//        Intent i = new Intent(getApplicationContext(),DrawerMainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.putExtra("user_name",user.getName());
//        startActivity(i);

        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        Toast toast = Toast.makeText(getApplicationContext(), "Account successfully registered!\nPlease login!", Toast.LENGTH_LONG);
        toast.show();
        try{
            progressDialog.dismiss();
        }
        catch (Exception e)
        {

        }
    }




    public void onFailure(Config.RequestStatusCode code) {
        try{
            progressDialog.dismiss();
        }
        catch (Exception e)
        {

        }
        CoordinatorLayout snackbarCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.snackbarCoordinatorLayout);
        Snackbar snackbar;
        if(code==Config.RequestStatusCode.AUTH_FAILED){

            new com.aaronsng.wheresitgo.common.CommonOperation().showSnackBar(snackbarCoordinatorLayout,"Authentication Failed");
        }
        if(code==Config.RequestStatusCode.GENERAL_ERROR){


            new com.aaronsng.wheresitgo.common.CommonOperation().showSnackBar(snackbarCoordinatorLayout, "Network Error");
        }
    }

}
