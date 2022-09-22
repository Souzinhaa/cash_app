package com.fho.piggycash.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private TextView text_cadastro;
    private ProgressBar progressBar;

    String[] mensagens = {"Preencha todos os campos","Login realizado com sucesso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        FirebaseApp.initializeApp(this);
        getSupportActionBar().hide();

        iniciarComponentes();

        bt_entrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();
                if(email.isEmpty() || senha.isEmpty()){
                    ToastUtil.showToast(v, mensagens[0]);
                } else {
                    autenticarUsuario(v);
                }
            }
        });

        text_cadastro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //telaCadastro();
                ToastUtil.showToast(v, "Não Habilitado!");
            }
        });
    }

    private void autenticarUsuario(View v){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    bt_entrar.setVisibility(View.INVISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            telaPrincipal();
                        }
                    }, 3000);
                } else {
                    String erro;

                    try {
                        throw task.getException();
                    } catch(Exception e){
                        erro = "Erro ao logar usuario";
                    }
                    ToastUtil.showToast(v, erro);
                }
            }
        });
    }

    private void telaPrincipal(){
        Intent intent = new Intent(LoginScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void iniciarComponentes(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        text_cadastro = findViewById(R.id.text_cadastro);
        bt_entrar = findViewById(R.id.bt_login);
        progressBar = findViewById(R.id.progressBar);
    }
}