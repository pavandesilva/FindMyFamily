package com.finalapp.findmyfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    Button newAccountButton;
    Button LoginButton;
    TextView forgotPw;
    EditText email, password;
    FirebaseUser currentUser;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        newAccountButton = findViewById(R.id.createNewAccount_button);
        LoginButton = findViewById(R.id.login_button);
        forgotPw = findViewById(R.id.textView_forgot_password);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        progressBar = findViewById(R.id.newAccount_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, "Check your email to verify your account.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        progressBar.setVisibility(View.GONE);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser.isEmailVerified()) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignInActivity.this, "Check your email to verify your account.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Failed to login. Please check your email and password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateNewAccount.class));
            }
        });

        forgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

    }
}