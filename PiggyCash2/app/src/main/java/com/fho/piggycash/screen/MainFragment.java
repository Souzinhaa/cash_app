package com.fho.piggycash.screen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.adapter.TransacaoAdapter;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.SignUp;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MainFragment extends Fragment {

    private final SignUp signUp = SignUp.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String usuarioId, nome;
    private TextView text_nome, text_valor, text_alternar;
    private AppCompatButton bt_add, bt_remove, bt_month;
    private RecyclerView recycler_view;
    private View view_main;
    private ProgressBar progressBar;

    public MainFragment(){
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getActivity().getColor(R.color.light_blue));

        iniciarComponentes2();

        if(recycler_view != null)
            mostrarLista();

        bt_add.setOnClickListener(v -> showBottomSheetDialog(true));

        bt_remove.setOnClickListener(v -> showBottomSheetDialog(false));

        bt_month.setOnClickListener(v -> {
            RackMonthPicker monthPicker = new RackMonthPicker(getActivity())
                    .setLocale(Locale.ENGLISH)
                    .setColorTheme(getContext().getColor(R.color.blue))
                    .setPositiveButton(new DateMonthDialogListener() {
                        @Override
                        public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                            signUp.updateMonthYear(month, year);
                            mostrarLista();
                            setMonthYear();
                        }
                    })
                    .setNegativeButton(new OnCancelMonthDialogListener() {
                        @Override
                        public void onCancel(AlertDialog dialog) {
                            ToastUtil.showToast(getView(), "Filtro Cancelado");
                            dialog.dismiss();
                        }
                    });
            monthPicker.show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        iniciarComponentes1();

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){
                nome = documentSnapshot.getString("nome");
                text_valor.setText(MaskEditUtil.addValueMask(Double.valueOf(documentSnapshot.getDouble("saldo").toString())));
                text_nome.setText(nome);
            }
        });

        mostrarLista();
        setMonthYear();
    }

    private void iniciarComponentes1(){
        text_nome = getActivity().findViewById(R.id.text_nome);
        text_valor = getActivity().findViewById(R.id.text_valor);
        recycler_view = getActivity().findViewById(R.id.recycler_view);
        view_main = getActivity().findViewById(R.id.view_main);
        if(signUp.attDate > 0) {
            signUp.updateMonthYear(null, null);
            signUp.attDate = 0;
        }
    }

    private void iniciarComponentes2(){
        bt_add = getActivity().findViewById(R.id.bt_add);
        bt_remove = getActivity().findViewById(R.id.bt_remove);
        bt_month = getActivity().findViewById(R.id.bt_month);
        text_alternar = getActivity().findViewById(R.id.text_alternar);
        progressBar = getActivity().findViewById(R.id.progressBar);
    }

    private void setMonthYear(){
        String month = "";
        switch (signUp.month){
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
        String text = month+"            "+signUp.year;
        bt_month.setText(text);
    }

    private void showBottomSheetDialog(boolean function){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_transacoes);

        TextView text_titulo = bottomSheetDialog.findViewById(R.id.text_titulo);
        if(function)
            text_titulo.setText(getString(R.string.novo_deposito));
        else
            text_titulo.setText(getString(R.string.novo_saque));

        EditText edit_nome = bottomSheetDialog.findViewById(R.id.edit_nome);
        EditText edit_valor = bottomSheetDialog.findViewById(R.id.edit_valor);
        edit_valor.addTextChangedListener(MaskEditUtil.maskValue(edit_valor, MaskEditUtil.FORMAT_VALUE));

        Button bt_cadastrar = bottomSheetDialog.findViewById(R.id.bt_cadastrar);
        ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.progressBar);

        bt_cadastrar.setOnClickListener(v -> {
            if(validarCampos(edit_nome.getText().toString(), edit_valor.getText().toString()))
                cadastrarTransacao(v, bt_cadastrar, progressBar, edit_nome, edit_valor, bottomSheetDialog, function);
            else
                ToastUtil.showToast(v, "Os campos devem ser preenchidos corretamente");
        });

        bottomSheetDialog.show();
    }

    private void cadastrarTransacao(View v, Button bt_cadastrar, ProgressBar progressBar, TextView edit_nome, TextView edit_valor, BottomSheetDialog bottomSheetDialog, boolean function){
        bt_cadastrar.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Double valorAdicionar = Double.parseDouble(MaskEditUtil.unmaskValue(String.valueOf(edit_valor.getText())));
        Double valorSaldo = Double.valueOf(MaskEditUtil.unmaskValue(String.valueOf(text_valor.getText()))); //Erro " 0.00"

        signUp.salvarTransacao(v, new TransactionModel(String.valueOf(edit_nome.getText()), valorAdicionar), function);

        Double saldo;
        if(function)
            saldo = valorSaldo + valorAdicionar;
        else
            saldo = valorSaldo - valorAdicionar;

        text_valor.setText(saldo.toString());

        signUp.salvarDadosUsuario(String.valueOf(text_nome.getText()), saldo);

        new Handler().postDelayed(() -> {
            mostrarLista();
            new Handler().postDelayed(bottomSheetDialog::hide, 800);
        }, 1200);
    }

    private boolean validarCampos(String nome, String valor){
        if(nome != null && nome.length() > 2 && valor != null && !valor.equalsIgnoreCase("R$x,xx") && valor.length() > 1)
            return true;

        return false;
    }

    private void mostrarLista(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("transacoes").document(usuarioId);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> map = documentSnapshot.getData();

            Integer month = signUp.month;
            Integer year = signUp.year;

            List<HashMap> list;
            List<TransactionModel> dList = new ArrayList<>();
            List<TransactionModel> listResult = new ArrayList<>();

            if(map != null && map.get("tmList") != null) {
                list = (List<HashMap>) map.get("tmList");
                list.stream().forEach(i -> {
                    Object nome = i.get("nome");
                    Object valor = i.get("valor");
                    Object data = i.get("data");
                    Object deposito = i.get("deposito");
                    TransactionModel tm = new TransactionModel(nome.toString(), Double.parseDouble(valor.toString()));
                    tm.setData(data.toString());
                    tm.setDeposito(Integer.valueOf(deposito.toString()));
                    dList.add(tm);
                });
            }

            dList.stream().forEach(v -> {
                if(v.getDataMonth().equals(month) && v.getDataYear().equals(year))
                    listResult.add(v);
            });

            recycler_view.setAdapter(new TransacaoAdapter(listResult));

            recycler_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        });
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}