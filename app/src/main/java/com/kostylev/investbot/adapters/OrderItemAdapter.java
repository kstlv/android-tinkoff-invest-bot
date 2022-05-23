package com.kostylev.investbot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kostylev.investbot.R;
import com.kostylev.investbot.models.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{

    private final LayoutInflater layoutInflater;
    private final List<OrderItem> orderItems;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_order, parent, false);
        return new OrderItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.textViewId.setText(orderItem.getId());
        holder.textViewFigi.setText(orderItem.getFigi());
        holder.textViewDirection.setText(orderItem.getDirection());
        holder.textViewType.setText(orderItem.getType());
        holder.textViewStatus.setText(orderItem.getStatus());
        holder.textViewInitialOrderPrice.setText(orderItem.getInitialOrderPrice());

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewId, textViewFigi, textViewDirection, textViewType, textViewStatus, textViewInitialOrderPrice;
        ViewHolder(View view){
            super(view);
            textViewId = view.findViewById(R.id.text_view_id);
            textViewFigi = view.findViewById(R.id.text_view_figi);
            textViewDirection = view.findViewById(R.id.text_view_direction);
            textViewType = view.findViewById(R.id.text_view_type);
            textViewStatus = view.findViewById(R.id.text_view_status);
            textViewInitialOrderPrice = view.findViewById(R.id.text_view_initial_order_price);
        }
    }
}