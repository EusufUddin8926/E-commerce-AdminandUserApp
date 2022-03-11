package com.example.e_commerce_store.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce_store.Model.ModelOrderAdmin;
import com.example.e_commerce_store.R;
import com.example.e_commerce_store.AdminOrderDetails;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderAdmin extends RecyclerView.Adapter<AdapterOrderAdmin.HolderOrderSelller> {

    private Context context;
    private ArrayList<ModelOrderAdmin> ordersellerList;


    public AdapterOrderAdmin(Context context, ArrayList<ModelOrderAdmin> ordersellerList) {
        this.context = context;
        this.ordersellerList = ordersellerList;
    }

    @NonNull
    @Override
    public HolderOrderSelller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_admin, parent, false);
        return new HolderOrderSelller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderSelller holder, int position) {

        ModelOrderAdmin modelOrderAdmin = ordersellerList.get(position);
        String orderId = modelOrderAdmin.getOrderId();
        String orderBy = modelOrderAdmin.getOrderBy();
        String orderTo = modelOrderAdmin.getOrderTo();
        String orderTime = modelOrderAdmin.getOrderTime();
        String orderStatus = modelOrderAdmin.getOrderStatus();
        String orderName = modelOrderAdmin.getpName();
        String orderPrice = modelOrderAdmin.getpPrice();


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
                Intent intent = new Intent(context, AdminOrderDetails.class);
                intent.putExtra("orderBy", orderTo);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersellerList.size();
    }

    class HolderOrderSelller extends RecyclerView.ViewHolder {

        private TextView orderTv, dateTv, pNameTv, pPriceTv, statusTv;


        public HolderOrderSelller(@NonNull View itemView) {
            super(itemView);
            orderTv = itemView.findViewById(R.id.ordersellerIdTv);
            dateTv = itemView.findViewById(R.id.ordersellerdateTv);
            pNameTv = itemView.findViewById(R.id.sellerpNameTv);
            pPriceTv = itemView.findViewById(R.id.sellerpPriceTv);
            statusTv = itemView.findViewById(R.id.sellerstatusTv);


        }
    }
}

