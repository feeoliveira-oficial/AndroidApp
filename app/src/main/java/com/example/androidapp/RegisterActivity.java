package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        EditText emailRegisterInput = findViewById(R.id.emailRegisterInput);
        EditText passwordRegisterInput = findViewById(R.id.passwordRegisterInput);
        Button registerUserButton = findViewById(R.id.registerUserButton);

        // Register logic
        registerUserButton.setOnClickListener(v -> {
            String email = emailRegisterInput.getText().toString().trim();
            String password = passwordRegisterInput.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Redirect to login
                            Toast.makeText(RegisterActivity.this, "Successful Register", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
