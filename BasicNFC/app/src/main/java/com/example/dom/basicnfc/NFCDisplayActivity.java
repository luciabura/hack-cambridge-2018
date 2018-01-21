package com.example.dom.basicnfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by mshrestha on 7/23/2014.
 */
public class NFCDisplayActivity extends Activity {
    protected TextView total_text;

    /* This is where the JSON object actually gets sent (I think) */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
    }

    /* This is where the JSON object gets received and the receiving table updated */
    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            /* Receive and unpackage the raw message string. */
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) rawMessages[0];
            String recvd_string = new String(message.getRecords()[0].getPayload());
            //Log.w("Message received", recvd_string);

            /* Load the received message string into a JSON object and then load the JSON object into
            the list of models to populate the table list. */
            try {
                JSONObject menu = new JSONObject(recvd_string); //the JSON object we load the string into
                //Log.w("JSON output received and loaded", menu.toString());
                JSONArray foodarray= menu.getJSONArray("food");
                JSONArray pricearray = menu.getJSONArray("price");
                //String ccnum = menu.getString("ccnum");
                Model[] menu_models = new Model[foodarray.length()]; //initialize the array of models
                /* Load the array of models to be loaded into the table. */
                double totalPrice = 0;
                for(int i = 0; i<foodarray.length(); i++){
                    String food = (String) foodarray.get(i);
                    Double price = (Double) pricearray.get(i);
                    totalPrice += price;
                    menu_models[i] = new Model(food, price);
                    Log.w("test name", menu_models[i].name);
                    Log.w("test price", Double.toString(menu_models[i].price));
                }
                populateList(menu_models);
                updateTotal(totalPrice);
            }
            catch (JSONException e) {}
        }
    }

    public void populateList(Model[] menuItems) {

        ListView listView = findViewById(R.id.recipe_list_view);
        CustomAdapter adapter = new CustomAdapter(this, menuItems, this);
        listView.setAdapter(adapter); //FIX THIS LINE
        total_text = findViewById(R.id.total);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(NFCDisplayActivity.this).create();
                alertDialog.setTitle("Would you like to add a tip?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add selected amount",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void updateTotal(double totalPrice) {
        if (totalPrice < 0) {
            totalPrice = 0;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        total_text.setText("Total: £" + df.format(totalPrice));
    }

}
