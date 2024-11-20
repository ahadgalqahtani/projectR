package com.example.lab4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<OrderData> orderList;
    private OnItemLongClickListener longClickListener;
    private OnItemClickListener clickListener; // New

    // Constructor accepts List<OrderData>
    public OrderAdapter(List<OrderData> orderList) {
        this.orderList = orderList;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(OrderData order);
    }

    public interface OnItemClickListener { // New
        void onItemClick(OrderData order);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) { // New
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderData order = orderList.get(position);
        holder.bind(order, clickListener, longClickListener); // Modified
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textOrderId, textDeliveryDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textDeliveryDate = itemView.findViewById(R.id.textDeliveryDate);
        }

        public void bind(OrderData order, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
            // Safeguard against null values in the OrderData fields
            String orderId = order.getOrderId() != null ? order.getOrderId() : "Unknown";
            String deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate() : "Not Set";

            // Setting text views for Order ID and Delivery Date
            textOrderId.setText("Order ID: " + orderId);
            textDeliveryDate.setText("Delivery Date: " + deliveryDate);

            // Regular item click listener
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(order);
                }
            });

            // Long press to delete
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(order);
                    return true;
                }
                return false;
            });
        }
    }
}