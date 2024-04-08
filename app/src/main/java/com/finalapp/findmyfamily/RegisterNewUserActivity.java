package com.finalapp.findmyfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.finalapp.findmyfamily.model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterNewUserActivity extends AppCompatActivity {

    UserDetails userDetails;
    ImageView userImage;
    Button registerButton;
    EditText firstnameEditText, lastnameEditText, phoneEditText, addressEditText;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databseReference;
    ProgressBar progressBar;
    private Uri imageUri;
    private Bitmap compressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        userImage = findViewById(R.id.newAccount_imageView);
        registerButton = findViewById(R.id.register_register_button);
        firstnameEditText = findViewById(R.id.register_firstname);
        lastnameEditText = findViewById(R.id.register_lastname);
        phoneEditText = findViewById(R.id.register_phone);
        addressEditText = findViewById(R.id.register_address);
        progressBar = findViewById(R.id.newAccount_progressBar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databseReference = firebaseDatabase.getReference();


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(RegisterNewUserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(RegisterNewUserActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(RegisterNewUserActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        chooseImage();
                    }
                } else {
                    chooseImage();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstname = firstnameEditText.getText().toString().trim();
                String lastname = lastnameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();

                if (firstname.isEmpty()) {
                    firstnameEditText.setError("Firstname cannot be empty");
                } else if (lastname.isEmpty()) {
                    lastnameEditText.setError("Lastname cannot be empty");
                } else if (phone.isEmpty()) {
                    phoneEditText.setError("Phone number cannot be empty");
                } else if (!Patterns.PHONE.matcher(phone).matches()) {
                    phoneEditText.setError("Enter a valid phone number");
                } else if (address.isEmpty()) {
                    addressEditText.setError("address cannot be empty.");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    userDetails = new UserDetails(currentUser.getEmail(), firstname, lastname, phone, address);

                    databseReference.child("users").child(currentUser.getUid())
                            .setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterNewUserActivity.this, "User registration failed" + task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivity(intent);
    }
}


//        progressBar.setVisibility(View.VISIBLE);
//        if (currentUser != null) {
//            if (currentUser.isEmailVerified()) {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
//            } else {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(RegisterNewUserActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
//            }
//        }else {
//            Toast.makeText(RegisterNewUserActivity.this, "current user null.", Toast.LENGTH_LONG).show();
//
//        }
//        progressBar.setVisibility(View.GONE);

