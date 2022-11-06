package com.fho.piggycash.screen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.service.AppService;
import com.fho.piggycash.util.ToastUtil;
import com.fho.piggycash.util.ValidFieldsUtil;

import java.util.Locale;
import java.util.Objects;

public class ActivitySignUp extends AppCompatActivity {

    private final AppService service = AppService.getInstance();

    private EditText edit_email, edit_senha, edit_nome;
    private Button bt_cadastrar;
    private ProgressBar progressBar;
    private View card_img_view_sign;
    private TextView text_overlay_sign, text_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initComponents();

        bt_cadastrar.setOnClickListener(v -> {
            if(validFields()) {

                bt_cadastrar.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                String nome = edit_nome.getText().toString();
                text_overlay_sign.setText(getInitials(nome));

                service.registerNewUser(v, edit_email.getText().toString(),
                        edit_senha.getText().toString(), nome, card_img_view_sign);

                new Handler().postDelayed(this::mainAcitivity, 800);
            }else
                ToastUtil.showToast(v, "Os campos devem ser preenchidos corretamente.");
        });

        text_home.setOnClickListener(v -> mainAcitivity());
    }

    private void mainAcitivity(){
        Intent intent = new Intent(ActivitySignUp.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    private boolean validFields(){
        String email = edit_email.getText().toString();
        String name = edit_nome.getText().toString();
        String password = edit_senha.getText().toString();

        return ValidFieldsUtil.isPassword(password) && ValidFieldsUtil.isEmail(email) && !name.equalsIgnoreCase("");
    }

    private String getInitials(String nomeCompleto){
        String[] nomes = nomeCompleto.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String nome: nomes) {
            if(nome != null && !nome.equalsIgnoreCase(""))
                initials.append(nome.substring(0, 1).toUpperCase(Locale.ROOT));
        }
        return initials.toString();
    }

    private void initComponents(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_nome = findViewById(R.id.edit_nome);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
        progressBar = findViewById(R.id.progressBar);
        card_img_view_sign = findViewById(R.id.card_img_view_sign);
        text_overlay_sign = findViewById(R.id.text_overlay_sign_up);
        text_home = findViewById(R.id.text_home);

        edit_senha.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }
}