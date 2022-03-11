package com.example.e_commerce_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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

public class UserOrderdetails extends AppCompatActivity {

    String orderTo, orderId;
    private ImageButton backBtn;
    private TextView orderIdTv, dateIdTv, orderStatusTv, pNameTv, pPriceTv;

    //private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orderdetails);

        orderIdTv = findViewById(R.id.orderIdTv);
        dateIdTv = findViewById(R.id.dateIdTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        pNameTv = findViewById(R.id.pNameTv);
        pPriceTv = findViewById(R.id.pPriceTv);
        backBtn = findViewById(R.id.backBtn);


        //firebaseAuth = FirebaseAuth.getInstance();
        // loadOrderDetails();


        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId)
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

                            orderStatusTv.setTextColor(UserOrderdetails.this.getResources().getColor(R.color.purple_500));

                        } else if (orderstatus.equals("Completed")) {

                            orderStatusTv.setTextColor(UserOrderdetails.this.getResources().getColor(R.color.purple_200));
                        } else if (orderstatus.equals("Cancelled")) {

                            orderStatusTv.setTextColor(UserOrderdetails.this.getResources().getColor(R.color.black));
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


}