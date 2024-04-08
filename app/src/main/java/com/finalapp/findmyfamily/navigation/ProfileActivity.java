package com.finalapp.findmyfamily.navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.finalapp.findmyfamily.MainActivity;
import com.finalapp.findmyfamily.R;
import com.finalapp.findmyfamily.RegisterNewUserActivity;
import com.finalapp.findmyfamily.model.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference referenceUser;
    UserDetails userDetails;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    TextView firstname, lastname, phoneNo, address;
    ImageView userImage;
    Button saveButton;
    ProgressBar progressBar;
    Uri imagePath;


    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstname = findViewById(R.id.profile_firstname);
        lastname = findViewById(R.id.profile_lastname);
        phoneNo = findViewById(R.id.profile_phone);
        address = findViewById(R.id.profile_address);
        userImage = findViewById(R.id.profile_imageView);
        saveButton = findViewById(R.id.save_data_button);
        progressBar = findViewById(R.id.profile_progressBar);
        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        referenceUser = firebaseDatabase.getReference("users");
        String userid = currentUser.getUid();

        referenceUser.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    userDetails = snapshot.getValue(UserDetails.class);
                    if (userDetails != null) {

                        firstname.setText(userDetails.getFirstname());
                        lastname.setText(userDetails.getLastname());
                        phoneNo.setText(userDetails.getPhone());
                        address.setText(userDetails.getAddress());

//                        Toast.makeText(MainActivity.this, "Welcome " + userDetails.getFirstname() + " " + userDetails.getLastname(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterNewUserActivity.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, R.string.unknownErrorMessage, Toast.LENGTH_LONG).show();
            }
        });

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null){
                            Uri data = result.getData().getData();
                            imagePath = data;
                            System.out.println(imagePath);
                            Toast.makeText(ProfileActivity.this,"dfafdsfa",Toast.LENGTH_LONG);

                            Log.i("IMG", "onActivityResult: getData is null");

                            userImage.setImageURI(imagePath);
                        }else {
                            Toast.makeText(ProfileActivity.this,"Testing 1234",Toast.LENGTH_LONG);

                            Log.i("IMG", "onActivityResult: getData is null");

                        }
                    }
                });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
//                        {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivity(intent);
                Toast.makeText(ProfileActivity.this,"test1",Toast.LENGTH_LONG);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fName = String.valueOf(firstname.getText());
                String lName = String.valueOf(lastname.getText());
                String pno = String.valueOf(phoneNo.getText());
                String add = String.valueOf(address.getText());

                if (fName.isEmpty()) {
                    firstname.setError("Firstname cannot be empty");
                } else if (lName.isEmpty()) {
                    lastname.setError("Lastname cannot be empty");
                } else if (pno.isEmpty()) {
                    phoneNo.setError("Phone number cannot be empty");
                } else if (!Patterns.PHONE.matcher(pno).matches()) {
                    phoneNo.setError("Enter a valid phone number");
                } else if (add.isEmpty()) {
                    address.setError("address cannot be empty.");
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    userDetails = new UserDetails(currentUser.getEmail(), fName, lName, pno, add);
                    referenceUser.child(currentUser.getUid())
                            .setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "User registration failed. Try again later" + task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}