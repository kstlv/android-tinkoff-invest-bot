package com.kostylev.investbot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kostylev.investbot.helpers.Token;

import java.util.regex.Pattern;

import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.SandboxService;

public class PostOrderActivity extends AppCompatActivity {

    private String figi, accountId;
    private long units;
    private int nano;
    private int quantity;
    private boolean isBuy = true;
    private int quantityMax = 0;

    private SandboxService sandboxService;

    private TextInputEditText editTextFigi, editTextQuantity, editTextPrice;
    private OrderDirection orderDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_order);

        editTextFigi = findViewById(R.id.edit_text_figi);
        editTextQuantity = findViewById(R.id.edit_text_quantity);
        editTextPrice = findViewById(R.id.edit_text_price);

        InvestApi api = InvestApi.createSandbox(Token.getToken());
        sandboxService = api.getSandboxService();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getString("account_id");
            figi = extras.getString("figi");
            units = extras.getLong("units");
            nano = extras.getInt("nano");
            isBuy = extras.getBoolean("is_buy");
            quantityMax = extras.getInt("quantity_max");
        }

        if(figi != null && !figi.isEmpty()){
            editTextFigi.setText(figi);
        }

        if(units > 0 && nano >= 0){
            editTextPrice.setText(units + "." + nano);
        }

        if(!isBuy && quantityMax > 0){
            editTextQuantity.setText(String.valueOf(quantityMax));
        }

        final ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.extended_floating_action_button);
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextFigi.getText().toString().length() != 0){
                    if(editTextQuantity.getText().toString().length() != 0){
                        quantity = Integer.parseInt(editTextQuantity.getText().toString().replace(".", "").replace(",", ""));
                        if(quantity > 0){
                            boolean check = false;
                            if(!isBuy){
                                if(quantity <= quantityMax){
                                    check = true;
                                } else {
                                    check = false;
                                    showDialog("???????????????? ?? ???????? \"????????????????????\" ???? ???????????? ?????????????????? ???????????????????? ?????????? ?? ????????????????.");
                                }
                            } else {
                                check = true;
                            }

                            if(check){
                                if(editTextPrice.getText().toString().length() != 0){
                                    String price = editTextPrice.getText().toString();
                                    if(price.contains(",")){
                                        price.replace(",", ".");
                                    }

                                    if(price.contains(".")){
                                        String[] value = price.split(Pattern.quote("."));
                                        units = Integer.parseInt(value[0]);
                                        nano = Integer.parseInt(value[1]);
                                    } else {
                                        units = Integer.parseInt(price);
                                        nano = 0;
                                    }

                                    if(units > 0 && nano >= 0){
                                        figi = editTextFigi.getText().toString();

                                        String orderDirectionText;
                                        if(isBuy){
                                            orderDirection = OrderDirection.ORDER_DIRECTION_BUY;
                                            orderDirectionText = "??????????????";
                                        } else {
                                            orderDirection = OrderDirection.ORDER_DIRECTION_SELL;
                                            orderDirectionText = "??????????????";
                                        }

                                        AlertDialog.Builder alert = new AlertDialog.Builder(PostOrderActivity.this);
                                        alert.setMessage("???? ??????????????, ?????? ???????????? ?????????????????? ?????????????\n\n??????????????????????: " + orderDirectionText + "\n???????????????????? (figi): " + figi + "\n????????????????????: " + quantity + "\n????????: " + units + "." + nano);
                                        alert.setTitle("?????????????????? ?????????????");

                                        alert.setPositiveButton("????", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                PostOrderResponse postOrderResponse = sandboxService.postOrderSync(figi, quantity, Quotation.newBuilder().setUnits(units).setNano(nano).build(),
                                                        orderDirection, accountId, OrderType.ORDER_TYPE_MARKET, "");
                                                if(postOrderResponse.getFigi().length() != 0){
                                                    finish();
                                                } else {
                                                    showDialog("??????-???? ?????????? ???? ?????? ?????? ???????????????? ??????????????.");
                                                }
                                            }
                                        });

                                        alert.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });

                                        alert.show();
                                    } else {
                                        showDialog("???????????????? ?? ???????? \"????????\" ???????????? ???????? ???????????? ????????.");
                                    }
                                } else {
                                    showDialog("???????? \"????????\" ???????????? ???????? ??????????????????.");
                                }
                            }
                        } else {
                            showDialog("???????????????? ?? ???????? \"????????????????????\" ???????????? ???????? ???????????? ????????.");
                        }
                    } else {
                        showDialog("???????? \"????????????????????\" ???????????? ???????? ??????????????????.");
                    }
                } else {
                    showDialog("???????? \"figi\" ???????????? ???????? ??????????????????.");
                }
            }
        });

        if(isBuy){
            extendedFloatingActionButton.setText("????????????");
            this.setTitle("?????????????? ???????????? ????????????");
        } else {
            extendedFloatingActionButton.setText("??????????????");
            this.setTitle("?????????????? ???????????? ????????????");
        }
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("????????????????????");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}