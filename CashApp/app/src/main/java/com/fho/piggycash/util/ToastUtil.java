package com.fho.piggycash.util;

import android.view.View;
import android.widget.Toast;

public abstract class ToastUtil {
    public static void showToast(View view, String mensagem){
        Toast.makeText(view.getContext(), mensagem, Toast.LENGTH_LONG).show();
    }
}
