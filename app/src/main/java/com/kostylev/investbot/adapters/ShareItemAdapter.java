package com.kostylev.investbot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kostylev.investbot.R;
import com.kostylev.investbot.models.ShareItem;

import java.util.List;

public class ShareItemAdapter extends RecyclerView.Adapter<ShareItemAdapter.ViewHolder>{

    private final LayoutInflater layoutInflater;
    private final List<ShareItem> shareItems;

    public ShareItemAdapter(Context context, List<ShareItem> shareItems) {
        this.shareItems = shareItems;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ShareItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_share, parent, false);
        return new ShareItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShareItemAdapter.ViewHolder holder, int position) {
        ShareItem shareItem = shareItems.get(position);
        holder.textViewFigi.setText(shareItem.getFigi());
        holder.textViewTicker.setText(shareItem.getTicker());
        holder.textViewName.setText(shareItem.getName());
        holder.textViewNominal.setText(shareItem.getNominal());
        holder.textViewCurrency.setText(shareItem.getCurrency());
        holder.textViewSector.setText(shareItem.getSector());
    }

    @Override
    public int getItemCount() {
        return shareItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewFigi, textViewTicker, textViewName, textViewNominal, textViewCurrency, textViewSector;
        ViewHolder(View view){
            super(view);
            textViewFigi = view.findViewById(R.id.text_view_figi);
            textViewTicker = view.findViewById(R.id.text_view_ticker);
            textViewName = view.findViewById(R.id.text_view_name);
            textViewNominal = view.findViewById(R.id.text_view_nominal);
            textViewCurrency = view.findViewById(R.id.text_view_currency);
            textViewSector = view.findViewById(R.id.text_view_sector);
        }
    }
}