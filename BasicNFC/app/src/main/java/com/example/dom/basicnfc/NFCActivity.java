package com.example.dom.basicnfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.widget.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.text.DecimalFormat;
import android.view.View;



public class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    int ISHOST = 1; //set to 0 or 1 manually
    JSONObject menu = new JSONObject();

    protected TextView total_text;

    //CustomAdapter adapterRef;

    /* Gets called only when the app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Log.w("Entered create", intent.getAction());
        super.onCreate(savedInstanceState);
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show(); //a little popup window
        }

        mAdapter.setNdefPushMessageCallback(this, this);

        if(ISHOST == 1) {
            setContentView(R.layout.activity_nfc);
            //Initial JSON Object loaded onto the bill/menu
            try {
                JSONArray foodlist = new JSONArray();
                foodlist.put("Fish");
                foodlist.put("Bread");
                foodlist.put("Salad");
                foodlist.put("Burger");
                foodlist.put("Soda");
                foodlist.put("Brownie");
                foodlist.put("Ice Cream");
                foodlist.put("Steak");
                JSONArray pricelist = new JSONArray();
                pricelist.put(17.90);
                pricelist.put(8.87);
                pricelist.put(13.93);
                pricelist.put(5.20);
                pricelist.put(2.39);
                pricelist.put(13.03);
                pricelist.put(8.49);
                pricelist.put(26.38);
                menu.put("food", foodlist);
                menu.put("price", pricelist);
                menu.put("ccnum", JSONObject.NULL);

                //the code below is copied and pasted from the other .java file.
                JSONArray foodarray = menu.getJSONArray("food");
                JSONArray pricearray = menu.getJSONArray("price");
                //String ccnum = menu.getString("ccnum");
                Model[] menu_models = new Model[foodarray.length()]; //initialize the array of models
                    /* Load the array of models to be loaded into the table. */
                double totalPrice = 0;
                for (int i = 0; i < foodarray.length(); i++) {
                    String food = (String) foodarray.get(i);
                    Double price = (Double) pricearray.get(i);
                    totalPrice += price;
                    menu_models[i] = new Model(food, price);
                    //Log.w("test name", menu_models[i].name);
                    //Log.w("test price", Double.toString(menu_models[i].price));
                }
                populateList(menu_models);
                total_text = findViewById(R.id.total);
                DecimalFormat df = new DecimalFormat("0.00");
                total_text.setText("Total: £" + df.format(totalPrice));
            } catch (JSONException e) {
            }
        }
    }


    /*
     * Called when Android Beam gets enabled (when the devices comes close to each other)
     * NdefMessage and NdefRecord get set up here. NdefRecord will be sent over via NFC.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        Log.w("Phones close together", "Phones close together");

//        try {
//            adapterRef = CustomAdapter.me;
//        }
//        catch (Exception e){
//            Log.w("Caused Exception", "Exception");
//        }
//        if (adapterRef != null) {
//            Log.w("Not null", "Not null");
//            try {
//                menu = new JSONObject();
//                JSONArray nameList = new JSONArray();
//                JSONArray priceList = new JSONArray();
//                for (int i = 0; i < adapterRef.modelItems.length; i++) {
//                    Model modelItem = adapterRef.modelItems[i];
//                    Log.w("Is box checked?", Boolean.toString(modelItem.checked));
//                    if (!modelItem.checked) {
//                        nameList.put(modelItem.name);
//                        priceList.put(modelItem.price);
//                    }
//                }
//                menu.put("food", nameList);
//                menu.put("price", priceList);
//                menu.put("ccnum", JSONObject.NULL);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else{
//            Log.w("Null", "Null");
//        }

        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", menu.toString().getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        //Log.w("JSON Message about to be passed (within NFC Range)", menu.toString());
        return ndefMessage;
    }


    //copied and pasted - very bad. i also changed a couple things. very bad code. bad.
    public void populateList(Model[] menuItems) {

        ListView listView = findViewById(R.id.recipe_list_view);
        NFCDisplayActivity obj = new NFCDisplayActivity();
        CustomAdapter adapter = new CustomAdapter(this, menuItems, obj);
        listView.setAdapter(adapter); //FIX THIS LINE
        //total_text = findViewById(R.id.total);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(NFCActivity.this).create();
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


}