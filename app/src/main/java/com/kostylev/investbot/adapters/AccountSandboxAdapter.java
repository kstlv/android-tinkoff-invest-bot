package com.kostylev.investbot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kostylev.investbot.R;
import com.kostylev.investbot.models.AccountSandbox;

import java.util.List;

public class AccountSandboxAdapter extends RecyclerView.Adapter<AccountSandboxAdapter.ViewHolder>{

    private final LayoutInflater layoutInflater;
    private final List<AccountSandbox> accountsSandbox;

    public AccountSandboxAdapter(Context context, List<AccountSandbox> accountsSandbox) {
        this.accountsSandbox = accountsSandbox;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public AccountSandboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_account_sandbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountSandboxAdapter.ViewHolder holder, int position) {
        AccountSandbox accountSandbox = accountsSandbox.get(position);
        holder.textViewId.setText(accountSandbox.getId());
        holder.textViewAccessLevel.setText(accountSandbox.getAccessLevel());
        holder.textViewOpenedDate.setText(accountSandbox.getOpenedDate());
        holder.textViewTotalAmountCurrencies.setText(accountSandbox.getTotalAmountCurrencies());
        holder.textViewTotalAmountShares.setText(accountSandbox.getTotalAmountShares());
    }

    @Override
    public int getItemCount() {
        return accountsSandbox.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewId, textViewAccessLevel, textViewOpenedDate, textViewTotalAmountCurrencies, textViewTotalAmountShares;
        ViewHolder(View view){
            super(view);
            textViewId = view.findViewById(R.id.text_view_id);
            textViewAccessLevel = view.findViewById(R.id.text_view_access_level);
            textViewOpenedDate = view.findViewById(R.id.text_view_opened_date);
            textViewTotalAmountCurrencies = view.findViewById(R.id.text_view_total_amount_currencies);
            textViewTotalAmountShares = view.findViewById(R.id.text_view_total_amount_shares);
        }
    }
}
