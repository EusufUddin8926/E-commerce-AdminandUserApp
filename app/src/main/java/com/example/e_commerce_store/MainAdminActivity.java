package com.example.e_commerce_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce_store.Adapter.AdapterOrderAdmin;
import com.example.e_commerce_store.Model.ModelOrderAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainAdminActivity extends AppCompatActivity {

    String orderId, orderBy;

    private TextView nameTv;
    private ImageButton logoutBtn, addProductBtn, settingsBtn;
    private RecyclerView sellerordersRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderAdmin> adminOrderList;
    private AdapterOrderAdmin adapterOrderAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        nameTv = findViewById(R.id.nameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        sellerordersRv = findViewById(R.id.sellerordersRv);
        settingsBtn = findViewById(R.id.settingsBtn);


        Intent intent = getIntent();
        orderBy = intent.getStringExtra("orderBy");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        displayAdminOrder();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });


        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start AddProduct Activity
                startActivity(new Intent(MainAdminActivity.this, AddProductActivity.class));
            }
        });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainAdminActivity.this, SettingsActivity.class));
            }
        });


    }

    private void displayAdminOrder() {

        adminOrderList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        adminOrderList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelOrderAdmin modelOrderAdmin = ds.getValue(ModelOrderAdmin.class);
                            adminOrderList.add(modelOrderAdmin);

                        }


                        adapterOrderAdmin = new AdapterOrderAdmin(MainAdminActivity.this, adminOrderList);
                        sellerordersRv.setAdapter(adapterOrderAdmin);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void checkUser() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainAdminActivity.this, LoginActivity.class));
            finish();
        } else {
            loadinfo();
        }
    }

    private void loadinfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uId").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String fullName = "" + ds.child("fullName").getValue();
                            String email = "" + ds.child("email").getValue();
                            String accounttype = "" + ds.child("accountType").getValue();

                            nameTv.setText(fullName + "(" + accounttype + ")");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(MainAdminActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}