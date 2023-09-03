package com.mansibhatt.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mansibhatt.myfirstapp.Admin.AdminHomeActivity;
import com.mansibhatt.myfirstapp.Reciver.ReceiverHomeActivity;
import com.mansibhatt.myfirstapp.Reciver.SignupActivity;
import com.mansibhatt.myfirstapp.rider.RiderHomeActivity;
import com.mansibhatt.myfirstapp.rider.RiderRegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String KEY_USER_TYPE = "USERTYPE";
    private EditText mEmail, mPassword;
    TextView forgotTextLink, reciver;
    private Button mLoginBtn;
    private FirebaseAuth fAuth;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String userID;
    String TAG = "LoginActivity";

    private static final String SHARED_PREF_NAME = "mypref";


    private static final String KEY_NAME = "email";
    private static final String KEY_NAME_ONE = "email";


    //Login
    SharedPreferences user_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        user_session = getSharedPreferences("user_details", MODE_PRIVATE);


        checkInternetAvailable();

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        forgotTextLink = findViewById(R.id.forgotpassword);
        reciver = findViewById(R.id.reciver);

        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        /// String isDonator = sharedPreferences.getString(KEY_NAME, null);

        reciver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });


//        if (isDonator != null){
//
//            Log.d(TAG,"isDonator");
//            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//            startActivity(intent);
//
//        }else {
//
//        }
//
//        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME1,MODE_PRIVATE);
//        String isRider= sharedPreferences.getString(KEY_NAME_ONE,null);
//
//        if (isRider != null){
//            Log.d(TAG,"isRider");
//            Intent intent = new Intent(LoginActivity.this,RiderHomeActivity.class);
//            startActivity(intent);
//        }else {
//
//        }


        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkInternetAvailable();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    return;

                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password  Must be >= 6 Characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                        Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            check(fAuth.getCurrentUser().getUid());
//
//
//
//                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//
                        } else {
                            Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        ///////forgot password text Ckicked

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAvailable();
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new
                        AlertDialog.Builder(v.getContext());

                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email");

                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //extract the email and send reset link

                                String mail = resetMail.getText().toString();
                                fAuth.sendPasswordResetEmail(mail).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(LoginActivity.
                                                                this, "Reset Link Sent To Your Email",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this,
                                                        "Error ! Reset Link Sent To Your Email" + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                passwordResetDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //close the dialog box
                            }
                        });
                passwordResetDialog.create().show();

            }
        });
    }

    private void check(String uid) {

        DocumentReference df = fStore.collection("users").document(uid);
        ///
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess" + documentSnapshot.getData());

                String usertype = documentSnapshot.getString("userType");
                String status = documentSnapshot.getString("status");

                if (usertype.equals("admin")) {

                    SharedPreferences.Editor editor = user_session.edit();
                    editor.putString("cu_type", "Admin");
                    editor.putBoolean("isLoggedIn", true);
                    editor.commit();
                    Toast.makeText(LoginActivity.this, " Admin Logged in Successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                    finish();

                } else {
                    if (status.equals("accept")) {

                        if (usertype.equals("donor")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Donator");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "accept");
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "Donator in Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();


                        }

                        if (usertype.equals("rider")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Rider");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "accept");

                            editor.commit();
                            Toast.makeText(LoginActivity.this, " Rider Logged in Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), RiderHomeActivity.class));
                            finish();

                        }

                        if (usertype.equals("receiver")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Receiver");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "accept");

                            editor.commit();
                            Toast.makeText(LoginActivity.this, " Receiver Logged in Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), ReceiverHomeActivity.class));
                            finish();

                        }


                    } else {

                        if (usertype.equals("donor")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Donator");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "decline");
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "Your account declined", Toast.LENGTH_SHORT).show();


                        }

                        if (usertype.equals("rider")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Rider");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "decline");
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "Your account declined", Toast.LENGTH_SHORT).show();
                        }

                        if (usertype.equals("receiver")) {

                            SharedPreferences.Editor editor = user_session.edit();
                            editor.putString("cu_type", "Receiver");
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("status", "decline");
                            editor.commit();
                            Toast.makeText(LoginActivity.this, "Your account declined", Toast.LENGTH_SHORT).show();

                        }

                    }


                }


            }
        });
    }

    private void checkInternetAvailable() {

        ////////////Initialize ConnectionManager/////////////////
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.
                        CONNECTIVITY_SERVICE);

        //////////Get active network info////////////////////////
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        ////////////////check network status////////////////////
        if (networkInfo == null || !networkInfo.isConnected() ||
                !networkInfo.isAvailable()) {
///////////when internet is  inactive/////////////////


            /////////initialize dialog//////////////////////////////
            Dialog dialog = new Dialog(this);
            ////////set Content View///////////////////////////
            dialog.setContentView(R.layout.alert_dialog);
            ///set outside touch////////
            dialog.setCanceledOnTouchOutside(false);
            /////set dialog width and height///////////
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            //////////////set transparent Background///////////////
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //////set animation
            dialog.getWindow().getAttributes().windowAnimations =
                    android.R.style.Animation_Dialog;

            /////////Initialize dialog variable
            Button btTryAgain = dialog.findViewById(R.id.bt_try_again);
            //////Perform onClick listner
            btTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //////call method
                    recreate();
                }
            });

            //show dialog
            dialog.show();

        }

    }


    public void Logintext(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    public void registerText(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    public void loginBtnlink(View view) {
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        finish();
    }

    public void riderresiter(View view) {
        startActivity(new Intent(LoginActivity.this, RiderRegisterActivity.class));
        finish();
    }
}