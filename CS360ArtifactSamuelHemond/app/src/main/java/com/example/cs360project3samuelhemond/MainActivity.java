package com.example.cs360project3samuelhemond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //logging tag
    private static final String TAG = "MainActivity";
    //database
    WeightRepository weightRepository;
    //user idea to search database
    Long userID;
    //declare views
    TextView goalWeightTextView;
    RecyclerView recyclerView;
    //resources for recycler view
    ArrayList<DailyWeight> weights;
    RecycleAdapter recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get views from xml
        goalWeightTextView = findViewById(R.id.goalWeight);
        recyclerView = findViewById(R.id.weightTable);

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //set user goal weight to text view
        String goalWeight = getString(R.string.goal_weight) + weightRepository.getGoalWeightByUserId(userID).getWeight() + getString(R.string.goal_weight2);
        goalWeightTextView.setText(goalWeight);

        //setup and use methods for the recyclerview
        weights = new ArrayList<>();
        recycleAdapter = new RecycleAdapter(MainActivity.this, weights, index -> {//Delete row from recycle view and update view
            Log.i(TAG, "Delete Weight at date: " + weights.get(index).getDate());
            weightRepository.deleteDailyWeight(weights.get(index));
            refreshDisplayData();
            //changed to remove single item
            recycleAdapter.notifyItemRemoved(index);
        });
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        refreshDisplayData();//refresh lists
    }

    //refresh lists
    private void refreshDisplayData() {
        List<DailyWeight> tmp = weightRepository.getDailyWeightByUserId(userID);
        Log.i(TAG, "List Length: " + tmp.size());
        //clear weights and remake with database
        weights.clear();
        if(tmp.size() > 0) {
            weights.addAll(tmp);
        }
        //Sort list by date
        weights.sort(Comparator.comparing(DailyWeight::getDate).reversed());
        Log.i(TAG, "Weights Length: " + weights.size());
    }

    //called by button to open the input activity
    public void useInputActivity(View view) {
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra("userID", userID);//pass user id
        startActivity(intent);
    }

    //Method to retrieve sms permission. If already given opens phone number activity
    public void getSmsPermission(View view) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.SEND_SMS"}, 1);
        }else{//If already given opens phone number activity
            Toast.makeText(MainActivity.this, "SMS Already Permission Granted", Toast.LENGTH_SHORT) .show();
            Intent intent = new Intent(this, PhoneNumberInputActivity.class);
            intent.putExtra("userID", userID);//pass user id
            startActivity(intent);
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

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //if permission is granted
                Intent intent = new Intent(this, PhoneNumberInputActivity.class);
                intent.putExtra("userID", userID);//pass user id
                startActivity(intent);
            } else {//permission denied program continues to run.
                Toast.makeText(MainActivity.this, "SMS Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }
}