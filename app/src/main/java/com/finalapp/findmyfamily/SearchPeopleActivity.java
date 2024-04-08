package com.finalapp.findmyfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.finalapp.findmyfamily.Utils.Common;
import com.finalapp.findmyfamily.model.Member;
import com.finalapp.findmyfamily.model.User;
import com.finalapp.findmyfamily.model.UserDetails;
import com.finalapp.findmyfamily.navigation.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchPeopleActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    UserDetails userDetails;
    Member memberDetails;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    MyAdapter myAdapter;
    ArrayList<User> list;
    SearchView search;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        search = findViewById(R.id.searchBar);
        addButton = findViewById(R.id.addButton);
//        recyclerView = findViewById(R.id.usersList);
        databaseReference = FirebaseDatabase.getInstance().getReference();
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
//        recyclerView.setAdapter(myAdapter);

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    User user = dataSnapshot.getValue(User.class);
//                    list.add(user);
//                }
//                myAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence member = search.getQuery();
                String memberEmail = String.valueOf(member);
                if (member != null) {
                    Toast.makeText(SearchPeopleActivity.this, member, Toast.LENGTH_LONG).show();

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchPeopleActivity.this, R.style.Theme_FindMyFamily);
                    alertDialog.setTitle("Request Friend");
                    alertDialog.setMessage("Do you want to send a friend request to " + member);
                    alertDialog.setIcon(R.drawable.ic_sidebar_person);

                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            databaseReference.child("users").child(currentUser.getUid()).child("members")
                                    .setValue(memberEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SearchPeopleActivity.this, "Request sent.", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SearchPeopleActivity.this, "Request sending failed. " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });



                            //                            DatabaseReference acceptList = FirebaseDatabase.getInstance()
//                                    .getReference(currentUser.getUid())
//                                    .child(currentUser.getUid())
//                                    .child(Common.ACCEPT_LIST);

//                            databaseReference.orderByChild("email").equalTo(memberEmail).on("value", function(snapshot) {
//                                snapshot.forEach((function(child) { console.log(child.key) });
//                            });


                                databaseReference.child("users").child("friends").orderByChild("email").equalTo(memberEmail).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
//                                    memberDetails = task.getResult(Member.class);
                                        Toast.makeText(SearchPeopleActivity.this, "Pending friend request", Toast.LENGTH_LONG).show();
                                        finish();

                                    } else {
                                        databaseReference.child(currentUser.getUid())
                                                .child("friends")
                                                .setValue(memberDetails.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(SearchPeopleActivity.this, "Friend request sent", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        ;
                    });
                    alertDialog.show();
                }
                ;


            }

        });
    }

    ;
}
