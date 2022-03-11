package com.example.e_commerce_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUserActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText emailUserET, passwordUserET, fullNameUserET;
    private Button registerUserBtn;
  //  private TextView registerSellerTv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private String email, password, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        backBtn = findViewById(R.id.backBtn);
        emailUserET = findViewById(R.id.emailUserET);
        fullNameUserET = findViewById(R.id.fullNameUserET);
        passwordUserET = findViewById(R.id.passwordUserET);
        registerUserBtn = findViewById(R.id.registerUserBtn);
        //registerSellerTv = findViewById(R.id.registerSellerTv);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // register user
                userinputData();
            }
        });

//        registerSellerTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // open Register Selller Activity
//                startActivity(new Intent(RegisterUserActivity.this, RegisterAdminActivity.class));
//
//            }
//        });
    }

    private void userinputData() {

        email = emailUserET.getText().toString().trim();
        password = passwordUserET.getText().toString().trim();
        fullName = fullNameUserET.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please give your Name ", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Pattern", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 4) {
            Toast.makeText(this, "Password must be atleast 6 Characters", Toast.LENGTH_SHORT).show();
            return;
        }

        createUserAccount();

    }


    private void createUserAccount() {

        progressDialog.setMessage("Creating Account......");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // Seller Account Created

                        saveUserDataWithFirebase();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // failed Creating Account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void saveUserDataWithFirebase() {
        progressDialog.setMessage("Saving Selling Account info.....");


        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("uId", "" + firebaseAuth.getUid());
        hashmap.put("fullName", "" + fullName);
        hashmap.put("email", "" + email);
        hashmap.put("password", "" + password);
        hashmap.put("timestamp", "" + timestamp);
        hashmap.put("accountType", "User");
        hashmap.put("online", "true");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // set data with db
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed Update to db
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                        finish();
                    }
                });


    }


}