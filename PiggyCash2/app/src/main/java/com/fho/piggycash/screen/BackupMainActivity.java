package com.fho.piggycash.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.adapter.TransacaoAdapter;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.SignUp;
import com.fho.piggycash.util.MaskEditUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BackupMainActivity extends AppCompatActivity {

    private String usuarioId, nome, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nome, text_valor;
    private AppCompatButton bt_add, bt_remove;
    private RecyclerView recycler_view;
    private TextView text_alternar;
    private ImageView img_exit;
    private BottomNavigationView bottom_navigation;
    private SignUp signUp = SignUp.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        iniciarComponentes2();

        if(recycler_view != null)
            mostrarLista();

        bt_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //signUp.calculoSaldo(v);
                showBottomSheetDialog(true);
            }
        });

        bt_remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showBottomSheetDialog(false);
            }
        });

        img_exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                telaLogin();
                FirebaseAuth.getInstance().signOut();
            }
        });

        text_alternar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMonthYearPicker pd = new DialogMonthYearPicker();
                pd.show(getSupportFragmentManager(), "MonthYearPicker");
            }
        });

        bottom_navigation.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_1:
                        break;
                    case R.id.page_2:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        iniciarComponentes1();

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nome = documentSnapshot.getString("nome");
                    text_valor.setText(MaskEditUtil.addValueMask(Double.valueOf(documentSnapshot.getDouble("saldo").toString())));
                    text_nome.setText(nome);
                }
            }
        });

        mostrarLista();
    }

    public void iniciarComponentes1(){
        text_nome = findViewById(R.id.text_nome);
        text_valor = findViewById(R.id.text_valor);
        recycler_view = findViewById(R.id.recycler_view);
    }

    public void iniciarComponentes2(){
        bt_add = findViewById(R.id.bt_add);
        bt_remove = findViewById(R.id.bt_remove);
        text_alternar = findViewById(R.id.text_alternar);
        img_exit = findViewById(R.id.img_exit);
        bottom_navigation = findViewById(R.id.bottom_navigation);
    }

    private void showBottomSheetDialog(boolean function){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
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

        bt_cadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cadastrarTransacao(v, bt_cadastrar, progressBar, edit_nome, edit_valor, bottomSheetDialog, function);
            }
        });

        bottomSheetDialog.show();
    }

    public void cadastrarTransacao(View v, Button bt_cadastrar, ProgressBar progressBar, TextView edit_nome, TextView edit_valor, BottomSheetDialog bottomSheetDialog, boolean function){
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarLista();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetDialog.hide();
                    }
                }, 800);
            }
        }, 1200);
    }

    public void mostrarLista(){
        List<TransactionModel> tm = new ArrayList<TransactionModel>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("transacoes").document(usuarioId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                List<TransactionModel> list;

                if(map != null && map.get("tmList") != null)
                    list = (List<TransactionModel>) map.get("tmList");
                else
                    list = new ArrayList<TransactionModel>();

                recycler_view.setAdapter(new TransacaoAdapter(list));
            }
        });

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
    }

    private void telaLogin(){
        Intent intent = new Intent(BackupMainActivity.this, LoginScreen.class);
        startActivity(intent);
        finish();
    }
}