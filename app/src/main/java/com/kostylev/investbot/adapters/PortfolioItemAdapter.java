package com.kostylev.investbot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kostylev.investbot.R;
import com.kostylev.investbot.models.PortfolioItem;

import java.util.List;

public class PortfolioItemAdapter extends RecyclerView.Adapter<PortfolioItemAdapter.ViewHolder>{

    private final LayoutInflater layoutInflater;
    private final List<PortfolioItem> portfolioItems;

    public PortfolioItemAdapter(Context context, List<PortfolioItem> portfolioItems) {
        this.portfolioItems = portfolioItems;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PortfolioItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_portfolio, parent, false);
        return new PortfolioItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PortfolioItemAdapter.ViewHolder holder, int position) {
        PortfolioItem portfolioItem = portfolioItems.get(position);
        holder.textViewFigi.setText(portfolioItem.getFigi());
        holder.textViewInstrumentType.setText(portfolioItem.getInstrumentType());
        holder.textViewName.setText(portfolioItem.getName());
        holder.textViewAveragePositionPrice.setText(portfolioItem.getAveragePositionPrice());
        holder.textViewQuantityLots.setText(portfolioItem.getQuantityLots());
    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewFigi, textViewInstrumentType, textViewName, textViewAveragePositionPrice, textViewQuantityLots;
        ViewHolder(View view){
            super(view);
            textViewFigi = view.findViewById(R.id.text_view_figi);
            textViewInstrumentType = view.findViewById(R.id.text_view_instrument_type);
            textViewName = view.findViewById(R.id.text_view_name);
            textViewAveragePositionPrice = view.findViewById(R.id.text_view_average_position_price);
            textViewQuantityLots = view.findViewById(R.id.text_view_quantity_lots);
        }
    }
}