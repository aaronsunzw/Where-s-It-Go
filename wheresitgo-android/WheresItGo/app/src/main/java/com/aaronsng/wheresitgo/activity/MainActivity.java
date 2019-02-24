package com.aaronsng.wheresitgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import java.util.HashMap;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.LoginRequest;
import com.aaronsng.wheresitgo.volley.VolleySingleton;

public class MainActivity extends AppCompatActivity implements LoginRequest.LoginListener{

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StoredObject so = new StoredObject(getApplicationContext());
        User user = so.getUserDetails();
        //so.storeServerUrl("http://192.168.1.69:8080/wheres-it-go/");
        EditText passwordET = (EditText) findViewById(R.id.edit_text_password);
        passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordET.setTransformationMethod(new PasswordTransformationMethod());
        progressDialog=  new ProgressDialog(MainActivity.this);

        //Logged in
        if(so.getUserDetails()!=null)
        {
            onSuccess(user);
        }

    }

    public void btnLogin(View abc)
    {
        try{
            progressDialog.setMessage("Logging in... Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        catch (Exception e)
        {
        }
        EditText editTextUsername= (EditText) findViewById(R.id.edit_text_username);
        EditText editTextLoginPassword= (EditText) findViewById(R.id.edit_text_password);
        EditText editTextServerUrl = (EditText) findViewById(R.id.edit_text_server_url);
        StoredObject so = new StoredObject(getApplicationContext());
        //URL Check
        if(!editTextServerUrl.getText().toString().contentEquals("")){
            so.storeServerUrl("http://"+editTextServerUrl.getText().toString()+"/wheres-it-go/");
        }else{
            progressDialog.dismiss();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Server url cannot be empty", Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("username", editTextUsername.getText().toString());
        parameter.put("password", editTextLoginPassword.getText().toString());


        //Server Url

        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }
        LoginRequest loginRequest = new LoginRequest(Request.Method.POST,parameter, this, urlHead);
        loginRequest.setTag(Config.VolleyID);
        loginRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(loginRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
     protected void onStop () {
        super.onStop();

        if (VolleySingleton.getInstance(this) != null) {

        }
    }

    @Override
    public void onSuccess(User user) {
        try{
            progressDialog.dismiss();
        }
            catch (Exception e) {
        }

        StoredObject so = new StoredObject(getApplicationContext());
        so.storeObject(user);
        Intent i = new Intent(getApplicationContext()	,DrawerMainActivity.class);
        i.putExtra("username",so.getUserDetails().getUsername());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
      //  new CommonOperation().showToast(MainActivity.this,u.getName());
    }

    @Override
    public void onFailure(Config.RequestStatusCode code) {      try{
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
    public void btnRegister(View abc)
    {
        EditText editTextServerUrl = (EditText) findViewById(R.id.edit_text_server_url);
        StoredObject so = new StoredObject(getApplicationContext());
        //URL Check
        if(!editTextServerUrl.getText().toString().contentEquals("")){
            so.storeServerUrl("http://"+editTextServerUrl.getText().toString()+"/wheres-it-go/");
        }else{
            progressDialog.dismiss();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Please enter server url to register", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

}
