package com.example.e_commerce_store.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce_store.Model.ModelOrderUser;
import com.example.e_commerce_store.R;
import com.example.e_commerce_store.UserOrderdetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_user, parent, false);

        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {

        // get Data
        ModelOrderUser modelOrderUser = orderUserList.get(position);
        String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderTo = modelOrderUser.getOrderTo();
        String orderTime = modelOrderUser.getOrderTime();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderName = modelOrderUser.getpName();
        String orderPrice = modelOrderUser.getpPrice();

        //get orderinfo
        // loadOrderinfo(modelOrderUser, holder);

        // set Data

        holder.pPriceTv.setText("Price: " + orderPrice);
        holder.pNameTv.setText("Name: " + orderName);
        holder.statusTv.setText(orderStatus);
        holder.orderTv.setText("Order ID: " + orderId);

        if (orderStatus.equals("In Progress")) {

            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));

        } else if (orderStatus.equals("Completed")) {

            holder.statusTv.setTextColor(context.getResources().getColor(R.color.purple_200));
        } else if (orderStatus.equals("Cancelled")) {

            holder.statusTv.setTextColor(context.getResources().getColor(R.color.black));
        }

        //convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(formatDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserOrderdetails.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });

    }

//    private void loadOrderinfo(ModelOrderUser modelOrderUser, HolderOrderUser holder) {
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(modelOrderUser.getOrderTo())
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String pName = ""+snapshot.child("pName").getValue();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder {

        private TextView orderTv, dateTv, pNameTv, pPriceTv, statusTv;

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            orderTv = itemView.findViewById(R.id.orderTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            pNameTv = itemView.findViewById(R.id.pNameTv);
            pPriceTv = itemView.findViewById(R.id.pPriceTv);
            statusTv = itemView.findViewById(R.id.statusTv);


        }
    }
}
