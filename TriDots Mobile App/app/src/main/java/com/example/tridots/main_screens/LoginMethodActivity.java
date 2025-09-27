package com.example.tridots.main_screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tridots.R;

public class LoginMethodActivity extends AppCompatActivity {

    private static final String TAG = "LoginMethodActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_method);

        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginMethodActivity.this, LoginActivity.class));
        });

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginMethodActivity.this, RegisterActivity.class));
        });
    }
}
