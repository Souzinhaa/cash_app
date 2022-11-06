package com.fho.piggycash.screen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.util.KeyboardUtil;
import com.fho.piggycash.util.ToastUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private TextView text_cadastro;
    private ProgressBar progressBar;

    String[] mensagens = {"Preencha todos os campos","Login realizado com sucesso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        Objects.requireNonNull(getSupportActionBar()).hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initComponents();

        bt_entrar.setOnClickListener(v -> {
            String email = edit_email.getText().toString();
            String senha = edit_senha.getText().toString();
            if(email.isEmpty() || senha.isEmpty()){
                ToastUtil.showToast(v, mensagens[0]);
            } else {
                autenticarUsuario(v);
            }
        });

        text_cadastro.setOnClickListener(v -> signUpActivity());

        edit_senha.setOnEditorActionListener((v, actionId, event) -> {
            KeyboardUtil.hideKeyboardActivity(ActivityLogin.this);
            edit_senha.clearFocus();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            mainActivity();
        }
    }

    private void autenticarUsuario(View v){
        progressBar.setVisibility(View.VISIBLE);
        bt_entrar.setVisibility(View.INVISIBLE);

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                mainActivity();
            } else {
                String erro;

                try {
                    throw Objects.requireNonNull(task.getException());
                } catch(Exception e){
                    erro = "Erro ao logar usuario";
                    progressBar.setVisibility(View.INVISIBLE);
                    bt_entrar.setVisibility(View.VISIBLE);
                }
                ToastUtil.showToast(v, erro);
            }
        });
    }

    private void mainActivity(){
        Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }

    private void signUpActivity(){
        Intent intent = new Intent(ActivityLogin.this, ActivitySignUp.class);
        startActivity(intent);
        finish();
    }

    private void initComponents(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        text_cadastro = findViewById(R.id.text_cadastro);
        bt_entrar = findViewById(R.id.bt_login);
        progressBar = findViewById(R.id.progressBar);

        edit_senha.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }
}