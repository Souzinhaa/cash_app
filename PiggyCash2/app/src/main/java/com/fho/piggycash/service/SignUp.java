package com.fho.piggycash.service;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.model.TransactionModelData;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUp {

    private final static String FORMAT_DATA = "dd/MM/yyyy";
    private static SignUp instance;
    private UserData userData;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};
    public Integer month, year;
    public int attDate = 1;

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

                    //salvarDadosUsuario(userData);

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

    public void salvarDadosUsuario(String nome, Double saldo){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("saldo", saldo);

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

    public void salvarTransacao(View v, TransactionModel transaction, boolean type){

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

                Date dataAtual = new Date();
                DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATA);

                transaction.setData(dateFormat.format(dataAtual));
                transaction.setDeposito(0);

                if(type)
                    transaction.setDeposito(1);

                list.add(transaction);

                TransactionModelData tData = new TransactionModelData(new ArrayList<TransactionModel>());
                tData.setTmList(list);

                documentReference.set(tData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ToastUtil.showToast(v, "Transação cadastrada com sucesso!");
                        Log.d("db","Sucesso ao salvar os dados");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastUtil.showToast(v, "Erro ao cadastrar transação!");
                        Log.d("db","Erro ao salvar os dados: " + e.toString());
                    }
                });
            }
        });
    }


    public Map<String, Map<String, Object>> getTransacao(View v){

        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Map<String,Object>> map = new HashMap<>();
        //map.put("add", null);
        map.put("retire", null);

        Map<String, Object> map2;


        DocumentReference docRef = db.collection("transacao-adicionar").document(usuarioId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //ToastUtil.showToast(v, documentSnapshot.getData().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ToastUtil.showToast(v, e.toString());
            }
        });

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                map.put("add", value.getData());
            }
        });

        /*docRef = db.collection("transacao-retirar").document(usuarioId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                map.replace("add", (List<TransactionModel>) documentSnapshot.getData().get("tmList"));
                ToastUtil.showToast(v, documentSnapshot.getData().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ToastUtil.showToast(v, e.toString());
            }
        });*/


        ToastUtil.showToast(v, map.get("add").toString());

        return map;
    }

    public void calculoSaldo(View v){
        Map<String, Map<String, Object>> map = getTransacao(v);
        //List<TransactionModel> lm = (List<TransactionModel>) map.get("add").get("tmList");

        /*AtomicDouble a = new AtomicDouble(0.0);
        map.get("add").stream().forEach(it -> a.addAndGet(it.getValor()));
        Double addValue = a.get();
        Log.i("Add Cash", addValue.toString());

        map.get("retire").stream().forEach(it -> a.addAndGet(it.getValor()));
        Double retireValue = a.get();
        Log.i("Retire Cash", retireValue.toString());*/
    }

    public void updateMonthYear(Integer newMonth, Integer newYear){
        if(newMonth == null && newYear == null) {
            Calendar cal = Calendar.getInstance();
            month = cal.get(Calendar.MONTH)+1;
            year = cal.get(Calendar.YEAR);
        } else if(newMonth == 0 && newYear == 0){

        } else {
            month = newMonth;
            year = newYear;
        }
    }

    private SignUp() {
    }

    public static SignUp getInstance() {
        if (instance == null)
            instance = new SignUp();
        return instance;
    }

}
