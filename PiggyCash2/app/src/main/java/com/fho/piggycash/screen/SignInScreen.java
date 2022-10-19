package com.fho.piggycash.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.util.ToastUtil;
import com.fho.piggycash.util.ValidFieldsUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignInScreen extends AppCompatActivity {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private EditText edit_email, edit_senha, edit_nome;
    private Button bt_cadastrar;
    private ProgressBar progressBar;
    private View card_img_view_sign;
    private TextView text_overlay_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        getSupportActionBar().hide();

        iniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(validarCampos())
                    cadastrarUsuario(v);
                else
                    ToastUtil.showToast(v, "Os campos devem ser preenchidos corretamente.");
            }
        });
    }

    private void telaInicial(){
        Intent intent = new Intent(SignInScreen.this, LoginScreen.class);
        startActivity(intent);
        finish();
    }

    private void cadastrarUsuario(View view){
        progressBar.setVisibility(View.VISIBLE);
        bt_cadastrar.setVisibility(View.INVISIBLE);

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        String nome = edit_nome.getText().toString();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String iniciais = getInitials(nome);

                    text_overlay_sign.setText(iniciais);

                    String usuarioId = task.getResult().getUser().getUid();
                    uploadImage(usuarioId);

                    salvarDadosUsuario();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(view, "Cadastro Realizado com Sucesso!!");
                            telaInicial();
                        }
                    }, 1000);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no minimo 6 caracteres";
                    }catch(FirebaseAuthUserCollisionException e) {
                        erro = "Esse email ja existe";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido";
                    } catch(Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }
                    ToastUtil.showToast(view, erro);
                }
            }
        });
    }

    private void salvarDadosUsuario(){
        String nome = edit_nome.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("saldo", 0);

         String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db","Erro ao salvar os dados: " + e.toString());
            }
        });

    }

    private boolean validarCampos(){
        String email = edit_email.getText().toString();
        String nome = edit_nome.getText().toString();
        String senha = edit_senha.getText().toString();

        if(ValidFieldsUtil.isPassword(senha) && ValidFieldsUtil.isEmail(email) && nome != null)
            return true;

        return false;
    }

    private void iniciarComponentes(){
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_nome = findViewById(R.id.edit_nome);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
        progressBar = findViewById(R.id.progressBar);
        card_img_view_sign = findViewById(R.id.card_img_view_sign);
        text_overlay_sign = findViewById(R.id.text_overlay_sign_up);
    }

    private void uploadImage(String usuarioId){
        byte[] image = convertImage();
        final String path = "usuarios/perfil/" + usuarioId + ".png";
        StorageReference reference = storage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("metadata", "testMeta")
                .build();

        UploadTask uploadTask = reference.putBytes(image, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Atualizar imagem
                Log.e("IMAGEM PERFIL:", "Upload da imagem feita com sucesso!");
            }
        });
    }

    private byte[] convertImage(){
        card_img_view_sign.setDrawingCacheEnabled(true);
        card_img_view_sign.buildDrawingCache();
        Bitmap bitmap = card_img_view_sign.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        card_img_view_sign.setDrawingCacheEnabled(false);
        return baos.toByteArray();
    }

    private String getInitials(String nomeCompleto){
        String[] nomes = nomeCompleto.split(" ");
        String initials = "";
        for (String nome: nomes) {
            if(nome != null && nome != "")
                initials += nome.substring(0,1).toUpperCase(Locale.ROOT);
        }
        return initials;
    }
}