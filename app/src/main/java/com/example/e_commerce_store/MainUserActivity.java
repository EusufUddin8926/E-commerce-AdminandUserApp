package com.example.e_commerce_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.e_commerce_store.Adapter.ProductListAdapter;
import com.example.e_commerce_store.Model.ProductModel;
import com.example.e_commerce_store.constants.Constants;
import com.example.e_commerce_store.listner.ClickInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainUserActivity extends AppCompatActivity implements ClickInterface {

    private TextView nameTv, txtview;
    private ImageButton logoutBtn, OrderProductId, settingsBtn;
    private ProgressDialog dialog;
    private String sellerUid;


    private RecyclerView productShowRecycler;
    private ArrayList<ProductModel> productList;
    private ProductListAdapter productListAdapter;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv = findViewById(R.id.nameTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        OrderProductId = findViewById(R.id.OrderProductId);
        productShowRecycler = findViewById(R.id.productShowRecyclerId);
        settingsBtn = findViewById(R.id.settingsBtn);


        dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);


//        LinearLayoutManager layoutManager = new LinearLayoutManager(MainUserActivity.this);
//        productShowRecycler.setLayoutManager(layoutManager);


        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadProductDetails();


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        OrderProductId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, OrderActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, SettingsActivity.class));
            }
        });


    }

    private void loadProductDetails() {


        productList = new ArrayList<>();
        //String Uid = firebaseAuth.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.orderByChild("accountType").equalTo("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            sellerUid = "" + ds.child("uId").getValue();

                            reference.child(sellerUid).child("Products")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            productList.clear();
                                            for (DataSnapshot ds : snapshot.getChildren()) {

                                                ProductModel model = ds.getValue(ProductModel.class);
                                                productList.add(model);

                                            }

                                            productListAdapter = new ProductListAdapter(productList, MainUserActivity.this::onItemClick);

                                            productShowRecycler.setAdapter(productListAdapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void checkUser() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
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

                    }
                });
    }

    @Override
    public void onItemClick(int position) {

        showOrder(position);

    }

    private void showOrder(int position) {

        View view = LayoutInflater.from(this).inflate(R.layout.confirm_order_dialog, null);

        CircleImageView productIv = view.findViewById(R.id.productIv);
        TextView nameTv = view.findViewById(R.id.nameTv);
        TextView priceTv = view.findViewById(R.id.priceTv);
        TextView confirmOrder = view.findViewById(R.id.confirmOrderId);

        // get data from model

        ProductModel model = productList.get(position);

        String productName = model.getProductName();
        String productPrice = model.getProductPrice();
        String productId = model.getProductId();
        String producttimestamp = model.getTimestamp();
        String productimage = model.getProductIcon();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        try {
            Picasso.get().load(productimage).placeholder(R.drawable.ic_shopping_coloured).into(productIv);

        } catch (Exception e) {

            productIv.setImageResource(R.drawable.ic_shopping_coloured);

        }
        nameTv.setText(productName);
        priceTv.setText(productPrice);


        AlertDialog dialog = builder.create();
        dialog.show();

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmOrder(position);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                }, 2000);


            }
        });

    }

    private void confirmOrder(int position) {

        dialog.setMessage("placing order");
        dialog.show();
        ProductModel model = productList.get(position);

        final String timestamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + sellerUid);
        hashMap.put("pName", "" + model.getProductName());
        hashMap.put("pPrice", "" + model.getProductPrice());

        // add to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(sellerUid).child("Orders");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ProductModel model = productList.get(position);
                        String pId = model.getProductId();
                        String pName = model.getProductName();
                        String pPrice = model.getProductPrice();


                        HashMap<String, String> phashmap = new HashMap<>();
                        phashmap.put("pId", pId);
                        phashmap.put("pName", pName);
                        phashmap.put("pPrice", pPrice);

                        reference.child(timestamp).child("Items").child(pId).setValue(phashmap);


                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }

                });
        dialog.dismiss();
        Toast.makeText(MainUserActivity.this, "Order Place Successfully", Toast.LENGTH_SHORT).show();
        prepareNotificationMessage(timestamp);

    }

    private void prepareNotificationMessage(String orderId) {
        String NOTIFICATION_TOPIC = "//" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order " + orderId;
        String NOTIFICATION_MESSAGE = "Congtopicsratulations...! You have new order.";
        String NOTIFICATION_TYPE = "NewOrder";

        // Prepare json What to sent and Where to send
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            // what to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid", sellerUid);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            // where to send

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);


        } catch (Exception e) {

            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        sendFcmNotification(notificationJo, orderId);


    }

    private void sendFcmNotification(JSONObject notificationJo, final String orderId) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Intent intent = new Intent(MainUserActivity.this, UserOrderdetails.class);
                intent.putExtra("orderTo", sellerUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Intent intent = new Intent(MainUserActivity.this, UserOrderdetails.class);
                intent.putExtra("orderTo", sellerUid);
                intent.putExtra("orderId", orderId);
                startActivity(intent);

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