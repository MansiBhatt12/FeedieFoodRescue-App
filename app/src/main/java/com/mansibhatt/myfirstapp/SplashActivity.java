package com.mansibhatt.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mansibhatt.myfirstapp.Admin.AdminHomeActivity;
import com.mansibhatt.myfirstapp.Reciver.ReceiverHomeActivity;
import com.mansibhatt.myfirstapp.rider.RiderHomeActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences MainActivity;
    SharedPreferences user_session;
    String currentUserType, status;
    boolean isLoggedIn;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //User session
        user_session = this.getSharedPreferences("user_details", MODE_PRIVATE);


        //Get the Session Values
        currentUserType = user_session.getString("cu_type", "");
        isLoggedIn = user_session.getBoolean("isLoggedIn", false);
        status = user_session.getString("status", "");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                MainActivity = getSharedPreferences("onBordingScreen", MODE_PRIVATE);
                boolean isFirstTime = MainActivity.getBoolean("firstTime", true);


                if (isFirstTime) {

                    SharedPreferences.Editor editor = MainActivity.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    //Check whether user available or not

                    if (mAuth.getUid() != null) {

                        if (isLoggedIn) {

                            documentReference = fStore.collection("users").document(mAuth.getUid());
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String status = documentSnapshot.getString("status");

                                    if (currentUserType.equals("Admin")) {
                                        startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                                        finish();
                                    } else {

                                        if (status.equals("accept")) {

                                            if (currentUserType.equals("Rider")) {
                                                startActivity(new Intent(SplashActivity.this, RiderHomeActivity.class));
                                                finish();

                                            } else if (currentUserType.equals("Donator")) {

                                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                                finish();

                                            } else if (currentUserType.equals("Receiver")) {

                                                startActivity(new Intent(SplashActivity.this, ReceiverHomeActivity.class));
                                                finish();
                                            }
                                        } else if (status.equals("decline")) {
                                            Toast.makeText(SplashActivity.this, "Account Declined", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                            finish();
                                        }

                                    }


                                }
                            });


//                        if (status.equals("accept")) {
//
//                            if (currentUserType.equals("Rider")) {
//                                startActivity(new Intent(SplashActivity.this, RiderHomeActivity.class));
//                                finish();
//
//                            } else if (currentUserType.equals("Donator")) {
//
//                                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//                                finish();
//
//                            } else if (currentUserType.equals("Receiver")) {
//
//                                startActivity(new Intent(SplashActivity.this, ReceiverHomeActivity.class));
//                                finish();
//                            }
//
//                        } else if (status.equals("decline")) {
//
//                            Toast.makeText(SplashActivity.this, "Account Declined", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                            finish();
//
//                        } else if (currentUserType.equals("Admin")) {
//
//                            startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
//                            finish();
//                        }
//
//                    } else {
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
//
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }

                    }
                    else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }


                }


            }
        }, 8000);
    }
}