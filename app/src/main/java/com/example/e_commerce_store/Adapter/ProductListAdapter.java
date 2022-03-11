package com.example.e_commerce_store.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce_store.Model.ProductModel;
import com.example.e_commerce_store.R;
import com.example.e_commerce_store.listner.ClickInterface;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.HolderProductList> {

    private Context context;
    public ArrayList<ProductModel> productList;
    private ProgressDialog dialog;
    private ClickInterface clickInterface;


    public ProductListAdapter(ArrayList<ProductModel> productList, ClickInterface clickInterface) {
        this.productList = productList;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public HolderProductList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_product, parent, false);

        return new HolderProductList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductList holder, int position) {

        ProductModel model = productList.get(position);
        String name = model.getProductName();
        String price = model.getProductPrice();
        String productId = model.getProductId();
        String timestamp = model.getTimestamp();
        String producticon = model.getProductIcon();

        holder.productNametxt.setText(name);
        holder.productPricetxt.setText(price);
        try {
            Picasso.get().load(producticon).placeholder(R.drawable.ic_shopping_coloured).into(holder.productImage);

        } catch (Exception e) {
            holder.productImage.setImageResource(R.drawable.ic_shopping_coloured);
        }

//        holder.buyProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                showConfirmOrder(model);
//
//            }
//        });


    }

//    private void showConfirmOrder(ProductModel model) {
//
//        View view = LayoutInflater.from(context).inflate(R.layout.confirm_order_dialog, null);
//
//        CircleImageView productIv = view.findViewById(R.id.productIv);
//        TextView nameTv = view.findViewById(R.id.nameTv);
//        TextView priceTv = view.findViewById(R.id.priceTv);
//        TextView confirmOrder = view.findViewById(R.id.confirmOrderId);
//
//
//
//        // get data from model
//
//        String productName = model.getProductName();
//        String productPrice = model.getProductPrice();
//        String productId = model.getProductId();
//        String producttimestamp = model.getTimestamp();
//        String productimage = model.getProductIcon();
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setView(view);
//
//        try{
//            Picasso.get().load(productimage).placeholder(R.drawable.ic_shopping_coloured).into(productIv);
//
//        }catch (Exception e){
//
//            productIv.setImageResource(R.drawable.ic_shopping_coloured);
//
//        }
//        nameTv.setText(productName);
//        priceTv.setText(productPrice);
//
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        confirmOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                confirmOrder();
//            }
//        });
//
//
//
//    }

//    private void confirmOrder() {
//
//
//
//        dialog = new ProgressDialog(context);
//        dialog.setMessage("Placing Order");
//        dialog.setCanceledOnTouchOutside(false);
//
//        String timestamp = ""+System.currentTimeMillis();
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("orderId", ""+timestamp);
//        hashMap.put("orderTime", ""+timestamp);
//        hashMap.put("orderStatus", "InProgress");
//
//
//
//
//
//    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class HolderProductList extends RecyclerView.ViewHolder {
        private CircleImageView productImage;
        private TextView productNametxt, productPricetxt;
        private ImageView buyProduct;

        public HolderProductList(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.singleProductImageId);
            productNametxt = itemView.findViewById(R.id.productNameId);
            productPricetxt = itemView.findViewById(R.id.productPriceId);
            buyProduct = itemView.findViewById(R.id.btnBuyId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickInterface.onItemClick(getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
