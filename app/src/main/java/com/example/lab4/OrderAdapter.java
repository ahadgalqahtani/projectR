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

    // Constructor accepts List<OrderData>
    public OrderAdapter(List<OrderData> orderList) {
        this.orderList = orderList;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(OrderData order);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
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
        holder.bind(order, longClickListener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textOrderId, textDeliveryDate, textCustomerDetails, textAssignedDriver, textCity, textOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textDeliveryDate = itemView.findViewById(R.id.textDeliveryDate);
            textCustomerDetails = itemView.findViewById(R.id.textCustomerDetails);
            textAssignedDriver = itemView.findViewById(R.id.textAssignedDriver);
            textCity = itemView.findViewById(R.id.textCity);
            textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
        }

        public void bind(OrderData order, OnItemLongClickListener longClickListener) {
            // Safeguard against null values in the OrderData fields
            String orderId = order.getOrderId() != null ? order.getOrderId() : "Unknown";
            String deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate() : "Not Set";
            String customerDetails = order.getCustomerDetails() != null ? order.getCustomerDetails() : "No Customer Info";
            String assignedDriver = order.getAssignedDriver() != null ? order.getAssignedDriver() : "Not Assigned";
            String city = order.getCity() != null ? order.getCity() : "Unknown";

            // Setting text views
            textOrderId.setText("Order ID: " + orderId);
            textDeliveryDate.setText("Delivery Date: " + deliveryDate);
            textCustomerDetails.setText("Customer: " + customerDetails);
            textAssignedDriver.setText("Driver: " + assignedDriver);
            textCity.setText("City: " + city);

            // Color coding for order status
            String status = order.getStatus() != null ? order.getStatus() : "Unknown";
            switch (status) {
                case "Pending":
                    textOrderStatus.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_orange_light));
                    break;
                case "Completed":
                    textOrderStatus.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_green_light));
                    break;
                default:
                    textOrderStatus.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_red_light));
                    break;
            }

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