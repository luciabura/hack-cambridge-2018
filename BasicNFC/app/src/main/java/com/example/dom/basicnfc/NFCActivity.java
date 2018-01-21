package com.example.dom.basicnfc;

import android.app.Activity;
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

    int ISHOST = 0; //set to 0 or 1 manually

    JSONObject menu = new JSONObject();

    /* Gets called only when the app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(ISHOST == 1)
        setContentView(R.layout.activity_nfc);
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            return;
        }
        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show(); //a little popup window
        }

        mAdapter.setNdefPushMessageCallback(this, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        //Initial JSON Object loaded onto the bill/menu
        try{
            JSONArray foodlist = new JSONArray();
            foodlist.put("fish");
            foodlist.put("bread");
            JSONArray pricelist = new JSONArray();
            pricelist.put(7.90);
            pricelist.put(8.87);
            menu.put("food", foodlist);
            menu.put("price", pricelist);
            menu.put("ccnum", JSONObject.NULL);

            //the code below is copied and pasted from the other .java file.
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
        }
        catch(JSONException e){}
    }


    /*
     * Called whenever device comes near another device (within NFC range).
     * NdefMessage and NdefRecord get set up here. NdefRecord will be sent over via NFC.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
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