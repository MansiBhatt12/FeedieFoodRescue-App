package com.mansibhatt.myfirstapp.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mansibhatt.myfirstapp.R;
import com.mansibhatt.myfirstapp.SplashActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    AdminShowUsersAdapter adminShowUsersAdapter;
    ArrayList<UsersModel> usersModelArrayList;
    RecyclerView recyclerView;
    ImageButton logOutBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        fStore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycleView);
        logOutBtn = findViewById(R.id.logOutBtn);

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Data Loading");
//        progressDialog.show();

        usersModelArrayList = new ArrayList<>();
        adminShowUsersAdapter = new AdminShowUsersAdapter(AdminHomeActivity.this, usersModelArrayList);
        recyclerView.setAdapter(adminShowUsersAdapter);

        showAllUsers();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });

    }

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("confirm logout");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminHomeActivity.this, SplashActivity.class));
                finish();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    private void showAllUsers() {

        fStore.collection("users").orderBy("fName").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                usersModelArrayList.clear();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d:list){
                    UsersModel object = d.toObject(UsersModel.class);
                    usersModelArrayList.add(object);
                }
                showAllUsers();
                adminShowUsersAdapter.notifyDataSetChanged();
            }
        });

//        fStore.collection("users").orderBy("fName").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                //usersModelArrayList.clear();
//
//                if (error != null) {
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                }
//
//                for (DocumentChange dc : value.getDocumentChanges()) {
//
//                    if (dc.getType() == DocumentChange.Type.ADDED) {
//                        usersModelArrayList.add(dc.getDocument().toObject(UsersModel.class));
//                    }
//                    adminShowUsersAdapter.notifyDataSetChanged();
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                }
//            }
//        });
    }
}