package com.yanir.supersmartalpha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        in = getIntent();

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(signup.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                signUpUser(email, password);
            }
        });

    }

    private boolean signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(signup.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(signup.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(signup.this, "User creation failed", Toast.LENGTH_SHORT).show();
                        // log the error
                        Log.e("signupLog", "User creation failed", task.getException());
                    }
                });
        return false;
    }


    /**
     * This function presents the options menu for moving between activities.
     *
     * @param menu The options menu in which you place your items.
     * @return true in order to show the menu, otherwise false.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().toString().equals("barcode")) {
            in.setClass(this, MainActivity.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("sign up")) {
            in.setClass(this, signup.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("csv")) {
            in.setClass(this, csv_imp.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("machine learning")) {
            in.setClass(this, TeachableMachine.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("Gallery")) {
            in.setClass(this, uplodeFromGallery.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("Camera")) {
            in.setClass(this, uplodeFromCamera.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}