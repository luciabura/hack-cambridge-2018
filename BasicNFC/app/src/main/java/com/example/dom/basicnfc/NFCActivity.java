package com.example.dom.basicnfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.app.PendingIntent;

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
    String mode = "send";
    protected TextView total_text;

    JSONObject menu = new JSONObject();
    NfcAdapter mAdapter;
    PendingIntent myPendingIntent;

    /* Gets called only when the app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        myPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
                foodlist.put("fish");
                foodlist.put("bread");
                foodlist.put("otherfood");
                JSONArray pricelist = new JSONArray();
                pricelist.put(7.90);
                pricelist.put(8.87);
                pricelist.put(9.83);
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
                    Log.w("test name", menu_models[i].name);
                    Log.w("test price", Double.toString(menu_models[i].price));
                }
                populateList(menu_models);
                updateTotal(totalPrice);
            }
            catch (JSONException e) {
            }
            mode = "send";
        }
        else{ //we're in the client
            mode = "receive";
        }
    }


    /*
     * Called whenever Android Beam is invoked (device comes near another NFC Device)
     * NdefMessage and NdefRecord get set up here. NdefRecord will be sent over via NFC.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //Update what the menu says here.

        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", menu.toString().getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        //Log.w("JSON Message about to be passed (within NFC Range)", menu.toString());
        return ndefMessage;
    }


    /* This is where the JSON object gets received and the receiving table updated */
    @Override
    protected void onResume(){
        Log.w("Here", "Here");
        super.onResume();
        mAdapter.enableForegroundDispatch(this, myPendingIntent, null, null);
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
                //Remove the checked items here

            }
            catch (JSONException e) {}
        }
    }




    //copied and pasted - very bad. i also changed a couple things. very bad code. bad.
    public void populateList(Model[] menuItems) {

        ListView listView = findViewById(R.id.recipe_list_view);
        NFCActivity obj = new NFCActivity();
        CustomAdapter adapter = new CustomAdapter(this, menuItems, obj);
        listView.setAdapter(adapter); //FIX THIS LINE
        total_text = findViewById(R.id.total);
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

    public void updateTotal(double totalPrice) {
        if (totalPrice < 0) {
            totalPrice = 0;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        total_text.setText("Total: £" + df.format(totalPrice));
    }


}