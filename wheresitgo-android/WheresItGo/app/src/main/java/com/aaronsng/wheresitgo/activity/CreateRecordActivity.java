package com.aaronsng.wheresitgo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronsng.wheresitgo.R;
import com.aaronsng.wheresitgo.common.CommonOperation;
import com.aaronsng.wheresitgo.common.Config;
import com.aaronsng.wheresitgo.common.RealPathUtil;
import com.aaronsng.wheresitgo.common.StoredObject;
import com.aaronsng.wheresitgo.model.Category;
import com.aaronsng.wheresitgo.model.User;
import com.aaronsng.wheresitgo.volley.CreateRecordRequest;
import com.aaronsng.wheresitgo.volley.VolleySingleton;
import com.android.volley.Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateRecordActivity extends AppCompatActivity implements  CreateRecordRequest.Listener {
    ProgressDialog progressDialog;
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private static final String alphanumeric = "[\\w\\s]*";
    private static String mAudioFileName = "";
    private static final String LOG_TAG = "AudioRecord";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE= 0;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    boolean isRecorded=false;
    String mCurrentPhotoPath="";
    private byte[] multipartBody=null;
    //Recording
    Boolean mStartRecording = true;
    Boolean mStartPlaying = true;
    private Button mRecordButton = null;
    private Button   mPlayButton = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);
        setTitle("Create Record");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog=  new ProgressDialog(CreateRecordActivity.this);
        StoredObject so = new StoredObject(getApplicationContext());
        EditText editTextRecordTitle = (EditText) findViewById(R.id.edit_text_category_title);
        EditText editTextRecordDescription= (EditText) findViewById(R.id.edit_text_category_desc);



        mPlayButton = (Button) findViewById(R.id.button_play_audio);
        mPlayer = new MediaPlayer();
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayButton.setText("Stop playing");
                    mRecordButton.setEnabled(false);
                } else {
                    mPlayButton.setText("Start playing");
                    mRecordButton.setEnabled(true);
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        mRecordButton = (Button) findViewById(R.id.button_record_audio);
        mRecorder = new MediaRecorder();
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecordButton.setText("STOP");
                    mPlayButton.setEnabled(false);
                } else {
                    mRecordButton.setText("RECORD");
                    mPlayButton.setEnabled(true);
                }
                mStartRecording = !mStartRecording;
            }
        });
    }

    public void btnCreateRecord(View abc) {

        User user = new StoredObject(getApplicationContext()).getUserDetails();
        Category category = (Category) getIntent().getSerializableExtra("category");

        boolean wrongInput = false;
        findViewById(R.id.edit_text_record_title).setVisibility(View.INVISIBLE);
        findViewById(R.id.edit_text_record_desc).setVisibility(View.INVISIBLE);
        EditText editTextRecordTitle= (EditText) findViewById(R.id.edit_text_record_title);
        EditText editTextRecordDescription= (EditText) findViewById(R.id.edit_text_record_desc);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        //Validation
        if (editTextRecordTitle.getText().toString().matches("")) {
            findViewById(R.id.edit_text_record_title).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
            }
            catch (Exception e)
            {
            }
            wrongInput = true;
        }

        if (!Pattern.compile(alphanumeric).matcher(editTextRecordTitle.getText().toString()).matches()) {
            findViewById(R.id.edit_text_record_title).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }

        if (!Pattern.compile(alphanumeric).matcher(editTextRecordDescription.getText().toString()).matches()) {
            findViewById(R.id.edit_text_record_desc).setVisibility(View.VISIBLE);
            try{
                progressDialog.dismiss();
            }
            catch (Exception e)
            {
            }
            wrongInput = true;

        }

        if(wrongInput)
            return;
        //Validation End

        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("username", user.getUsername());
        parameter.put("password", user.getPassword());
        parameter.put("user_id", user.getUser_id());
        if(!mCurrentPhotoPath.equalsIgnoreCase("")){
            parameter.put("photo_base64",CommonOperation.getStringImage(mCurrentPhotoPath));
        }
        parameter.put("category_id", category.getCategory_id());
        parameter.put("record_title", editTextRecordTitle.getText().toString().trim());
        parameter.put("record_description", editTextRecordDescription.getText().toString().trim());

        //Audio to multipart
        String   mimeType = "multipart/form-data;boundary=" + boundary;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            try {
                // the first file
                if(!mAudioFileName.equalsIgnoreCase("")) {
                    byte[] fileData1 = convertAudio(mAudioFileName);
                    if(fileData1!=null)
                    {   mimeType =  "multipart/form-data;boundary=" + boundary;
                        buildPart(dos, fileData1);
                    }else
                    {
                        Log.e(Config.log_id,"bytes is null");
                    }

                    Log.e(Config.log_id,"bytes is " + fileData1.length);
                }else
                {
                    Log.e(Config.log_id,"no audio file");

                }

                for (Map.Entry<String, String> entry : parameter.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
                // send multipart form data necesssary after file data
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // pass to multipart body
                multipartBody = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(Config.log_id,e.getMessage());

            }
            //End of Audio to Multipart


        //Server Url
        StoredObject so = new StoredObject(this.getApplicationContext());
        String urlHead;
        if (so.getServerUrl().contentEquals("")){
            urlHead = Config.url;
        }else{
            urlHead = so.getServerUrl();
        }

        CreateRecordRequest recordRequest = new CreateRecordRequest(Request.Method.POST, parameter, this,mimeType,multipartBody, urlHead);
        recordRequest.setTag(Config.VolleyID);
        recordRequest.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(recordRequest);

        try {
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        catch (Exception e)
        {

        }



    }

    private byte[] convertAudio(String filename) {
        int bytesRead;
        try {
            Log.e(Config.log_id,"uri"+filename);
            File file = new File((filename));

            FileInputStream is = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while (( bytesRead = is.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }



            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            Log.e(Config.log_id,e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onSuccess() {
        StoredObject so = new StoredObject(getApplicationContext());
        onBackPressed();
        Toast toast = Toast.makeText(getApplicationContext(),"Record successfully created." , Toast.LENGTH_LONG);
        toast.show();
//        Intent i = new Intent(getApplicationContext(),DrawerMainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( resultCode == RESULT_OK) {

            if(requestCode==REQUEST_IMAGE_CAPTURE)
            {
                Bundle extras = data.getExtras();
                Uri selectedImage = data.getData();
              //  Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView image_view_upload_image = (ImageView) findViewById(R.id.image_view_upload_image);
                TextView text_view_upload_image = (TextView) findViewById(R.id.text_view_upload_image);
                text_view_upload_image.setText("");
               // image_view_upload_image.setImageBitmap(imageBitmap);

                Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath);
                image_view_upload_image.setImageBitmap(bmp);


            }else
            if(requestCode== REQUEST_GALLERY_CAPTURE)
            {



                Uri selectedImage = data.getData();

                ImageView image_view_upload_image = (ImageView) findViewById(R.id.image_view_upload_image);
                TextView text_view_upload_image = (TextView) findViewById(R.id.text_view_upload_image);
                text_view_upload_image.setText("");
                image_view_upload_image.setImageURI(selectedImage);
                if (Build.VERSION.SDK_INT < 11)
                    mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                    // SDK > 19 (Android 4.4)
                else
                    mCurrentPhotoPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());


            }


            Log.e(Config.log_id,mCurrentPhotoPath+" image path");
        }
    }




    public void btnUploadImage(View a)
    {

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakeGalleryIntent();
                                break;
                            case 1:
                                dispatchTakePictureIntent();
                                break;
                        }
                    }
                });
                 pictureDialog.show();


    }

    private void dispatchTakeGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), REQUEST_GALLERY_CAPTURE);

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(Config.log_id,"error creating thje picture "+ex.getMessage());
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aaronsng.wheresitgo.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


            }
        }
    }
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }
    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + "abc" + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    //Recording
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
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
            mPlayer.setDataSource(mAudioFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {

        //Recording
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mAudioFileName = getExternalCacheDir().getAbsolutePath()+"/"+timeStamp+".3gp";
        Log.d("filepath", mAudioFileName);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


}
