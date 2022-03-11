package com.example.e_commerce_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_commerce_store.constants.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminOrderDetails extends AppCompatActivity {

    String orderBy, orderId;

    private ImageButton backBtn, editBtn;
    private TextView orderIdTv, dateIdTv, orderStatusTv, pNameTv, pPriceTv;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_details);


        orderIdTv = findViewById(R.id.orderIdTv);
        dateIdTv = findViewById(R.id.dateIdTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        pNameTv = findViewById(R.id.pNameTv);
        pPriceTv = findViewById(R.id.pPriceTv);
        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);

        firebaseAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        orderBy = intent.getStringExtra("orderBy");
        orderId = intent.getStringExtra("orderId");


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrderStatusDialog();
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderBy = "" + snapshot.child("orderBy").getValue();
                        String orderId = "" + snapshot.child("orderId").getValue();
                        String orderstatus = "" + snapshot.child("orderStatus").getValue();
                        String orderpPrice = "" + snapshot.child("pPrice").getValue();
                        String orderpName = "" + snapshot.child("pName").getValue();
                        String orderTime = "" + snapshot.child("orderTime").getValue();
                        String orderTo = "" + snapshot.child("orderTo").getValue();

                        //convert timestamp to proper format
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formatDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();


                        if (orderstatus.equals("In Progress")) {

                            orderStatusTv.setTextColor(AdminOrderDetails.this.getResources().getColor(R.color.purple_500));

                        } else if (orderstatus.equals("Completed")) {

                            orderStatusTv.setTextColor(AdminOrderDetails.this.getResources().getColor(R.color.purple_200));
                        } else if (orderstatus.equals("Cancelled")) {

                            orderStatusTv.setTextColor(AdminOrderDetails.this.getResources().getColor(R.color.black));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderstatus);
                        dateIdTv.setText(formatDate);
                        pNameTv.setText(orderpName);
                        pPriceTv.setText(orderpPrice);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void editOrderStatusDialog() {

        String[] options = {"In Progress", "On The Way", "Delivared","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Details")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String selectedOptions = options[i];
                        editorderStatus(selectedOptions);

                    }
                })
                .show();
    }

    private void editorderStatus(String selectedOptions) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", "" + selectedOptions);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = "Order is now" + selectedOptions;

                        Toast.makeText(AdminOrderDetails.this, message, Toast.LENGTH_SHORT).show();
                        prepareNotificationMessage(orderId, message);

                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(AdminOrderDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void prepareNotificationMessage(String orderId, String message) {

        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order " + orderId;
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        // Prepare json What to sent and Where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            // what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", orderBy);
            notificationBodyJo.put("sellerUid", firebaseAuth.getUid());
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            // where to send

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);


        } catch (Exception e) {

            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        sendFcmNotification(notificationJo);


    }

    private void sendFcmNotification(JSONObject notificationJo) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Notification sent
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Notification Failed
                Toast.makeText(AdminOrderDetails.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);

                return headers;
            }
        };

        // enque the Volly request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}