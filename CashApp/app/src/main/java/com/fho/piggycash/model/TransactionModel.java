package com.fho.piggycash.model;

import com.google.firebase.firestore.Exclude;

public class TransactionModel implements Comparable<TransactionModel> {
    private String name;
    private double value;
    private String date;
    private Integer isDeposit;

    public TransactionModel(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public TransactionModel(String name, double value, String date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public Integer getDateDay(){
        if(date != null){
            String[] dateDividida = date.split("/");
            return Integer.parseInt(dateDividida[0]);
        }
        return null;
    }

    @Exclude
    public Integer getDateMonth(){
        if(date != null){
            String[] dateDividida = date.split("/");
            return Integer.parseInt(dateDividida[1]);
        }
        return null;
    }

    @Exclude
    public Integer getDateYear(){
        if(date != null){
            String[] dateDividida = date.split("/");
            return Integer.parseInt(dateDividida[2]);
        }
        return null;
    }

    public Integer isDeposit() {
        return isDeposit;
    }

    public boolean valueIsDeposit() {
        return isDeposit == 1;
    }

    public void setDeposit(Integer deposit) {
        isDeposit = deposit;
    }

    @Exclude
    @Override
    public int compareTo(TransactionModel other) {
        if(this.getDateDay() > other.getDateDay())
            return -1;
        else if (this.getDateDay() < other.getDateDay())
            return 1;

        return 0;
    }
}
