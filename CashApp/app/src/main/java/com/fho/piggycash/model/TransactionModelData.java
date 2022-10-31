package com.fho.piggycash.model;

import java.util.List;

public class TransactionModelData {
    private List<TransactionModel> tmList;

    public TransactionModelData(List<TransactionModel> tmList) {
        this.tmList = tmList;
    }

    public List<TransactionModel> getTmList() {
        return tmList;
    }

    public void setTmList(List<TransactionModel> tmList) {
        this.tmList = tmList;
    }
}
