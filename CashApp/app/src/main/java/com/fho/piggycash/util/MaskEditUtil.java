package com.fho.piggycash.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class MaskEditUtil<FORMAT_CASH_1> {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_CPF_NONE = "###########";
    public static final String FORMAT_CNPJ = "##.###.###/####-##";
    public static final String FORMAT_FONE = "(##) #####-####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##/##/####";
    public static final String FORMAT_DATE_MONTH_YEAR = "##/####";
    public static final String FORMAT_HOUR = "##:##";


    public static final String FORMAT_CASH_1 = "R$x,x#";
    public static final String FORMAT_CASH_2 = "R$x,##";
    public static final String FORMAT_CASH_3 = "R$#,##";
    public static final String FORMAT_CASH_4 = "R$##,##";
    public static final String FORMAT_CASH_5 = "R$###,##";
    public static final String FORMAT_CASH_6 = "R$#.###,##";
    public static final String FORMAT_CASH_7 = "R$##.###,##";
    public static final String FORMAT_CASH_8 = "R$###.###,##";
    public static final String FORMAT_CASH_9 = "R$#.###.###,##";

    public static final String[] FORMAT_VALUE = {FORMAT_CASH_1,FORMAT_CASH_2,FORMAT_CASH_3,FORMAT_CASH_4,FORMAT_CASH_5,FORMAT_CASH_6,FORMAT_CASH_7,FORMAT_CASH_8, FORMAT_CASH_9};

    public static final String FORMAT_CARD = "#### #### #### ####";

    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param ediTxt
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText ediTxt, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskEditUtil.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (final char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }

    public static TextWatcher maskValue(final EditText ediTxt, final String[] mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = MaskEditUtil.unmask(s.toString());
                int lenghtStr = 0;
                if(str != null)
                    lenghtStr = str.length();
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }

                if(lenghtStr != 0)
                    mascara = valueHelper(str, mask[lenghtStr-1], old);
                else
                    mascara = "R$x,xx";

                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }

    public static String valueHelper(String str, String mask, String old){
        String mascara = "";
        int i = 0;
        for (final char m : mask.toCharArray()) {
            if (m != '#' && str.length() > old.length()) {
                mascara += m;
                continue;
            }
            try {
                mascara += str.charAt(i);
            } catch (final Exception e) {
                break;
            }
            i++;
        }
        return mascara;

    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "").replaceAll("[R$]","").replaceAll("[,]","").replaceAll("[x]","");
    }

    public static String unmaskValue(final String s) {
        String sub = s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "").replaceAll("[R$ ]","").replaceAll("[,]",".").replaceAll("[x]","0").replaceAll("[ ]","");
        /*if(sub.equalsIgnoreCase(" 0.00") || sub.equalsIgnoreCase(" 0.00"))
            sub = "0.0";*/
        return sub;
    }

    public static String addValueMask(final Double textoAFormatar) {
        return NumberFormat.getCurrencyInstance().format(textoAFormatar);
    }
}