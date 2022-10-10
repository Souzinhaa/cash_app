package com.fho.piggycash.screen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.util.ToastUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    String usuarioId, nome, email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView text_nome, text_valor;
    AppCompatButton bt_add, bt_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        iniciarComponentes2();

        bt_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BottomSheetMoney bottomSheetMoney = new BottomSheetMoney();
                bottomSheetMoney.show(getSupportFragmentManager(), "tag");
            }
        });

        bt_remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BottomSheetMoney bottomSheetMoney = new BottomSheetMoney();
                bottomSheetMoney.show(getSupportFragmentManager(), "tag");
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
}