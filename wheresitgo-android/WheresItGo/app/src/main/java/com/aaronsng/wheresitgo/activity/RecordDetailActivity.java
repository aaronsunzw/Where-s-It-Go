package com.aaronsng.wheresitgo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.CommonOperation;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.Record;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.CreateRecordRequest;
import com.aaronsng.wheresitgo.volley.DeleteRecordRequest;
import com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class RecordDetailActivity extends AppCompatActivity implements DeleteRecordRequest.Listener{
    String LOG_AUDIO = "LOG_AUDIO";
    String serverUrl;
    Record targetRecord;
    MediaPlayer mPlayer = new MediaPlayer();
    Boolean mStartPlaying = true;
    CoordinatorLayout snackbarCoordinatorLayout;
    private Button mPlayButton = null;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        targetRecord = (Record) getIntent().getSerializableExtra("targetRecord");
        TextView textViewRecordTitle = (TextView) findViewById(R.id.text_view_display_record_title);
        TextView textViewRecordDesc = (TextView) findViewById(R.id.text_view_display_record_desc);
        ImageView image_view_uploaded_record = (ImageView) findViewById(R.id.image_view_uploaded_image) ;
        TextView textViewNoImage = (TextView) findViewById(R.id.text_view_no_image_desc);
        textViewRecordTitle.setText(targetRecord.getRecord_title());
        if (!targetRecord.getRecord_description().contentEquals("null")){
            textViewRecordDesc.setText(targetRecord.getRecord_description());
        }else{
            textViewRecordDesc.setText("No description");
        }
        mPlayButton = (Button) findViewById(R.id.button_play_uploaded_audio);
        snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayoutRecordDetail);
        progressDialog = new ProgressDialog(RecordDetailActivity.this);

        StoredObject so = new StoredObject(getApplicationContext());
        if (so.getServerUrl().contentEquals("")){
            serverUrl = Config.url;
        }else{
            serverUrl = so.getServerUrl();
        }

        if(targetRecord.getRecord_image()!= null){
            textViewNoImage.setVisibility(View.GONE);
            Picasso.with(getApplicationContext())
                    .load(serverUrl+"public/image/user_photos/"+targetRecord.getRecord_image())
//                    .load(Config.url+"public/image/user_photos/"+targetRecord.getRecord_image())
                    .error(R.drawable.ic_menu_gallery)
//                .resize(50, 50)
//                .centerCrop()
                    .into(image_view_uploaded_record);
        }

    }


    public void btnPlayAudio(View view){
        try {
            if(!targetRecord.getRecord_audio().toString().equalsIgnoreCase("null")){
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayButton.setText("STOP");
                } else {
                    mPlayButton.setText("PLAY");
                }
                mStartPlaying = !mStartPlaying;
            }else{
//                Toast toast = Toast.makeText(getApplicationContext(),"No audio found for this record." , Toast.LENGTH_SHORT);
//                toast.show();
                new CommonOperation().showSnackBar(snackbarCoordinatorLayout,"No audio found for this record.");
            }


        }catch (Exception e){
            Log.e(LOG_AUDIO, e.getLocalizedMessage());
            Log.e(LOG_AUDIO, e.getMessage());
        }

    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(serverUrl+"public/audio/user_audio/"+targetRecord.getRecord_audio());
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayAudio(findViewById(R.id.button_play_uploaded_audio));
                }
            });
        } catch (IOException e) {
            Log.e("PLAY_AUDIO:", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void btnDeleteRecord(View view){
        progressDialog.setMessage("Deleting record...");
        progressDialog.setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Record");
        builder.setMessage("Are you sure you want to delete this record? \nDeleting this record means that all audio and images associated will be lost.\n Press OK to continue.");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                progressDialog.setCancelable(false);
                progressDialog.show();
                dialog.dismiss();
                deleteRecord();

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

    public void deleteRecord(){
        User user = new StoredObject(getApplicationContext()).getUserDetails();
        String category_id = targetRecord.getCategory_id().toString();
        String record_id = targetRecord.getRecord_id().toString();

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("username", user.getUsername());
        parameter.put("password", user.getPassword());
        parameter.put("category_id", category_id);
        parameter.put("record_id", record_id);

        //Server Url
        StoredObject so = new StoredObject(this.getApplicationContext());
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        DeleteRecordRequest deleteRecordRequest = new DeleteRecordRequest(Request.Method.POST, parameter, this, urlHead);
        deleteRecordRequest.setTag(Config.VolleyID);
        deleteRecordRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(deleteRecordRequest);

    }

    @Override
    public void onSuccess() {
        StoredObject so = new StoredObject(getApplicationContext());
        onBackPressed();
        Toast toast = Toast.makeText(getApplicationContext(),"Record successfully deleted." , Toast.LENGTH_LONG);
        toast.show();

//        Intent i = new Intent(getApplicationContext(),DrawerMainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);

        try{
            progressDialog.dismiss();
        }
        catch (Exception e) {

        }


    }

    @Override
    public void onFailure(Config.RequestStatusCode code) {
        try{
            progressDialog.dismiss();
        }
        catch (Exception e) {

        }
        if(code==Config.RequestStatusCode.AUTH_FAILED){

            new CommonOperation().showSnackBar(snackbarCoordinatorLayout,"Authentication Failed");
        }
        if(code==Config.RequestStatusCode.GENERAL_ERROR){


            new CommonOperation().showSnackBar(snackbarCoordinatorLayout, "Network Error");
        }
    }
}
