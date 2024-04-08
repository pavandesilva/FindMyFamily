package com.finalapp.findmyfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.finalapp.findmyfamily.model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNewAccount extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    public UserDetails userDetails;
    EditText emailEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;
    Button createNewAccountButton, loginButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);

        userDetails = new UserDetails();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        emailEditText = findViewById(R.id.newAccount_editText_email);
        passwordEditText = findViewById(R.id.newAccount_editText_password);
        repeatPasswordEditText = findViewById(R.id.newAccount_editText_repeatPassword);
        createNewAccountButton = findViewById(R.id.newAccount_createNewAccountButton);
        loginButton = findViewById(R.id.newAccount_signInButton);
        progressBar = findViewById(R.id.newAccount_progressBar);

        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccountButton.requestFocus();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String rePassword = repeatPasswordEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    emailEditText.setError("Email cannot be empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Enter a valid email");
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Password cannot be empty");
                    Toast.makeText(CreateNewAccount.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    passwordEditText.setError("Password is too short");
                    Toast.makeText(CreateNewAccount.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(rePassword)) {
                    repeatPasswordEditText.setError("Password fields doesn't match.");
                    Toast.makeText(CreateNewAccount.this, "Password doesn't match with repeated password.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        currentUser = task.getResult().getUser();
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(CreateNewAccount.this, "Check your email to confirm your account.", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        });
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(CreateNewAccount.this, "User credentials registration failed.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}