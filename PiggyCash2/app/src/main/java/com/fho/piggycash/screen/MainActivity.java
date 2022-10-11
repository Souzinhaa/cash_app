package com.fho.piggycash.screen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.SignUp;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    private String usuarioId, nome, email;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView text_nome, text_valor;
    private AppCompatButton bt_add, bt_remove;
    private SignUp signUp = SignUp.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        iniciarComponentes2();

        bt_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showBottomSheetDialog(true);
            }
        });

        bt_remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showBottomSheetDialog(false);
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
                    text_valor.setText(documentSnapshot.getDouble("saldo").toString());
                    text_nome.setText(nome);
                }
            }
        });
    }

    public void iniciarComponentes1(){
        text_nome = findViewById(R.id.text_nome);
        text_valor = findViewById(R.id.text_valor);
    }

    public void iniciarComponentes2(){
        bt_add = findViewById(R.id.bt_add);
        bt_remove = findViewById(R.id.bt_remove);
    }

    private void showBottomSheetDialog(boolean function){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.fragment_transacoes);

        EditText edit_nome = bottomSheetDialog.findViewById(R.id.edit_valor);
        EditText edit_valor = bottomSheetDialog.findViewById(R.id.edit_valor);
        edit_valor.addTextChangedListener(MaskEditUtil.maskValue(edit_valor, MaskEditUtil.FORMAT_VALUE));

        Button bt_cadastrar = bottomSheetDialog.findViewById(R.id.bt_cadastrar);

        bt_cadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(function)
                    signUp.salvarTransacao(v, new TransactionModel(String.valueOf(edit_nome.getText()), Double.parseDouble(MaskEditUtil.unmask(String.valueOf(edit_valor.getText())))), true);
                else
                    signUp.salvarTransacao(v, new TransactionModel(String.valueOf(edit_nome.getText()), Double.parseDouble(MaskEditUtil.unmask(String.valueOf(edit_valor.getText())))), true);
            }


        });

        bottomSheetDialog.show();
    }
}