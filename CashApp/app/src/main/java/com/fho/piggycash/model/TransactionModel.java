package com.fho.piggycash.model;

public class TransactionModel {
    private String nome;
    private double valor;
    private String data;
    private Integer isDeposito;

    public TransactionModel(String nome, double valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getDataMonth(){
        if(data != null){
            String[] dataDividida = data.split("/");
            return Integer.parseInt(dataDividida[1]);
        }
        return null;
    }

    public Integer getDataYear(){
        if(data != null){
            String[] dataDividida = data.split("/");
            return Integer.parseInt(dataDividida[2]);
        }
        return null;
    }

    public Integer isDeposito() {
        return isDeposito;
    }

    public boolean valueIsDeposito() {
        if(isDeposito == 1)
            return true;

        return false;
    }

    public void setDeposito(Integer deposito) {
        isDeposito = deposito;
    }
}
