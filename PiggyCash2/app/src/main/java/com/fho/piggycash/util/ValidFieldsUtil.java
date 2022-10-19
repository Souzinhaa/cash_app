package com.fho.piggycash.util;

import android.util.Patterns;

public abstract class ValidFieldsUtil {

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean isEmail(String emailToText) {
        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches())
            return true;

        return false;
    }

    public static boolean isPassword(String pass){

        if(pass != null && !pass.isEmpty() && pass.length() > 5)
            return true;

        return false;
    }

    public static boolean isBothPassword(String pass, String pass2){

        if(isPassword(pass) && isPassword(pass2) && pass.equals(pass2))
            return true;

        return false;
    }

    public static boolean isCardDate(String date){
        if(date != null) {
            String month = date.substring(0, 1);
            Integer monthI = Integer.parseInt(month);
            String year = date.substring(3, 7);
            Integer yearI = Integer.parseInt(year);

            return monthI > 0 && monthI <= 12 && yearI > 2021 && yearI < 2999? true : false;
        }

        return false;
    }

    public static boolean isCvv(String cvv){
        return cvv != null && cvv.length() == 3;
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    public static boolean isCPF(String cpf) {

        cpf = cpf.replace(".","");
        cpf = cpf.replace("-","");

        if ((cpf==null) || (cpf.length()!=11)) return false;

        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    public static boolean isCNPJ(String cnpj) {

        int valor0 = 0;
        cnpj = cnpj.replace(".","");
        cnpj = cnpj.replace("/","");
        cnpj = cnpj.replace("-","");

        for(int i = 0; i<14; i++) {
            if(cnpj.substring(i,i+1) == "0")
                valor0++;
        }

        if ((cnpj==null)||(cnpj.length()!=14)||valor0==14) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }
}
