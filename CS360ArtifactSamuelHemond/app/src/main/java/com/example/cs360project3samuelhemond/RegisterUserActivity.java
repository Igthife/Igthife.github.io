package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterUserActivity extends AppCompatActivity {
    //logging tag
    private static final String TAG = "RegisterUser";
    //database
    WeightRepository weightRepository;
    //user id to search database
    Long userID;
    //declare views
    EditText userNameView;
    EditText passwordView;
    EditText phoneNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        userNameView = findViewById(R.id.usernameInputRegister);
        passwordView = findViewById(R.id.passwordInputRegister);
        phoneNumberView = findViewById(R.id.phoneNumberInputRegister);

        phoneNumberView.setFocusable(false);

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);
    }

    //uses login edit texts and creates user if inputs are 1 or longer and account doesn't exist
    public void newUserAttempt(View view){
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        if(weightRepository.getUser(username, password) == null && username.length() > 0 && password.length() > 0){

            initNewUser(username,password);



        }else if(username.length() == 0 || password.length() == 0) {
            Log.i(TAG, "One Or More Input Fields Empty");       //Additional user feedback and logging
            Toast.makeText(RegisterUserActivity.this, "One Or More Input Fields Are Empty", Toast.LENGTH_LONG).show();
        }else{
            Log.i(TAG, "User Already Exists");
            Toast.makeText(RegisterUserActivity.this, "User Already Exists", Toast.LENGTH_LONG).show();
        }
    }

    //Check valid username is entered FIXME enhancement 2
    private boolean validateUsernameFormat(String username){
        return true;
    }
    //Check valid password is entered FIXME enhancement 2
    private boolean validatePasswordFormat(String password){
        return true;
    }
    //Check valid phone number is entered (format only) FIXME enhancement 2
    private boolean validatePhoneNumberFormat(String phoneNumber){
        return true;
    }
    //setup new user and add value to the goal weigh of 0 LBS
    private void initNewUser(String username, String password){
        User newUser;
        Log.i(TAG, "User Does Not Exists Creating New User");
        newUser = new User();
        newUser.setUserName(username);
        newUser.setUserPassword(password);
        newUser.setUserPhoneNumber(null);
        weightRepository.addUser(newUser);
        newUser = weightRepository.getUser(username, password);     //FIXME test
        GoalWeight initGoalWeight = new GoalWeight();
        initGoalWeight.setWeight(0);
        initGoalWeight.setUserId(newUser.getId());
        weightRepository.addGoalWeight(initGoalWeight);
    }

    public void checkPermissions(View view){
        Toast.makeText(RegisterUserActivity.this, "Press works", Toast.LENGTH_LONG).show();
        phoneNumberView.setFocusableInTouchMode(true);
    }
}