package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class goalWeightActivity extends AppCompatActivity {
    //logging tag
    private static final String TAG = "InputActivity";
    //database
    WeightRepository weightRepository;
    //user idea to search database
    Long userID;
    //views declaration
    EditText goalWeightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_weight);
        //get views
        goalWeightInput = findViewById(R.id.goalWeightInput);

        //get singleton
        weightRepository = WeightRepository.getInstance(this);
        //get user id
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);
    }
    //method called by the submit goal weight button
    public void submitGoalWeight(View view) {
        String input = goalWeightInput.getText().toString();
        GoalWeight goalWeight;

        //check for no input and double check input is numeric
        if(input.length()>0 && input.chars().allMatch(Character::isDigit)){

            goalWeight = weightRepository.getGoalWeightByUserId(userID);//get goal weight and update it
            try {
                goalWeight.setWeight(Integer.parseInt(input));
            }catch (NumberFormatException e) {
                Log.i(TAG, "Input Exception");
            }
            weightRepository.updateGoalWeight(goalWeight);
            finish(); //done with activity

        }else{//creates toast for user and log for invalid input
            Log.i(TAG, "Invalid Input");
            Toast.makeText(goalWeightActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
        }
    }
}