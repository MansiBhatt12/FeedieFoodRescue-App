package com.mansibhatt.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    private static final String enabledMessage = "Notifications are enabled";
    private static final String disabledMessage = "Notifications are disabled";

    private SharedPreferences sp_client;
    private SharedPreferences.Editor spClientEditor;
    private boolean isChecked = false;

    SwitchCompat fcmSwitch;
    TextView notificationStatusTv;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);
        backBtn = findViewById(R.id.backBtn);

        sp_client = SettingsActivity.this.getSharedPreferences("CLIENT_SETTINGS_SP", MODE_PRIVATE);
        //check last selected option: true/false
        isChecked = sp_client.getBoolean("CLIENT_FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);
        if (isChecked) {
            //was enabled
            notificationStatusTv.setText(enabledMessage);
            notificationStatusTv.setTextColor(getResources().getColor(R.color.newyellow));
        } else {
            notificationStatusTv.setText(disabledMessage);
            notificationStatusTv.setTextColor(getResources().getColor(R.color.blacks));
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //enable notification
                    subscribeToTopic();
                } else {
                    //disable notification
                    unSubscribeToTopic();
                }
            }
        });
    }

    private void unSubscribeToTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Unsubscribe
                //save settings ins shared preferences
                spClientEditor = sp_client.edit();
                spClientEditor.putBoolean("CLIENT_FCM_ENABLED", false);
                spClientEditor.apply();
                Toast.makeText(SettingsActivity.this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
                notificationStatusTv.setText(disabledMessage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Unsubscribe Failed
                Toast.makeText(SettingsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC).addOnSuccessListener(unused -> {
            //subscribed Successfully
            //save settings ins shared preferences
            spClientEditor = sp_client.edit();
            spClientEditor.putBoolean("CLIENT_FCM_ENABLED", true);
            spClientEditor.apply();
            Toast.makeText(SettingsActivity.this, "" + enabledMessage, Toast.LENGTH_SHORT).show();
            notificationStatusTv.setText(enabledMessage);


        }).addOnFailureListener(e -> {
            //subscribed failed
            Toast.makeText(SettingsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}