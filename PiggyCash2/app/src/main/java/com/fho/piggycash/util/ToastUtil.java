package com.fho.piggycash.util;

import android.view.View;
import android.widget.Toast;

public abstract class ToastUtil {
    public static void showToast(View view, String mensagem){
        Toast.makeText(view.getContext(), mensagem, Toast.LENGTH_LONG).show();
        /*toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        Snackbar snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();*/
    }
}
