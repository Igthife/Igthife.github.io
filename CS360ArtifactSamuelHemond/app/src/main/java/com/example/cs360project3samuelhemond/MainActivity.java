package com.example.cs360project3samuelhemond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    //resources for drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get views from xml
        goalWeightTextView = findViewById(R.id.goalWeight);
        recyclerView = findViewById(R.id.weightTable);
        drawerLayout = findViewById(R.id.mainDrawerLayout);
        navigationView = findViewById(R.id.navigationView);

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);

        //retrieve user ID from intent for uses in program
        Intent intent = getIntent();
        userID = intent.getLongExtra("userID", 0);

        //setup setting drawer and set listener
        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Called when activity resumes or starts
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

    //checks for item in drawer pressed and returns boolean
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    //called by button to open the dailyWeightActivity
    public void openDailyWeightActivity(View view) {
        Intent intent = new Intent(this, dailyWeightActivity.class);
        intent.putExtra("userID", userID);//pass user id
        startActivity(intent);
    }

    //called by button to open the goalWeightActivity
    public void openGoalWeightActivity(View view) {
        Intent intent = new Intent(this, goalWeightActivity.class);
        intent.putExtra("userID", userID);//pass user id
        startActivity(intent);
    }

    //Method to open setting drawer from a button rather than action bar
    public void openSettings(View view) {
        drawerLayout.openDrawer(GravityCompat.END);
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
                //inform user of success
                Toast.makeText(MainActivity.this, "SMS Permission Granted", Toast.LENGTH_SHORT) .show();
            } else {//if permission denied open up app settings in the android settings
                Toast.makeText(MainActivity.this, "SMS Permission Denied", Toast.LENGTH_SHORT) .show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

    }

    //method called when when item in drawer selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //handle id checking which item is selected
        if(item.getItemId() == R.id.smsPermissionsItem){
            //if permission are needed request from user else inform user already granted
            if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.SEND_SMS") == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.SEND_SMS"}, 1);
            }else{//If already given opens phone number activity
                Toast.makeText(MainActivity.this, "SMS Already Permission Granted", Toast.LENGTH_SHORT) .show();
            }

        }else if(item.getItemId() == R.id.phoneNumberItem){
            //if permissions present open phone number activity else don't
            if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.SEND_SMS") != PackageManager.PERMISSION_DENIED) {
                Intent intent = new Intent(this, PhoneNumberInputActivity.class);
                intent.putExtra("userID", userID);//pass user id
                startActivity(intent);
        }else{//If no permission inform user
            Toast.makeText(MainActivity.this, "SMS Permission Missing", Toast.LENGTH_SHORT) .show();
        }
        }
        //close drawer once item selected
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}