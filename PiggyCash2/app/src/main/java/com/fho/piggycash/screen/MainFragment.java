package com.fho.piggycash.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    private final SignUp signUp = SignUp.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String usuarioId, nome;
    private TextView text_nome, text_valor, text_alternar;
    private AppCompatButton bt_add, bt_remove;
    private RecyclerView recycler_view;
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

        text_alternar.setOnClickListener(v -> {
            DialogMonthYearPicker pd = new DialogMonthYearPicker();
            pd.show(getParentFragmentManager(), "MonthYearPicker");
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
    }

    private void iniciarComponentes1(){
        text_nome = getActivity().findViewById(R.id.text_nome);
        text_valor = getActivity().findViewById(R.id.text_valor);
        recycler_view = getActivity().findViewById(R.id.recycler_view);
    }

    private void iniciarComponentes2(){
        bt_add = getActivity().findViewById(R.id.bt_add);
        bt_remove = getActivity().findViewById(R.id.bt_remove);
        text_alternar = getActivity().findViewById(R.id.text_alternar);
        progressBar = getActivity().findViewById(R.id.progressBar);
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
            List<TransactionModel> list;

            if(map != null && map.get("tmList") != null)
                list = (List<TransactionModel>) map.get("tmList");
            else
                list = new ArrayList<>();

            recycler_view.setAdapter(new TransacaoAdapter(list));

            recycler_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        });
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}