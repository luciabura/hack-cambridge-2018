package com.example.dom.basicnfc;

/**
 * Created by Simon on 20/01/2018.
 */

public class Model {
    String name;
    double price;
    boolean checked;

    Model(String name, double price){
        this.name = name;
        this.price = price;
        this.checked = false;
    }
    public String getName(){
        return this.name;
    }
    public double getPrice(){
        return this.price;
    }

}