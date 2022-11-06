package com.fho.piggycash.screen.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.AppService;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;
import com.fho.piggycash.util.ValidFieldsUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kal.rackmonthpicker.RackMonthPicker;

import java.util.Calendar;
import java.util.Locale;

public class FragmentMain extends Fragment {

    private final AppService appService = AppService.getInstance();
    private final Calendar cal = Calendar.getInstance();

    private TextView text_name, text_value;
    private AppCompatButton bt_add, bt_remove, bt_month;
    private RecyclerView recycler_view;
    private ProgressBar progressBar;

    public FragmentMain(){
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(requireActivity().getColor(R.color.light_blue));

        initComponentsCreated();

        if(recycler_view != null)
            showList();

        bt_add.setOnClickListener(v -> showBottomSheetDialog(true));

        bt_remove.setOnClickListener(v -> showBottomSheetDialog(false));

        bt_month.setOnClickListener(v -> {
            RackMonthPicker monthPicker = new RackMonthPicker(requireActivity())
                    .setLocale(Locale.ENGLISH)
                    .setColorTheme(requireContext().getColor(R.color.blue))
                    .setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
                        appService.updateMonthYear(month, year);
                        showList();
                        setMonthYear();
                    })
                    .setNegativeButton(dialog -> {
                        ToastUtil.showToast(requireView(), "Filtro Cancelado");
                        dialog.dismiss();
                    });
            monthPicker.show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        initComponentsStart();

        appService.showUserData(text_value, text_name);
        showList();

        setMonthYear();
    }

    private void showBottomSheetDialog(boolean function){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_transacoes);

        TextView text_tittle = bottomSheetDialog.findViewById(R.id.text_tittle);
        assert text_tittle != null;
        if(function) {
            text_tittle.setText(getString(R.string.novo_deposito));
        }
        else {
            text_tittle.setText(getString(R.string.novo_saque));
        }

        EditText edit_nome = bottomSheetDialog.findViewById(R.id.edit_nome);

        EditText edit_valor = bottomSheetDialog.findViewById(R.id.edit_valor);
        assert edit_valor != null;
        edit_valor.addTextChangedListener(MaskEditUtil.maskValue(edit_valor, MaskEditUtil.FORMAT_VALUE));

        EditText edit_data = bottomSheetDialog.findViewById(R.id.edit_data);

        assert edit_data != null;
        edit_data.setHint(validateDate(getDate()));
        edit_data.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edit_data.addTextChangedListener(MaskEditUtil.mask(edit_data, MaskEditUtil.FORMAT_DATE));

        Button bt_cadastrar = bottomSheetDialog.findViewById(R.id.bt_cadastrar);
        ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.progressBar);

        assert bt_cadastrar != null;
        bt_cadastrar.setOnClickListener(v -> {
            String dataCerta = edit_data.getHint().toString();
            if(!edit_data.getText().toString().equalsIgnoreCase(""))
                dataCerta = edit_data.getText().toString();

            assert edit_nome != null;
            if(validFields(edit_nome.getText().toString(), edit_valor.getText().toString(), dataCerta)) {

                assert progressBar != null;
                cadastrartransaction(v, bt_cadastrar, progressBar, edit_nome, edit_valor, bottomSheetDialog, function, dataCerta);
            }else
                ToastUtil.showToast(v, "Os campos devem ser preenchidos corretamente");
        });

        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void cadastrartransaction(View v, Button bt_cadastrar, ProgressBar progressBar, TextView edit_nome, TextView edit_valor, BottomSheetDialog bottomSheetDialog, boolean function, String data){
        bt_cadastrar.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Double valorAdicionar = Double.parseDouble(MaskEditUtil.unmaskValue(String.valueOf(edit_valor.getText())));
        Double valorSaldo = Double.valueOf(MaskEditUtil.unmaskValue(String.valueOf(text_value.getText()))); //Erro " 0.00"

        appService.saveTransaction(v, new TransactionModel(String.valueOf(edit_nome.getText()), valorAdicionar, data), function);

        double saldo;
        if(function)
            saldo = valorSaldo + valorAdicionar;
        else
            saldo = valorSaldo - valorAdicionar;

        text_value.setText(Double.toString(saldo));

        appService.saveUser(String.valueOf(text_name.getText()), saldo);

        new Handler().postDelayed(() -> {
            showList();
            new Handler().postDelayed(bottomSheetDialog::hide, 800);
        }, 1200);
    }

    private boolean validFields(String nome, String valor, String data){
        return nome != null && nome.length() > 2 && valor != null && !valor.equalsIgnoreCase("R$x,xx") && valor.length() > 1 && ValidFieldsUtil.isDate(data);
    }

    private String getDate(){
        return cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
    }

    private String validateDate(String date){
        String[] dateSplit = date.split("/");
        StringBuilder dateFull = new StringBuilder();

        for(int i = 0; i < 3; i++){
            if(dateSplit[i].length()<4) {
                if (dateSplit[i].length() < 2) {
                    String aux = dateSplit[i];
                    dateSplit[i] = "0" + aux;
                }
            }
            if(i == 0)
                dateFull.append(dateSplit[i]);
            else
                dateFull.append("/").append(dateSplit[i]);
        }
        return dateFull.toString();
    }

    @SuppressLint("SetTextI18n")
    private void setMonthYear(){
        bt_month.setText(ValidFieldsUtil.getMonth(appService.month)+"            "+ appService.year);
    }

    private void showList(){
        appService.showTransactions(recycler_view, requireActivity(), progressBar);
    }

    private void initComponentsStart(){
        text_name = requireActivity().findViewById(R.id.text_name);
        text_value = requireActivity().findViewById(R.id.text_value);
        recycler_view = requireActivity().findViewById(R.id.recycler_view);
        if(appService.attDate > 0) {
            appService.updateMonthYear(null, null);
            appService.attDate = 0;
        }
    }

    private void initComponentsCreated(){
        bt_add = requireActivity().findViewById(R.id.bt_add);
        bt_remove = requireActivity().findViewById(R.id.bt_remove);
        bt_month = requireActivity().findViewById(R.id.bt_month);
        progressBar = requireActivity().findViewById(R.id.progressBar);
    }
}