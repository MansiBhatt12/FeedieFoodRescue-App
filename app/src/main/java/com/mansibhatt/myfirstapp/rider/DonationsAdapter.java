package com.mansibhatt.myfirstapp.rider;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mansibhatt.myfirstapp.DonationList;
import com.mansibhatt.myfirstapp.Donations;
import com.mansibhatt.myfirstapp.R;
import com.mansibhatt.myfirstapp.RetrieveMapActivity;

import java.util.ArrayList;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsAdapter.DonationsViewHolder> {

    Context context;
    ArrayList<DonationList> list;
    private DatabaseReference databaseReference1;
    Donations donations;
    FirebaseFirestore firestore;

    private FirebaseAuth firebaseAuth;
    String userID;

    private FusedLocationProviderClient mLocationClient;
    GoogleMap mGoogleMap;

    public DonationsAdapter(Context context, ArrayList<DonationList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DonationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new DonationsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  DonationsAdapter.DonationsViewHolder holder, int position) {

        DonationList donationList = list.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();

//        String keys = FirebaseDatabase.getInstance().getReference(String.valueOf(position)).getKey();

        holder.tvdonationweight.setText(donationList.getDonationWeight());
        holder.tvdonationtype.setText(donationList.getDonationType());
        holder.tvdonatiovehicle.setText(donationList.getDonationVehivle());
        holder.tvmobile.setText(donationList.getMobile());
        holder.tvdate.setText(donationList.getDonationdate());

        holder.tvtime.setText(donationList.getDonationtime());
        holder.tvid.setText(donationList.getUserID());

//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Donations");
//        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        databaseReference.orderByChild("userID").equalTo(currentUserID);
//        String keys = databaseReference.getKey();
//        Log.d("TAG","chathura"+keys);



        holder.tvlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myactivity = new Intent(context.getApplicationContext(), RetrieveMapActivity.class);
                myactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(myactivity);

            }

        });
        holder.adapter_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.moredetails);

                TextView fname,address,donationID,type,weight,time,date,mobile,street,userid;
                Button btn_close;

                btn_close = dialog.findViewById(R.id.btn_close);
                fname = dialog.findViewById(R.id.fname);
                address = dialog.findViewById(R.id.address);
                donationID = dialog.findViewById(R.id.donationID);

                type = dialog.findViewById(R.id.type);
                weight = dialog.findViewById(R.id.weight);
                time = dialog.findViewById(R.id.time);
                date = dialog.findViewById(R.id.date);
                mobile = dialog.findViewById(R.id.mobile);
                street = dialog.findViewById(R.id.street);
                userid = dialog.findViewById(R.id.userid);


                fname.setText(donationList.getfName());
                address.setText(donationList.getAddress());
                type.setText(donationList.getDonationType());

                weight.setText(donationList.getDonationWeight());
                time.setText(donationList.getDonationtime());
                date.setText(donationList.getDonationdate());
                mobile.setText(donationList.getMobile());
                street.setText(donationList.getStreet());
                userid.setText(donationList.getUserID());
                donationID.setText(donationList.getKey());



                btn_close.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();


                    }
                });

                dialog.show();
            }


        });
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   checkStatus();

               // if (status.equals("accept")){

                DocumentReference df = firestore.collection("users").document(FirebaseAuth.getInstance().getUid());
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        status = documentSnapshot.getString("status");

                        if (status.equals("accept")){

                            Intent myactivity = new Intent(context.getApplicationContext(), RetrieveMapActivity.class);
                            myactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.getApplicationContext().startActivity(myactivity);

                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Donations");

                            databaseReference1.child(donationList.getKey()).child("state").setValue(2);
                            databaseReference1.child(donationList.getKey()).child("connectingkey").setValue(userID+2);
                            databaseReference1.child(donationList.getKey()).child("riderkey").setValue(userID);
                            databaseReference1.child(donationList.getKey()).child("riderid").setValue(userID.toString());
                        }
                        else {
                            Toast.makeText(context, "Ur account declined", Toast.LENGTH_SHORT).show();
                            checkStatus();
                        }
                    }
                });
            }

        });


    }

    private String status;
    private void checkStatus() {
        DocumentReference df = firestore.collection("users").document(FirebaseAuth.getInstance().getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                status = documentSnapshot.getString("status");

                if (status.equals("decline")){
                    Toast.makeText(context, "Your Account declined", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DonationsViewHolder extends RecyclerView.ViewHolder{

        TextView tvdonationweight,tvdonationtype,tvdonatiovehicle,tvmobile,tvdate,tvtime,tvid;
        LinearLayout tvlocation,adapter_more_info;
        Button btn_close,acceptBtn;


        public DonationsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvdonationweight = itemView.findViewById(R.id.tvdonationweight);
            tvdonationtype = itemView.findViewById(R.id.tvdonationtype);
            tvdonatiovehicle = itemView.findViewById(R.id.tvdonatiovehicle);
            tvmobile = itemView.findViewById(R.id.tvmobile);
            tvdate = itemView.findViewById(R.id.tvdate);
            tvtime = itemView.findViewById(R.id.tvtime);
            tvid = itemView.findViewById(R.id.tvid);
            tvlocation = itemView.findViewById(R.id.tvlocation);
            adapter_more_info = itemView.findViewById(R.id.adapter_more_info);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);

        }
    }
}
