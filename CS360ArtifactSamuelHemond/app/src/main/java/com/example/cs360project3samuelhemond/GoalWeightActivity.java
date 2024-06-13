package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;


public class GoalWeightActivity extends AppCompatActivity {
    //logging tag
    private static final String TAG = "GoalWeightActivity";
    //database
    WeightRepository weightRepository;
    //user idea to search database
    Long userID;
    //views declaration
    EditText goalWeightInput;
    TextView goalWeightTextView;

    //setup variables for firebase database
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_weight);
        //get views
        goalWeightInput = findViewById(R.id.goalWeightInput);
        goalWeightTextView = findViewById(R.id.currentGoalWeight);

        //get singleton
        weightRepository = WeightRepository.getInstance(this);
        //get user id
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);

        //get firebase database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(userID.toString());

        //updates current goal weight textView from
        // firebase if online and by local database if offline
        if(isNetworkAvailable(this)){
            getData();
        }else{
            String goalWeight = getString(R.string.goal_weight) +
                    weightRepository.getGoalWeightByUserId(userID).getWeight() +
                    getString(R.string.goal_weight2);

            goalWeightTextView.setText(goalWeight);
        }

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
            Log.i(TAG, "Set goal weight:" + goalWeight.getWeight() + " to userID: " + userID.toString());
            //Set goal weight value in firebase database
            myRef.setValue(goalWeight.getWeight());

            weightRepository.updateGoalWeight(goalWeight);      //update local database
            finish();       //done with activity

        }else{//creates toast for user and log for invalid input
            Log.i(TAG, "Invalid Input");
            Toast.makeText(GoalWeightActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
        }
    }

    //Method for getting goal weight from firebase database
    private void getData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Long class and use it to set current goal weight textView
                Long goalWeightFirebase = dataSnapshot.getValue(Long.class);

                //replace null with 0
                if(goalWeightFirebase == null){
                    goalWeightFirebase = 0L;
                    myRef.setValue(0);
                }
                //set textview text
                String goalWeight = getString(R.string.current_goal_weight) + goalWeightFirebase + getString(R.string.goal_weight2);
                goalWeightTextView.setText(goalWeight);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadLong:onCancelled", databaseError.toException());

                String goalWeight = getString(R.string.goal_weight) +
                        weightRepository.getGoalWeightByUserId(userID).getWeight() +
                        getString(R.string.goal_weight2);

                goalWeightTextView.setText(goalWeight);
            }
        });
    }

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

