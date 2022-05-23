package com.kostylev.investbot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.SandboxService;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.protobuf.Timestamp;
import com.kostylev.investbot.adapters.AccountSandboxAdapter;
import com.kostylev.investbot.helpers.RecyclerItemClickListener;
import com.kostylev.investbot.helpers.Token;
import com.kostylev.investbot.models.AccountSandbox;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;

public class MainActivity extends AppCompatActivity {

    private SandboxService sandboxService;

    private RecyclerView recyclerViewAccountsSandbox;
    private RecyclerView.LayoutManager layoutManagerAccountsSandbox;
    private AccountSandboxAdapter accountSandboxAdapter;
    private ArrayList<AccountSandbox> accountsSandbox = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InvestApi api = InvestApi.createSandbox(Token.getToken());
        sandboxService = api.getSandboxService();

        final ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.extended_floating_action_button);
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccountSandbox();
            }
        });

        recyclerViewAccountsSandbox = findViewById(R.id.recycler_view_accounts_sandbox);
        layoutManagerAccountsSandbox = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewAccountsSandbox.setLayoutManager(layoutManagerAccountsSandbox);
        recyclerViewAccountsSandbox.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewAccountsSandbox, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView textViewId = view.findViewById(R.id.text_view_id);

                        Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                        intent.putExtra("account_id", textViewId.getText().toString());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        TextView textViewId = view.findViewById(R.id.text_view_id);

                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage("Вы уверены, что хотите закрыть аккаунт?");
                        alert.setTitle("Закрыть аккаунт?");

                        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                closeAccountSandbox(textViewId.getText().toString());
                            }
                        });

                        alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                        alert.show();
                    }
                })
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        getAccountsSandbox();
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Информация");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static String convertedDate(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()).atZone(ZoneId.of("America/Montreal")).toLocalDate().toString();
    }

    private void openAccountSandbox(){
        showDialog("Открыт новый счёт (песочница): " + sandboxService.openAccountSync());
        getAccountsSandbox();
    }

    private void closeAccountSandbox(String accountId){
        if(accountId != null && !accountId.isEmpty()){
            sandboxService.closeAccountSync(accountId);
            showDialog("Счёт закрыт (песочница): " + accountId);
            getAccountsSandbox();
        } else {
            showDialog("Не удалось закрыть счёт, так как идентификатор аккаунта (счёта) не может быть пустым.");
        }
    }

    private void getAccountsSandbox() {
        accountsSandbox.clear();

        List<Account> accounts = sandboxService.getAccountsSync();
        if(accounts.isEmpty()){
            showDialog("Нет аккаунтов в песочнице. Вы можете открыть новый аккаунт.");
        } else {
            for (Account account : accounts) {
                String id = account.getId();

                String accessLevel = "Не определён";
                switch (account.getAccessLevel().getNumber()) {
                    case 1:
                        accessLevel = "Полный доступ";
                        break;
                    case 2:
                        accessLevel = "Только чтение";
                        break;
                    case 3:
                        accessLevel = "Доступ отсутствует";
                        break;
                }

                PortfolioResponse portfolio = sandboxService.getPortfolioSync(id);
                MoneyValue totalAmountCurrencies = sandboxService.getPortfolioSync(id).getTotalAmountCurrencies();
                MoneyValue totalAmountShares = portfolio.getTotalAmountShares();

                accountsSandbox.add(new AccountSandbox (id, accessLevel, convertedDate(account.getOpenedDate()), totalAmountCurrencies.getUnits() + "." + totalAmountCurrencies.getNano() + " " + totalAmountCurrencies.getCurrency(),  totalAmountShares.getUnits() + "." + totalAmountShares.getNano() + " " + totalAmountShares.getCurrency()));
            }
        }

        accountSandboxAdapter = new AccountSandboxAdapter(this, accountsSandbox);
        recyclerViewAccountsSandbox.setAdapter(accountSandboxAdapter);
    }
}