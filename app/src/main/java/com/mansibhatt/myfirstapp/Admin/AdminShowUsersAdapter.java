package com.mansibhatt.myfirstapp.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mansibhatt.myfirstapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminShowUsersAdapter extends RecyclerView.Adapter<AdminShowUsersAdapter.ViewHolder> {

    Context context;
    ArrayList<UsersModel> usersModelArrayList;

    public AdminShowUsersAdapter(Context context, ArrayList<UsersModel> usersModelArrayList) {
        this.context = context;
        this.usersModelArrayList = usersModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new AdminShowUsersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UsersModel usersModel = usersModelArrayList.get(position);

        String id = usersModel.getId();
        String status = usersModel.getStatus();

        //check status for button visible
        if (status.equals("accept")){
            holder.approveBtn.setVisibility(View.GONE);
            holder.declineBtn.setVisibility(View.VISIBLE);
        }
        else if (status.equals("decline")){
            holder.approveBtn.setVisibility(View.VISIBLE);
            holder.declineBtn.setVisibility(View.GONE);
        }

        //check image for users
        if (usersModel.getUserType().equals("donor")){
            holder.profilePic.setImageResource(R.drawable.donor);
        }
        else if (usersModel.getUserType().equals("rider")){
            holder.profilePic.setImageResource(R.drawable.rider);
        }
        else if (usersModel.getUserType().equals("receiver")){
            holder.profilePic.setImageResource(R.drawable.receiver);
        }
        else {
            holder.profilePic.setImageResource(R.drawable.profile);
        }

        holder.nameEt.setText("Username: "+usersModel.getfName());
        holder.status.setText("User Status: "+usersModel.getStatus());
        holder.userType.setText("UserType : "+usersModel.getUserType());

        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Confirm Decline";
                String message = "Do you want to decline user";
                String statusId = "declineBtn";
                loadPopup(title,message,id, statusId);
            }
        });

        holder.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Confirm Approve";
                String message = "Do you want to approve user";
                String statusId = "approveBtn";
                loadPopup(title,message,id,statusId);
            }
        });
    }

    private void loadPopup(String title, String message, String id, String statusId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (statusId.equals("declineBtn")){
                    declineUser(id);
                }
                else if (statusId.equals("approveBtn")){
                    approveUser(id);
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void approveUser(String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().
                collection("users").document(id);
        Map<String,Object> user = new HashMap<>();
        user.put("status","accept");
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "user approved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void declineUser(String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().
                collection("users").document(id);
        Map<String,Object> user = new HashMap<>();
        user.put("status","decline");
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "user declined", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameEt,userType,status,declineBtn,approveBtn;
        CircleImageView profilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameEt = itemView.findViewById(R.id.nameEt);
            userType = itemView.findViewById(R.id.typeEt);
            status = itemView.findViewById(R.id.statusEt);
            declineBtn = itemView.findViewById(R.id.declineBtn);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}
