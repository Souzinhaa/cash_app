package com.fho.piggycash.util;

import android.util.Patterns;

public abstract class ValidFieldsUtil {

    public static boolean isEmail(String emailToText) {
        return !emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches();
    }

    public static boolean isPassword(String pass){
        return pass != null && !pass.isEmpty() && pass.length() > 5;
    }

    public static boolean isDate(String date){
        if(date != null && !date.equals("") && date.length() <= 10){
            String[] dateSubs = date.split("/");
            return dateSubs.length == 3;
        }

        return false;
    }

    public static String getMonth(int nMonth){
        String month = "";
        switch (nMonth){
            case 1:
                month = "JAN";
                break;
            case 2:
                month = "FEV";
                break;
            case 3:
                month = "MAR";
                break;
            case 4:
                month = "ABR";
                break;
            case 5:
                month = "MAI";
                break;
            case 6:
                month = "JUN";
                break;
            case 7:
                month = "JUL";
                break;
            case 8:
                month = "AGO";
                break;
            case 9:
                month = "SET";
                break;
            case 10:
                month = "OUT";
                break;
            case 11:
                month = "NOV";
                break;
            case 12:
                month = "DEZ";
                break;
        }
        return month;
    }
}
