package com.fho.piggycash.service;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.fho.piggycash.model.UserData;
import com.fho.piggycash.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp {

    private static SignUp instance;
    private UserData userData;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};

    public void iniciarUsuario(UserData user){
        userData = user;
    }

    public void cadastrarUsuario(View view){
        String email = userData.getEmail();
        String senha = userData.getSenha();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    salvarDadosUsuario(userData);

                    ToastUtil.showToast(view, mensagens[1]);
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

    private void salvarDadosUsuario(UserData user){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("user", user);


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

    private SignUp() {}

    public static SignUp getInstance() {
        if (instance == null)
            instance = new SignUp();
        return instance;
    }

}
