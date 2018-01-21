package com.example.dom.basicnfc;

/**
 * Created by Simon on 20/01/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.text.DecimalFormat;

public class CustomAdapter extends ArrayAdapter {

    Model[] modelItems = null;
    Context context;
    NFCDisplayActivity main;

    public CustomAdapter(Context context, Model[] resource, NFCDisplayActivity main) {
        super(context, R.layout.row, resource);
        this.context = context;
        this.modelItems = resource;
        this.main = main;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView name = convertView.findViewById(R.id.title);
        TextView price = convertView.findViewById(R.id.price);
        CheckBox cb = convertView.findViewById(R.id.checkBox1);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTotal(buttonView);
                View v = (View) buttonView.getParent();
                if (isChecked) {
                    v.setBackgroundColor(Color.parseColor("#a3c6ff"));
                } else {
                    v.setBackgroundColor(Color.WHITE);
                }
            }

        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                CheckBox cb = v.findViewById(R.id.checkBox1);
                cb.setChecked(!cb.isChecked());
                //updateTotal(cb);
                if (cb.isChecked()) {
                    v.setBackgroundColor(Color.parseColor("#a3c6ff"));
                } else {
                    v.setBackgroundColor(Color.WHITE);
                }
            }
        });

        name.setText(modelItems[position].getName());
        DecimalFormat df = new DecimalFormat("0.00");
        price.setText("£" + df.format(modelItems[position].getPrice()));
        cb.setTag(modelItems[position]);
        return convertView;
    }

    public void updateTotal(CompoundButton buttonView) {
        Model item = (Model) buttonView.getTag();
        if (buttonView.isChecked()) {
            main.totalPrice += item.price;
        } else {
            main.totalPrice -= item.price;
        }
        main.updateTotal();
    }

}