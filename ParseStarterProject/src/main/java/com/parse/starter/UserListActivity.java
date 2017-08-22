package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    ArrayList<String>  usernames ;
    ListView userListView ;
    ArrayAdapter<String> arrayAdapter ;


    public void sharePhotos(){
        Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent , 1);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.share) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 1);
                } else {
                    sharePhotos();
                }
            } else {
                sharePhotos();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , selectedImage);
                Log.i("photo" , "recieved");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                // Create the ParseFile

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("picturePath", byteArray);

                // Create a New Class called "ImageUpload" in Parse
                ParseObject imgupload = new ParseObject("Image");

                // Create a column named "ImageName" and set the string
                imgupload.put("Image", file);


                // Create a column named "ImageFile" and insert the image
                imgupload.put("username", ParseUser.getCurrentUser().getUsername());

                // Create the class and the columns
                imgupload.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(UserListActivity.this, "Image shared", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserListActivity.this, "Image not shared . please try later on", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        usernames = new ArrayList<>();
        userListView = (ListView)findViewById(R.id.userListView);
        arrayAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_dropdown_item_1line , usernames);


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username" , ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    if(objects.size() > 0){
                        for(ParseUser user : objects ){
                            usernames.add(user.getUsername());
                        }
                        userListView.setAdapter(arrayAdapter);
                    }else{
                        e.printStackTrace();
                    }
                }
            }
        });
    }




}
