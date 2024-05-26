package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class dailyWeightActivity extends AppCompatActivity {
    //logging tag
    private static final String TAG = "InputActivity";
    //database
    WeightRepository weightRepository;
    //user idea to search database
    Long userID;
    LocalDate localDate;
    //views declaration
    EditText goalWeightInput;
    EditText weightInput;
    TextView dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_weight);
        //get views
        goalWeightInput = findViewById(R.id.goalWeightInput);
        dateInput = findViewById(R.id.dateInput);
        weightInput = findViewById(R.id.weightInput);

        //get singleton
        weightRepository = WeightRepository.getInstance(this);
        //get user id
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);
    }



    //submits information from ui to logic creates new daily weight or updates daily weight as needed
    public void submitDailyWeight(View view) {
        String input = weightInput.getText().toString();

        if(input.length() > 0 && input.chars().allMatch(Character::isDigit) && localDate != null){
            DailyWeight dailyWeight;
            dailyWeight = weightRepository.getDailyWeightByUserIDAndDate(userID, localDate);
            if(dailyWeight == null) {//Does Not exist so create new daily weight and add to database
                dailyWeight = new DailyWeight();
                try {
                    dailyWeight.setWeight(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    Log.i(TAG, "Input Exception");
                }
                dailyWeight.setDate(localDate);
                dailyWeight.setUserId(userID);
                weightRepository.addDailyWeight(dailyWeight);
                Log.i(TAG, "New Daily Weight Added");
            }else{ //Exists modify daily weight at existing date
                try {
                    dailyWeight.setWeight(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    Log.i(TAG, "Input Exception");
                }
                weightRepository.updateDailyWeight(dailyWeight);
                Log.i(TAG, "Daily Weight Updated");
            }
            //Send Text message if most recent input weight equals goal weight and permissions exist
            if(ContextCompat.checkSelfPermission(dailyWeightActivity.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED &&
                    dailyWeight.getWeight() == weightRepository.getGoalWeightById(userID).getWeight()&& weightRepository.getUserById(userID).getUserPhoneNumber() != null){
                Toast.makeText(this, "Goal Weight Hit! Sending Text Message.",Toast.LENGTH_LONG).show();
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(weightRepository.getUserById(userID).getUserPhoneNumber(),null,"Congratulation on hitting you goal weight!",null,null);
            }
            finish(); //done with activity
        }else if(localDate == null){    //specifies reason for failure
            Log.i(TAG, "Date Empty");
            Toast.makeText(this, "Date Empty", Toast.LENGTH_LONG).show();
        }else if(input.chars().allMatch(Character::isDigit)){
            Log.i(TAG, "Weight Wrong");
            Toast.makeText(this, "Weight Wrong" + Integer.parseInt(input), Toast.LENGTH_LONG).show();
        }else{
            Log.i(TAG, "Weight Empty");
            Toast.makeText(this, "Weight Empty", Toast.LENGTH_LONG).show();
        }

    }

    //method to open a data picker and retrieve output
    //code modified from https://www.geeksforgeeks.org/datepicker-in-android/ and https://developer.android.com/develop/ui/views/components/pickers
    public void datePicker(View view) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                dailyWeightActivity.this,
                (view1, year1, monthOfYear, dayOfMonth) -> {
                    //get localDate from calender picker and set the text view to the input date
                    localDate = LocalDate.of(year1, monthOfYear + 1,dayOfMonth);
                    dateInput.setText(localDate.format(DateTimeFormatter.ISO_DATE));
                    Log.i(TAG, "Date Picker: " + localDate.format(DateTimeFormatter.ISO_DATE));
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();

    }


}