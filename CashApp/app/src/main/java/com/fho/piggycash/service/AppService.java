package com.fho.piggycash.service;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.model.TransactionModelData;
import com.fho.piggycash.screen.adapter.TransactionAdapter;
import com.fho.piggycash.util.MaskEditUtil;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService {

    private static AppService instance;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private String userId;
    public Integer month, year;
    public int attDate = 1;

    public final String userKey = "users";
    public final String transactionKey = "transactions";
    public final String pathImage = "users/profile/x.png";

    public void registerNewUser(View view, String email, String password, String name, View viewImage){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    userId = task.getResult().getUser().getUid();

                    uploadImage(viewImage);

                    saveUser(name, 0.0);
                    ToastUtil.showToast(view, "Cadastro Realizado com Sucesso!!");

                } else {
                    String errorMessage;
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        errorMessage = "Digite uma senha com no minimo 6 caracteres";
                    }catch(FirebaseAuthUserCollisionException e) {
                        errorMessage = "Esse email já existe";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        errorMessage = "E-mail inválido";
                    } catch(Exception e){
                        errorMessage = "Erro ao cadastrar usuário";
                    }
                    ToastUtil.showToast(view, errorMessage);
                }
            }
        });
    }

    public void saveUser(String name, Double balance){

        Map<String,Object> user = new HashMap<>();
        user.put("name", name);
        user.put("balance", balance);

        updateUserId();

        DocumentReference documentReference = database.collection(userKey).document(userId);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void updateBalance(Double tBalance, boolean isDeposit){

        updateUserId();

        DocumentReference documentReference = database.collection(userKey).document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();

                if(map != null){
                    String name = map.get("name").toString();
                    Double beforeBalance = Double.valueOf(map.get("balance").toString());
                    if(isDeposit)
                        beforeBalance -= tBalance;
                    else
                        beforeBalance += tBalance;

                    Map<String,Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("balance", beforeBalance);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            }
        });



    }

    public void saveTransaction(View v, TransactionModel transaction, boolean type){

        updateUserId();

        DocumentReference documentReference = database.collection(transactionKey).document(userId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                List<TransactionModel> list;

                if(map != null && map.get("tmList") != null)
                    list = (List<TransactionModel>) map.get("tmList");
                else
                    list = new ArrayList<TransactionModel>();

                transaction.setDeposit(0);

                if(type)
                    transaction.setDeposit(1);

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

    public void deleteTransaction(View v, int indexTransaction){

        updateUserId();

        DocumentReference documentReference = database.collection(transactionKey).document(userId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                List<HashMap> list;

                if(map != null && map.get("tmList") != null)
                    list = (List<HashMap>) map.get("tmList");

                List<TransactionModel> dList = new ArrayList<>();
                List<TransactionModel> listResult = new ArrayList<>();
                List<TransactionModel> otherResult = new ArrayList<>();

                if(map != null && map.get("tmList") != null) {
                    list = (List<HashMap>) map.get("tmList");
                    list.stream().forEach(i -> {
                        Object nome = i.get("name");
                        Object valor = i.get("value");
                        Object data = i.get("date");
                        Object deposito = i.get("deposit");
                        TransactionModel tm = new TransactionModel(nome.toString(), Double.parseDouble(valor.toString()));
                        tm.setDate(data.toString());
                        tm.setDeposit(Integer.valueOf(deposito.toString()));
                        dList.add(tm);
                    });
                }

                dList.stream().forEach(v -> {
                    if(v.getDateMonth().equals(month) && v.getDateYear().equals(year))
                        listResult.add(v);
                    else
                        otherResult.add(v);
                });

                Collections.sort(listResult);

                updateBalance(listResult.get(indexTransaction).getValue(), listResult.get(indexTransaction).valueIsDeposit());

                listResult.remove(indexTransaction);

                listResult.addAll(otherResult);

                TransactionModelData tData = new TransactionModelData(new ArrayList<TransactionModel>());
                tData.setTmList(listResult);

                documentReference.set(tData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ToastUtil.showToast(v, "Transação excluída com sucesso!");
                        Log.d("db","Sucesso ao atualizar os dados");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToastUtil.showToast(v, "Erro ao excluir transação!");
                        Log.d("db","Erro ao atualizar os dados: " + e.toString());
                    }
                });
            }
        });
    }

    public void showTransactions(RecyclerView recyclerView, Activity activity, ProgressBar progressBar){

        updateUserId();

        DocumentReference documentReference = database.collection(transactionKey).document(userId);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> map = documentSnapshot.getData();

            List<HashMap> list;
            List<TransactionModel> dList = new ArrayList<>();
            List<TransactionModel> listResult = new ArrayList<>();

            if(map != null && map.get("tmList") != null) {
                list = (List<HashMap>) map.get("tmList");
                list.stream().forEach(i -> {
                    Object nome = i.get("name");
                    Object valor = i.get("value");
                    Object data = i.get("date");
                    Object deposito = i.get("deposit");
                    TransactionModel tm = new TransactionModel(nome.toString(), Double.parseDouble(valor.toString()));
                    tm.setDate(data.toString());
                    tm.setDeposit(Integer.valueOf(deposito.toString()));
                    dList.add(tm);
                });
            }

            dList.stream().forEach(v -> {
                if(v.getDateMonth().equals(month) && v.getDateYear().equals(year))
                    listResult.add(v);
            });

            Collections.sort(listResult);

            recyclerView.setAdapter(new TransactionAdapter(listResult));

            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    public void showUserData(TextView textName){
        updateUserId();

        DocumentReference documentReference = database.collection(userKey).document(userId);

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){
                textName.setText(documentSnapshot.getString("name"));
            }
        });
    }

    public void showUserData(TextView textValue, TextView textName){
        updateUserId();

        DocumentReference documentReference = database.collection(userKey).document(userId);

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){
                textValue.setText(MaskEditUtil.addValueMask(Double.valueOf(documentSnapshot.getDouble("balance").toString())));
                textName.setText(documentSnapshot.getString("name"));
            }
        });
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

    private void uploadImage(View viewImage){

        updateUserId();

        byte[] image = convertViewToByte(viewImage);
        final String path = pathImage.replace("x", userId);
        StorageReference reference = storage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("metadata", "testMeta")
                .build();

        UploadTask uploadTask = reference.putBytes(image, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("IMAGEM PERFIL:", "Upload da imagem feita com sucesso!");
            }
        });
    }

    private byte[] convertViewToByte(View image){
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = image.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image.setDrawingCacheEnabled(false);
        return baos.toByteArray();
    }

    public void getProfileImage(ImageView imageView){

        updateUserId();

        final String path = pathImage.replace("x", userId);
        StorageReference reference = storage.getReference(path);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {String urlS = uri.toString();
                    URL url = new URL(urlS);
                    InputStream urlStream = url.openStream();
                    Bitmap image = BitmapFactory.decodeStream(urlStream);
                    imageView.setImageBitmap(image);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUserId(){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private AppService() {
    }

    public static AppService getInstance() {
        if (instance == null)
            instance = new AppService();
        return instance;
    }

}
