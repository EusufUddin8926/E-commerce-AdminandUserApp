package com.example.e_commerce_store;

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

import com.example.e_commerce_store.constants.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat fcmSwitch;
    private TextView notificationStatusTv;
    private ImageButton backBtn;
    private Boolean isChecked = false;

    private SharedPreferences sp;
    private SharedPreferences.Editor speditor;

    public static final String enableMessage = "Notifications are enabled";
    public static final String disabledMessage = "Notifications are disabled";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);
        backBtn = findViewById(R.id.backBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);
        isChecked = sp.getBoolean("FCM_ENABLED", false);
        fcmSwitch.setChecked(isChecked);
        if (isChecked) {
            // was enable
            notificationStatusTv.setText(enableMessage);

        } else {
            // was disable
            notificationStatusTv.setText(disabledMessage);

        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //add switch check change Listner to enable disable notifications

        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // checked , enable notifications

                    subscribeToTopic();

                } else {
                    // Unchecked , disable notifications
                    UnsubscribeToTopic();

                }
            }
        });

    }

    public void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Suscribed successfully
                        // save setting in shareprefenerce
                        speditor = sp.edit();
                        speditor.putBoolean("FCM_ENABLED", true);
                        speditor.apply();
                        Toast.makeText(SettingsActivity.this, "" + enableMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(enableMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // failed Suscribed
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void UnsubscribeToTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //UnSuscribed successfully

                        // save setting in shareprefenerce
                        speditor = sp.edit();
                        speditor.putBoolean("FCM_ENABLED", false);
                        speditor.apply();
                        Toast.makeText(SettingsActivity.this, "" + disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // failed UnSuscribed
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}


// now we need to save the state of what user chose for this I am using sharePreference
