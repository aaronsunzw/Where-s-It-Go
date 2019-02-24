package com.aaronsng.wheresitgo.activity;

/**
 * Created by aaron on 16-Mar-17.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.CommonOperation;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.fragment.CategoryHomeFragment;
import com.aaronsng.wheresitgo.fragment.CategoryRecordsFragment;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.User;

public class DrawerMainActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, CategoryHomeFragment.OnFragmentInteractionListener, CategoryRecordsFragment.OnFragmentInteractionListener
{
    CoordinatorLayout snackbarCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView textViewName = (TextView) header.findViewById(R.id.textView_drawer_name);
        TextView textViewEmail = (TextView) header.findViewById(R.id.textView_drawer_email);
        User user = new StoredObject(getApplicationContext()).getUserDetails();
        textViewName.setText(user.getName());
        textViewEmail.setText(user.getEmail());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = (Fragment) new CategoryHomeFragment();
            fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).addToBackStack(null).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        snackbarCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.snackbarCoordinatorLayout);
        String user_name = getIntent().getStringExtra("user_name");

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.drawer_main, menu);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_my_categories) {
            fragmentClass = CategoryHomeFragment.class;
        } else if (id == R.id.nav_create_category) {
            //fragmentClass = CreateCategoryActivity.class;
            Intent intent = new Intent(this, CreateCategoryActivity.class);
            StoredObject so = new StoredObject(this.getApplicationContext());
            intent.putExtra("user_id", so.getUserDetails().getUser_id());
            startActivity(intent);
        }else if(id == R.id.nav_search_records){
            Intent intent = new Intent(this, SearchAllRecordsActivity.class);
            StoredObject so = new StoredObject(this.getApplicationContext());
            intent.putExtra("user_id", so.getUserDetails().getUser_id());
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            StoredObject so = new StoredObject(getApplicationContext());
            so.clearStoredObject();
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        drawer.closeDrawer(GravityCompat.START);

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.drawer_content, fragment).commit();
            fragmentManager.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) { //replace this with actual function which returns if the drawer is open
            mDrawerLayout.closeDrawer(GravityCompat.START);     // replace this with actual function which closes drawer
        } else if(getSupportFragmentManager().getBackStackEntryCount() > 1)
            onBackPressed();
            else
                moveTaskToBack(true);
        return true;
    }

//    @Override
//    public void onRefreshHome() {
//        try {
//            Class fragmentClass = CategoryHomeFragment.class;
//            Fragment fragment = null;
//            fragment = (Fragment) fragmentClass.newInstance();
//
//            // Insert the fragment by replacing any existing fragment
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void imageButtonAddCategory(View v){
        Intent intent = new Intent(this, CreateCategoryActivity.class);
        StoredObject so = new StoredObject(this.getApplicationContext());
        intent.putExtra("user_id", so.getUserDetails().getUser_id());
        startActivity(intent);
    }



    @Override
    public void onSnackBarEvent(String text) {
        new CommonOperation().showSnackBar(snackbarCoordinatorLayout,text);
    }

    @Override
    public void onRefreshCategory() {
        try {
            Class fragmentClass = CategoryHomeFragment.class;
            Fragment fragment = null;

            fragment = (Fragment) fragmentClass.newInstance();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefreshRecord() {
        try {
            Class fragmentClass = CategoryRecordsFragment.class;
            Fragment fragment = null;

            fragment = (Fragment) fragmentClass.newInstance();

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {

    }

//    @Override
//    public void onSnackBarEvent(String text) {
//        new CommonOperation().showSnackBar(snackbarCoordinatorLayout,text);
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
