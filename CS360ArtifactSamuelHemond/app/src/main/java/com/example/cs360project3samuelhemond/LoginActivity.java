package com.example.cs360project3samuelhemond;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    WeightRepository weightRepository;//database
    //declare views
    EditText userNameView;
    EditText passwordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setup views
        userNameView = findViewById(R.id.usernameInputLogin);
        passwordView = findViewById(R.id.passwordInputLogin);

        //get singleton for database
        weightRepository = WeightRepository.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //set text blank on refocus
        userNameView.setText("");
        passwordView.setText("");
    }

    //attempt to login. Limited parsing of data and not hashed password for this sized project
    public void loginAttempt(View view) {
        String username = userNameView.getText().toString();
        String password = passwordView.getText().toString();

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