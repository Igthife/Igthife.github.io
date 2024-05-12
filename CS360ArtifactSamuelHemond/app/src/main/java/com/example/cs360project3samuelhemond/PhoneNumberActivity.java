package com.example.cs360project3samuelhemond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberActivity extends Activity {
    private static final String TAG = "PhoneNumberActivity";

    WeightRepository weightRepository;
    Long userID;//user idea to search database
    //setup views
    EditText userPhoneNumber;
    TextView currentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_input);
        //setup views
        userPhoneNumber = findViewById(R.id.numberInput);
        currentPhoneNumber = findViewById(R.id.currentNumber);

        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);

        //display current phone number if present
        if(weightRepository.getUserById(userID).getUserPhoneNumber() == null){
            currentPhoneNumber.setText(getString(R.string.current_phone_number) + "Empty");
        }else{
            currentPhoneNumber.setText(getString(R.string.current_phone_number) + weightRepository.getUserById(userID).getUserPhoneNumber());
        }

    }

    //save phone number
    public void savePhoneNumber(View view) {
        String userInput = userPhoneNumber.getText().toString();
        if(userInput != null && userInput.length() >= 10){ //if phone number least 10 characters long save else notifiy user
            User user = weightRepository.getUserById(userID);
            user.setUserPhoneNumber(userInput);
            weightRepository.updateUser(user);
            Log.i(TAG, "Phone number updated to:" + userInput);
            finish(); //done with activity
        }else{
            Log.i(TAG, "Phone number invalid");
            Toast.makeText(PhoneNumberActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT) .show();
        }
    }
}
