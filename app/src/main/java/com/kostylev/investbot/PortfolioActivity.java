package com.kostylev.investbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.kostylev.investbot.adapters.OrderItemAdapter;
import com.kostylev.investbot.adapters.PortfolioItemAdapter;
import com.kostylev.investbot.adapters.ShareItemAdapter;
import com.kostylev.investbot.helpers.RecyclerItemClickListener;
import com.kostylev.investbot.helpers.Token;
import com.kostylev.investbot.models.OrderItem;
import com.kostylev.investbot.models.PortfolioItem;
import com.kostylev.investbot.models.ShareItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import ru.tinkoff.piapi.contract.v1.Currency;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PortfolioPosition;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.SandboxService;

public class PortfolioActivity extends AppCompatActivity {

    private final int updateSharesIntervalMs = 300000; // 5 min
    private final boolean allowAutoUpdateShares = true;

    private final int updateRobotIntervalMs = 60000; // 1 min
    private boolean allowAutoUpdateRobot = false;

    private SandboxService sandboxService;
    private InstrumentsService instrumentsService;
    private MarketDataService marketDataService;

    private String accountId;
    private TextView textViewId, textViewOrdersEmpty, textViewPortfolioEmpty, textViewCurrencies, textViewShares, textViewLog;
    private LinearLayout linearLayoutPortfolio, linearLayoutShares, linearLayoutOrders, linearLayoutRobot;
    private TextInputEditText editTextPercent;
    private Button buttonRobot;

    private RecyclerView recyclerViewPortfolioItems;
    private RecyclerView.LayoutManager layoutManagerPortfolioItems;
    private PortfolioItemAdapter portfolioItemAdapter;
    private ArrayList<PortfolioItem> portfolioItems = new ArrayList<>();

    private RecyclerView recyclerViewShareItems;
    private RecyclerView.LayoutManager layoutManagerShareItems;
    private ShareItemAdapter shareItemAdapter;
    private ArrayList<ShareItem> shareItems = new ArrayList<>();

    private RecyclerView recyclerViewOrderItems;
    private RecyclerView.LayoutManager layoutManagerOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        InvestApi api = InvestApi.createSandbox(Token.getToken());
        sandboxService = api.getSandboxService();
        instrumentsService = api.getInstrumentsService();
        marketDataService = api.getMarketDataService();

        textViewId = findViewById(R.id.text_view_id);
        textViewOrdersEmpty = findViewById(R.id.text_view_orders_empty);
        textViewPortfolioEmpty = findViewById(R.id.text_view_portfolio_empty);
        textViewCurrencies = findViewById(R.id.text_view_currencies);
        textViewShares = findViewById(R.id.text_view_shares);
        textViewLog = findViewById(R.id.text_view_log);
        buttonRobot = findViewById(R.id.button_robot);
        editTextPercent = findViewById(R.id.edit_text_percent);
        linearLayoutPortfolio = findViewById(R.id.linear_layout_portfolio);
        linearLayoutShares = findViewById(R.id.linear_layout_shares);
        linearLayoutOrders = findViewById(R.id.linear_layout_orders);
        linearLayoutRobot = findViewById(R.id.linear_layout_robot);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_portfolio:
                        linearLayoutPortfolio.setVisibility(View.VISIBLE);
                        linearLayoutShares.setVisibility(View.GONE);
                        linearLayoutOrders.setVisibility(View.GONE);
                        linearLayoutRobot.setVisibility(View.GONE);
                        getAccountPortfolioSandbox();
                        break;
                    case R.id.page_shares:
                        linearLayoutPortfolio.setVisibility(View.GONE);
                        linearLayoutShares.setVisibility(View.VISIBLE);
                        linearLayoutOrders.setVisibility(View.GONE);
                        linearLayoutRobot.setVisibility(View.GONE);
                        if(shareItems.isEmpty()){
                            getShares();
                        }
                        break;
                    case R.id.page_orders:
                        linearLayoutPortfolio.setVisibility(View.GONE);
                        linearLayoutShares.setVisibility(View.GONE);
                        linearLayoutOrders.setVisibility(View.VISIBLE);
                        linearLayoutRobot.setVisibility(View.GONE);
                        getOrdersSandbox();
                        break;
                    case R.id.page_robot:
                        linearLayoutPortfolio.setVisibility(View.GONE);
                        linearLayoutShares.setVisibility(View.GONE);
                        linearLayoutOrders.setVisibility(View.GONE);
                        linearLayoutRobot.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getString("account_id");
        }

        if(accountId != null && !accountId.isEmpty()){
            textViewId.setText(accountId);
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            linearLayoutPortfolio.setVisibility(View.GONE);
            linearLayoutShares.setVisibility(View.GONE);
            linearLayoutOrders.setVisibility(View.GONE);
            showDialog("?????????????????? ???????????? ?????? ?????????????????? ???????????????????????????? ???????????????? (??????????).");
        }

        recyclerViewPortfolioItems = findViewById(R.id.recycler_view_portfolio_items);
        layoutManagerPortfolioItems = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewPortfolioItems.setLayoutManager(layoutManagerPortfolioItems);
        getAccountPortfolioSandbox();

        recyclerViewShareItems = findViewById(R.id.recycler_view_share_items);
        layoutManagerShareItems = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewShareItems.setLayoutManager(layoutManagerShareItems);

        recyclerViewOrderItems = findViewById(R.id.recycler_view_order_items);
        layoutManagerOrderItems = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewOrderItems.setLayoutManager(layoutManagerOrderItems);
        getOrdersSandbox();

        recyclerViewShareItems.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewShareItems, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView textViewFigi = view.findViewById(R.id.text_view_figi);
                        TextView textViewNominal = view.findViewById(R.id.text_view_nominal);
                        if(!textViewFigi.getText().toString().isEmpty() && textViewFigi.getText().toString().length() != 0){
                            Intent intent = new Intent(getApplicationContext(), PostOrderActivity.class);
                            intent.putExtra("account_id", accountId);
                            intent.putExtra("figi", textViewFigi.getText().toString());

                            long units = 0;
                            int nano = 0;

                            String nominal = textViewNominal.getText().toString();

                            if(nominal.contains(".")){
                                String[] value = nominal.split(Pattern.quote("."));
                                units = Long.parseLong(value[0].replaceAll("\\D+",""));
                                nano = Integer.parseInt(value[1].replaceAll("\\D+",""));
                            } else {
                                units = Long.parseLong(nominal);
                                nano = 0;
                            }

                            if(units > 0 && nano >= 0){
                                intent.putExtra("units", units);
                                intent.putExtra("nano", nano);
                            } else {
                                units = 0;
                                nano = 0;
                            }

                            intent.putExtra("is_buy", true);
                            startActivity(intent);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        recyclerViewPortfolioItems.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerViewShareItems, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView textViewFigi = view.findViewById(R.id.text_view_figi);
                        TextView textViewAveragePositionPrice = view.findViewById(R.id.text_view_average_position_price);
                        TextView textViewQuantityLots = view.findViewById(R.id.text_view_quantity_lots);
                        if(!textViewFigi.getText().toString().isEmpty() && textViewFigi.getText().toString().length() != 0){
                            Intent intent = new Intent(getApplicationContext(), PostOrderActivity.class);
                            intent.putExtra("account_id", accountId);
                            intent.putExtra("figi", textViewFigi.getText().toString());
                            intent.putExtra("quantity_max", Integer.parseInt(textViewQuantityLots.getText().toString()));

                            long units = 0;
                            int nano = 0;

                            String positionPrice = textViewAveragePositionPrice.getText().toString();

                            if(positionPrice.contains(".")){
                                String[] value = positionPrice.split(Pattern.quote("."));
                                units = Long.parseLong(value[0].replaceAll("\\D+",""));
                                nano = Integer.parseInt(value[1].replaceAll("\\D+",""));
                            } else {
                                units = Long.parseLong(positionPrice);
                                nano = 0;
                            }

                            if(units > 0 && nano >= 0){
                                intent.putExtra("units", units);
                                intent.putExtra("nano", nano);
                            } else {
                                units = 0;
                                nano = 0;
                            }

                            intent.putExtra("is_buy", false);
                            startActivity(intent);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        buttonRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allowAutoUpdateRobot){
                    allowAutoUpdateRobot = true;
                    workRobot();
                } else {
                    allowAutoUpdateRobot = false;
                    buttonRobot.setText("???????????????? ????????????");
                }
            }
        });
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("????????????????????");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getAccountPortfolioSandbox(){
        portfolioItems.clear();
        List<PortfolioPosition> portfolioPositions = null;
        try {
            portfolioPositions = sandboxService.getPortfolio(accountId).get().getPositionsList();
        } catch (Exception e){
        }

        MoneyValue totalAmountCurrencies = sandboxService.getPortfolioSync(accountId).getTotalAmountCurrencies();
        MoneyValue totalAmountShares = sandboxService.getPortfolioSync(accountId).getTotalAmountShares();

        textViewCurrencies.setText("??????-???? ????????????: " + totalAmountCurrencies.getUnits() + "." + totalAmountCurrencies.getNano() + " " + totalAmountCurrencies.getCurrency().toUpperCase());
        textViewShares.setText("??????-???? ??????????: " + totalAmountShares.getUnits() + "." + totalAmountShares.getNano() + " " + totalAmountShares.getCurrency().toUpperCase());

        if(!portfolioPositions.isEmpty()){
            for (PortfolioPosition portfolioPosition : portfolioPositions) {
                if(!portfolioPosition.getFigi().equals("FG0000000000")){
                    String instrumentFigi = portfolioPosition.getFigi();
                    String instrumentType = portfolioPosition.getInstrumentType();
                    String instrumentName = instrumentFigi;

                    switch (instrumentType) {
                        case "share":
                            instrumentType = "??????????";
                            Share share = instrumentsService.getShareByFigiSync(instrumentFigi);
                            instrumentName = share.getName() + " (" + share.getTicker() + ")";
                            break;
                        case "currency":
                            instrumentType = "????????????";
                            Currency currency = instrumentsService.getCurrencyByFigiSync(instrumentFigi);
                            instrumentName = currency.getName();
                            break;
                    }

                    portfolioItems.add(new PortfolioItem (instrumentFigi, instrumentType, instrumentName, portfolioPosition.getAveragePositionPrice().getUnits() + "." + portfolioPosition.getAveragePositionPrice().getNano() + " " + portfolioPosition.getAveragePositionPrice().getCurrency().toUpperCase(), Long.toString(portfolioPosition.getQuantityLots().getUnits())));
                }
            }
        }

        portfolioItemAdapter = new PortfolioItemAdapter(this, portfolioItems);
        recyclerViewPortfolioItems.setAdapter(portfolioItemAdapter);

        if(portfolioItems.isEmpty()){
            textViewPortfolioEmpty.setVisibility(View.VISIBLE);
            recyclerViewPortfolioItems.setVisibility(View.GONE);
        } else {
            textViewPortfolioEmpty.setVisibility(View.GONE);
            recyclerViewPortfolioItems.setVisibility(View.VISIBLE);
        }
    }

    public void payInAccountSandbox(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setMaxLines(1);
        editText.setSingleLine(true);

        alert.setView(editText);
        alert.setMessage("?????????????? ?????????? ???????????????????? ?????????? (?????????? ??????????):");
        alert.setTitle("???????????????????? ??????????");

        alert.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String input = editText.getText().toString();
                if (input.length() != 0 && input != null && !input.isEmpty() && input.matches("\\d+")) {
                    int units = Integer.parseInt(input);
                    String currency = "rub";
                    MoneyValue money = MoneyValue.newBuilder().setCurrency(currency).setUnits(units).setNano(0).build();
                    sandboxService.payInSync(accountId, money);
                    showDialog("?????????????? ?????????????????? ???????????? ???? ???????????????????? ???? ?????????? " + units + " " + currency.toUpperCase());
                    getAccountPortfolioSandbox();
                } else {
                    showDialog("???????? ?????????? ???????????? ?????????????????????????????? ?????????????????? ????????????????:\n\n- ???? ?????????? ???????? ????????????;\n- ???? ???????????? ?????????????????? ???????? ?? ???????????? ????????????????;\n- ?????????????????? ???????????? ?????????? ??????????.");
                }
            }
        });

        alert.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void autoUpdateShares(){
        if(allowAutoUpdateShares){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getShares();
                }
            }, updateSharesIntervalMs);
        }
    }

    private Quotation getLastPrice(String figi){
        return marketDataService.getLastPricesSync(Collections.singleton(figi)).get(0).getPrice();
    }

    private void getShares(){
        shareItems.clear();
        List<Share> shares = instrumentsService.getAllSharesSync();

        if(shares.isEmpty()){
            showDialog("???? ?????????????? ???????????????? ???????????? ?????????? ???? ??????????.");
        } else {
            Quotation quotation;

            String[] figis = {"BBG005DXJS36", "BBG006L8G4H1", "BBG004730N88", "BBG004S682Z6", "BBG004RVFCY3", "BBG004730RP0", "BBG004731354", "BBG00178PGX3", "BBG000B9XRY4", "BBG000N9MNX3", "BBG000CL9VN6", "BBG000BPH459", "BBG000BVPV84", "BBG009S39JX6", "BBG000H6HNW3", "BBG000BMX289", "BBG000BH4R78",
            "BBG0077VNXV6", "BBG000MM2P62", "BBG000BNSZP1"};
            for (String figi : figis) {
                Share share = instrumentsService.getShareByFigiSync(figi);
                quotation = getLastPrice(figi);
                shareItems.add(new ShareItem (figi, share.getTicker(), share.getName(),
                        quotation.getUnits() + "." + quotation.getNano(), share.getNominal().getCurrency().toUpperCase(),
                        share.getSector()));
            }

            for (int i = 0; i <= 10; i++) {
                String figi = shares.get(i).getFigi();
                quotation = getLastPrice(figi);

                shareItems.add(new ShareItem (figi, shares.get(i).getTicker(), shares.get(i).getName(),
                        quotation.getUnits() + "." + quotation.getNano(), shares.get(i).getNominal().getCurrency().toUpperCase(),
                        shares.get(i).getSector()));
            }
        }

        shareItemAdapter = new ShareItemAdapter(this, shareItems);
        recyclerViewShareItems.setAdapter(shareItemAdapter);

        autoUpdateShares();
    }

    private void getOrdersSandbox(){
        orderItems.clear();
        List<OrderState> orders = sandboxService.getOrdersSync(accountId);

        if(orders.isEmpty()){
            textViewOrdersEmpty.setVisibility(View.VISIBLE);
            recyclerViewOrderItems.setVisibility(View.GONE);
        } else {
            textViewOrdersEmpty.setVisibility(View.GONE);
            recyclerViewOrderItems.setVisibility(View.VISIBLE);
            for(OrderState order : orders){

                orderItems.add(new OrderItem(order.getOrderId(), order.getFigi(), order.getDirection().toString(), order.getOrderType().toString(), order.getExecutionReportStatus().toString(), order.getInitialOrderPrice().getUnits() + "." + order.getInitialOrderPrice().getNano() + " " + order.getInitialOrderPrice().getCurrency()));
            }
        }

        orderItemAdapter = new OrderItemAdapter(this, orderItems);
        recyclerViewOrderItems.setAdapter(orderItemAdapter);
    }

    private void addLog(String text){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        textViewLog.setText(timeStamp + "\n" + text+ "\n\n" + textViewLog.getText());
    }

    private void autoUpdateRobot(){
        if(allowAutoUpdateRobot){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    workRobot();
                }
            }, updateRobotIntervalMs);
        } else {
            addLog("????????????????????. ?????????? ?????????????????? ????????????.");
        }
    }

    private void workRobot(){
        buttonRobot.setText("?????????????????? ????????????");
        addLog("????????????????????. ?????????? ?????????? ????????????.");
        if(editTextPercent.getText().toString().length() != 0 && !editTextPercent.getText().toString().isEmpty()){
            int percent = Integer.parseInt(editTextPercent.getText().toString().replaceAll("\\D+",""));
            if(percent > 0){
                getAccountPortfolioSandbox();
                if(!portfolioItems.isEmpty()){
                    ArrayList<PortfolioItem> shares = new ArrayList<>();

                    for(PortfolioItem portfolioItem : portfolioItems){
                        if(Objects.equals(portfolioItem.getInstrumentType(), "??????????")){
                            shares.add(portfolioItem);
                        }
                    }

                    if(!shares.isEmpty()){
                        addLog("????????????????????. ?????????? ?????????? ?? ???????????????? ?? ?????????????? " + shares.size() + " ????????.");
                        for(PortfolioItem share : shares){
                            String shareFigi = share.getFigi();
                            long priceBuy = Long.parseLong(share.getAveragePositionPrice().split(Pattern.quote("."))[0]);
                            long priceNow = Long.parseLong(String.valueOf(getLastPrice(shareFigi).getUnits()));

                            if(priceNow > 0){
                                addLog("????????????????????. ?????????????? ?????????????? ???????? ?????????? (" + share.getName() + "): " + priceNow + ".");
                                if(priceBuy > 0){
                                    addLog("????????????????????. ?????????????? ???????? ?????????????? ?????????? (" + share.getName() + "): " + priceBuy + ".");
                                    long priceBuyWithPercent = priceBuy + ((priceBuy * percent) / 100);

                                    if(priceNow >= priceBuyWithPercent){
                                        PostOrderResponse postOrderResponse = sandboxService.postOrderSync(shareFigi, Long.parseLong(share.getQuantityLots()), Quotation.newBuilder().setUnits(priceNow).setNano(getLastPrice(shareFigi).getNano()).build(),
                                                OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, "");
                                        if(postOrderResponse.getFigi().length() != 0){
                                            addLog("??????????. ???????????????????? ???????????? ???? ?????????????? ?????????? " + share.getName() + " (figi: " + shareFigi + ", ????????????????????: " + Long.parseLong(share.getQuantityLots()) + ", ???????? ??????????????: " + priceBuy + ", ?????????????? ????????: " + priceNow + ", ?????????????????????????? ?????????? ???? ??????????????: " + (priceNow - priceBuy) + ").");
                                        } else {
                                            addLog("????????????. ??????-???? ?????????? ???? ?????? ?????? ???????????????? ?????????????? ???? ?????????????? ??????????" + share.getName() + " (figi: " + shareFigi + ").");
                                        }
                                        allowAutoUpdateRobot = true;
                                    } else {
                                        allowAutoUpdateRobot = true;
                                        addLog("????????????????????. ???????????? ???? ?????????????? ?????????? " + share.getName() + " (figi: " + shareFigi + ") ???? ???????? ????????????????????, ?????? ?????? ?????????????? ???????? ???? ???????????????? ?????? ??????????????.");
                                    }
                                } else {
                                    allowAutoUpdateRobot = false;
                                    addLog("????????????. ???????? ?????????????? ?????????? " + share.getName() + " (figi: " + shareFigi + ") ???????????? ???????? ???????????? ????????.");
                                }
                            } else {
                                allowAutoUpdateRobot = false;
                                addLog("????????????. ?????????????? ???????? ?????????? " + share.getName() + " (figi: " + shareFigi + ") ???????????? ???????? ???????????? ????????.");
                            }
                        }

                    } else {
                        allowAutoUpdateRobot = false;
                        addLog("????????????????????. ?? ???????????????? ?????? ??????????.");
                    }

                } else {
                    allowAutoUpdateRobot = false;
                    addLog("????????????????????. ???????????????? ????????.");
                }
            } else {
                allowAutoUpdateRobot = false;
                addLog("????????????????????. ???????????????? ?? ???????? \"??????????????\" ???????????? ???????? ???????????? ????????.");
                showDialog("???????????????? ?? ???????? \"??????????????\" ???????????? ???????? ???????????? ????????.");
            }
        } else {
            allowAutoUpdateRobot = false;
            addLog("????????????????????. ???????? \"??????????????\" ???? ???????????? ???????? ????????????.");
            showDialog("???????? \"??????????????\" ???? ???????????? ???????? ????????????.");
        }

        if(allowAutoUpdateRobot){
            buttonRobot.setText("?????????????????? ????????????");
        } else {
            buttonRobot.setText("???????????????? ????????????");
        }

        autoUpdateRobot();
    }

    public void showHelp(View view){
        showDialog("?????????? ?? ???????????????????????????? ???????????? ???????????????? ???????????? ?? ????????????????, ?? ?????????? ???????????????????????? ???????????? ???? ?????????????? ?????????? ???? ??????????.\n\n???? ?????????? ???????????? ?????????? ???????????????????? ???????????? ???????? ???????????????? ?? ??????.");
    }
}
