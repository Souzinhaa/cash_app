package com.fho.piggycash.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fho.piggycash.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetMoney extends BottomSheetDialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transacoes, container, false);
        return view;
    }

}
