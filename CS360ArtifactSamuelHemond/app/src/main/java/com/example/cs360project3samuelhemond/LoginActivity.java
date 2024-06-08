package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    WeightRepository weightRepository;//database
    //declare views
    EditText userNameView;
    EditText passwordView;
    //get encryption class
    EncryptionAlgorithm encryptionAlgorithm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setup views
        userNameView = findViewById(R.id.usernameInputLogin);
        passwordView = findViewById(R.id.passwordInputLogin);



        //set encryption class
        encryptionAlgorithm = new EncryptionAlgorithm();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set text blank on refocus
        userNameView.setText("");
        passwordView.setText("");

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);
    }

    //attempt to login. Limited parsing of data and not hashed password for this sized project
    public void loginAttempt(View view) {
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();


        username = username.trim();
        password = password.trim();
        password = encryptionAlgorithm.hashSHA256(password);

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



    //method to open the register user activity
    public void openRegisterActivity(View view){

        Intent intent = new Intent(this, RegisterUserActivity.class);
        startActivity(intent);      //send intent without user info
    }

}