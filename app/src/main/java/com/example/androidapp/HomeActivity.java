package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView userEmailText = findViewById(R.id.userEmailText);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button goToTasksButton = findViewById(R.id.goToTasksButton);

        String userEmail = getIntent().getStringExtra("userEmail");
        userEmailText.setText("Welcome, " + userEmail + "!");

        goToTasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
