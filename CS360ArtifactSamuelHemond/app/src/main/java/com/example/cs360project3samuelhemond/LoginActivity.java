package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    WeightRepository weightRepository;//database
    //declare views
    EditText userNameView;
    EditText passwordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //setup views
        userNameView = findViewById(R.id.usernameInput);
        passwordView = findViewById(R.id.passwordInput);
        weightRepository = WeightRepository.getInstance(this);
    }

    //attempt to login. Limited parsing of data and not hashed password for this sized project
    public void loginAttempt(View view) {
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();

        User foundUser = weightRepository.getUser(username, password);

        if(foundUser != null){ //user exists
            Log.i(TAG, "User Exists");
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userID", foundUser.getId());
            startActivity(intent);
        }else{  //user does not exist
            Log.i(TAG, "User Does Not Exists");
            Toast.makeText(LoginActivity.this, "Username or Password Incorrect", Toast.LENGTH_LONG).show();
        }

    }

    //uses login edit texts and creates user if inputs are 1 or longer and account doesn't exist
    public void newUserAttempt(View view){
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        if(weightRepository.getUser(username, password) == null && username.length() > 0 && password.length() > 0){

            initNewUser(username,password);


        }else if(username.length() == 0 || password.length() == 0) {
            Log.i(TAG, "One Or More Input Fields Empty");
            Toast.makeText(LoginActivity.this, "One Or More Input Fields Empty", Toast.LENGTH_LONG).show();
        }else{
            Log.i(TAG, "User Already Exists");
            Toast.makeText(LoginActivity.this, "User Already Exists", Toast.LENGTH_LONG).show();
        }
    }

    //setup new user and add value to the goal weigh of 0 LBS
    public void initNewUser(String username, String password){
        User newUser;
        Log.i(TAG, "User Does Not Exists Creating New User");
        newUser = new User();
        newUser.setUserName(username);
        newUser.setUserPassword(password);
        newUser.setUserPhoneNumber(null);
        weightRepository.addUser(newUser);
        newUser = weightRepository.getUserByName(username);
        GoalWeight initGoalWeight = new GoalWeight();
        initGoalWeight.setWeight(0);
        initGoalWeight.setUserId(newUser.getId());
        weightRepository.addGoalWeight(initGoalWeight);
    }
}