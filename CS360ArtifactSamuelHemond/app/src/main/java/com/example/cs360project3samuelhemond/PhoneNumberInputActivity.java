package com.example.cs360project3samuelhemond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberInputActivity extends Activity {
    private static final String TAG = "PhoneNumberActivity";

    WeightRepository weightRepository;
    Long userID;//user idea to search database
    //setup views
    EditText userPhoneNumber;
    TextView currentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_input);
        //setup views
        userPhoneNumber = findViewById(R.id.numberInput);
        currentPhoneNumber = findViewById(R.id.currentNumber);

        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);

        //display current phone number if present
        String message;
        if(weightRepository.getUserById(userID).getUserPhoneNumber() == null){
            message = getString(R.string.current_phone_number) + "Empty";
        }else{
            message = getString(R.string.current_phone_number) + weightRepository.getUserById(userID).getUserPhoneNumber();
        }
        currentPhoneNumber.setText(message);

    }

    //save phone number
    public void savePhoneNumber(View view) {
        String phoneNumber = userPhoneNumber.getText().toString();
        phoneNumber = phoneNumber.replace("-", "");     //remove - char
        if( validatePhoneNumberFormat(phoneNumber)){ //check if phone number is valid
            User user = weightRepository.getUserById(userID);
            user.setUserPhoneNumber(phoneNumber);
            weightRepository.updateUser(user);
            Log.i(TAG, "Phone number updated to:" + phoneNumber);
            finish(); //done with activity
        }else{
            Log.i(TAG, "Phone number invalid");
            Toast.makeText(PhoneNumberInputActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT) .show();
        }
    }

    //Check valid phone number is entered valid in this case is numeric and 10-12 characters
    private boolean validatePhoneNumberFormat(String phoneNumber){
        String regexStr = "[0-9]{10,12}$";
        return phoneNumber.matches(regexStr);
    }
}
