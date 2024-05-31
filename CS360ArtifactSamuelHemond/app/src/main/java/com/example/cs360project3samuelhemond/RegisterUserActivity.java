package com.example.cs360project3samuelhemond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
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
    //toggle for checking sms permissions
    Boolean smsPermission;
    //declare views
    EditText userNameView;
    EditText passwordView;
    EditText phoneNumberView;
    EditText goalWeightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        //find view from xml
        userNameView = findViewById(R.id.usernameInputRegister);
        passwordView = findViewById(R.id.passwordInputRegister);
        goalWeightView = findViewById(R.id.goalWeightInputRegister);
        phoneNumberView = findViewById(R.id.phoneNumberInputRegister);

        //variables for phone number input, disable input until sms permissions are granted
        if (ContextCompat.checkSelfPermission(RegisterUserActivity.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED) {
            smsPermission = true;
        }else{
            phoneNumberView.setFocusable(false);
            smsPermission = false;
        }

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);
    }

    //uses login edit texts and creates user if inputs are 1 or longer and account doesn't exist
    public void newUserAttempt(View view){
        //Get strings from
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        String weight = goalWeightView.getText().toString();
        String phoneNumber = phoneNumberView.getText().toString();

        if(weightRepository.getUser(username, password) == null && username.length() > 0 && password.length() > 0){       //check if good to create user TODO enhancement 2 change to only check username

            if(!validateGoalWeightFormat(weight)){ //Check for valid weight
                weight = null;
            }

            phoneNumber = phoneNumber.replace("-", "");     //remove - char
            if(!validatePhoneNumberFormat(phoneNumber)){
                phoneNumber = null;
            }

            initNewUser(username,password, weight, phoneNumber);
            Toast.makeText(RegisterUserActivity.this, "User Registered", Toast.LENGTH_LONG).show();
            finish(); //done with activity

        }else if(username.length() == 0 || password.length() == 0) {        //Additional user feedback for incorrect information and logging
            Log.i(TAG, "One Or More Input Fields Empty");
            Toast.makeText(RegisterUserActivity.this, "One Or More Input Fields Are Empty", Toast.LENGTH_SHORT).show();
        }else{
            Log.i(TAG, "User Already Exists");
            Toast.makeText(RegisterUserActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
        }


    }

    //Check valid username is entered TODO enhancement 2
    private boolean validateUsernameFormat(String username){
        return true;
    }
    //Check valid password is entered TODO enhancement 2
    private boolean validatePasswordFormat(String password){
        return true;
    }

    //Check valid goal weight is entered only boundary is greater than 0
    private boolean validateGoalWeightFormat(String goalWeight)
    {
        int value;
        if(goalWeight == null){ //if statement for null protection
            Log.i(TAG, "validateGoalWeightFormat reached with null variable");
            Toast.makeText(RegisterUserActivity.this, "Unexpected Value In Weight", Toast.LENGTH_SHORT).show();
            return false;
        }else if(goalWeight.length() < 1){      //If statement taken of left blank
            return false;
        }else if(goalWeight.chars().allMatch(Character::isDigit)){
            try {
                value = (Integer.parseInt(goalWeight));
            } catch (NumberFormatException e) {
                Log.i(TAG, "Input Exception");
                Toast.makeText(RegisterUserActivity.this, "Error In Weight Please Retry", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(value >= 0){
                return true;
            }else{      //if goal weight is negative false (unlikely using number input)
                Toast.makeText(RegisterUserActivity.this, "Error Weight Cannot Be Less Than 0 LBS", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(RegisterUserActivity.this, "Invalid Weight", Toast.LENGTH_SHORT).show();
        return false;
    }
    //Check valid phone number is entered valid in this case is numeric and 10-12 characters
    private boolean validatePhoneNumberFormat(String phoneNumber){
        String regexStr = "[0-9]{10,12}$";
        return phoneNumber.matches(regexStr);
    }

    //setup new user and add value to the goal weigh of 0 LBS
    private void initNewUser(String username, String password, String goalWeight, String phoneNumber){
        User newUser;
        Log.i(TAG, "User Does Not Exists Creating New User");
        newUser = new User();       //setup user
        newUser.setUserName(username);
        newUser.setUserPassword(password);
        newUser.setUserPhoneNumber(phoneNumber);        //null or valid number
        weightRepository.addUser(newUser);      //add user to database
        newUser = weightRepository.getUser(username, password);     //now get user back from database to get assigned id
        GoalWeight initGoalWeight = new GoalWeight();       //setup goal weight
        if(goalWeight == null) {        //if goal weight null set 0 else set to weight value
            initGoalWeight.setWeight(0);
        }else{
            try {
                initGoalWeight.setWeight(Integer.parseInt(goalWeight));
            } catch (NumberFormatException e) {
                Log.i(TAG, "Input Exception");
                Toast.makeText(RegisterUserActivity.this, "Error In Weight Please Retry", Toast.LENGTH_SHORT).show();
            }
        }
        initGoalWeight.setUserId(newUser.getId());      //get user id from above and use as the foreign key for goal weight
        weightRepository.addGoalWeight(initGoalWeight);     //add goal weight to database
    }

    //method called when the phoneNumberView is pressed and checks for phone permissions
    //and dynamically enables the edit text
    public void checkPermissions(View view){

        if (smsPermission || ContextCompat.checkSelfPermission(RegisterUserActivity.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(RegisterUserActivity.this, new String[]{"android.permission.SEND_SMS"}, 1);     //request code 1
        }else{//If already given opens phone number activity
            phoneNumberView.setFocusableInTouchMode(true);
            smsPermission = true;
        }
    }

    //Method called after user is prompted for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == 1) {     //request code 1
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission is granted
                phoneNumberView.setFocusableInTouchMode(true);
                smsPermission = true;
            } else {//permission denied program continues to run.
                Toast.makeText(RegisterUserActivity.this, "SMS Permission Denied", Toast.LENGTH_SHORT) .show();
                phoneNumberView.setEnabled(false);      //disable editText if permissions denied
            }
        }

    }
}