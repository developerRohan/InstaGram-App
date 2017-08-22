/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  TextView changeSignUpModeTextView;
  EditText usernameEditText;
  EditText passwordEditText;
  Boolean signupActiveMode = true;


  public void showUserList() {
    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
    startActivity(intent);
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    changeSignUpModeTextView = (TextView) findViewById(R.id.changeSignUpModeTextView);
    changeSignUpModeTextView.setOnClickListener(this);
//    passwordEditText.setOnKeyListener(this);
    RelativeLayout relativeLayoutView = (RelativeLayout) findViewById(R.id.relativeLayoutVIew);
    relativeLayoutView.setOnClickListener(this);

    if (ParseUser.getCurrentUser() != null) {
      showUserList();
    }
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }





  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.changeSignUpModeTextView) {
//      Log.i("info" , "button clicked");
      if (signupActiveMode) {
        Button signupButton = (Button) findViewById(R.id.signupButton);
        signupActiveMode = false;
        signupButton.setText("LOG IN");
        changeSignUpModeTextView.setText("or, SIGNUP");

      } else {
        Button signupButton = (Button) findViewById(R.id.signupButton);
        signupActiveMode = true;
        signupButton.setText("SIGNUP");
        changeSignUpModeTextView.setText("or, LOGIN");

      }
    } else if (view.getId() == R.id.relativeLayoutVIew) {
      InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
        mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
      }
    }
  }



  public void signUp(View view) {
    if (usernameEditText.getText().toString().matches("") && passwordEditText.getText().toString().matches("")) {
      Toast.makeText(this, "username and password are required", Toast.LENGTH_SHORT).show();
    } else {
      if (signupActiveMode) {
        ParseUser user = new ParseUser();
        user.setUsername(usernameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Toast.makeText(MainActivity.this, "sign up successsful", Toast.LENGTH_SHORT).show();
              showUserList();
//              Log.i("sign up" , "successsful");
            } else {
              Toast.makeText(MainActivity.this, "sign up error: " + e, Toast.LENGTH_SHORT).show();
//              Log.d("sign up" , "error:" + " "+ e);
            }
          }
        });
      } else {
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (e == null) {
//              Log.i("log in" , "successsful");
              showUserList();
              Toast.makeText(MainActivity.this, "log in successsful", Toast.LENGTH_SHORT).show();
            } else {
//              Log.d("log in" , "error:" + " "+ e);
              Toast.makeText(MainActivity.this, "log in error: " + e, Toast.LENGTH_SHORT).show();

            }
          }
        });
      }


    }
  }
}




