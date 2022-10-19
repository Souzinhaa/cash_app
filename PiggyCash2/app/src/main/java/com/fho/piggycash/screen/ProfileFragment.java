package com.fho.piggycash.screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fho.piggycash.R;
import com.fho.piggycash.adapter.TransacaoAdapter;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.service.SignUp;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private String usuarioId;

    private TextView text_nome, text_funcao;
    private ImageView img_profile;

    private EditText edit_email;

    private AppCompatButton bt_exit;

    public ProfileFragment(){
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getActivity().getColor(R.color.blue));

        iniciarComponentes();

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getView(), "Obrigado por usar nosso APP :)");
                telaLogin();
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public void iniciarComponentes(){
        initImage();
        text_nome = getActivity().findViewById(R.id.text_nome_perfil);
        text_funcao = getActivity().findViewById(R.id.text_funcao);
        edit_email = getActivity().findViewById(R.id.edit_email);
        bt_exit = getActivity().findViewById(R.id.bt_exit);
    }

    public void initImage(){
        img_profile = getActivity().findViewById(R.id.img_profile);
        ImageView img_main = getActivity().findViewById(R.id.img_main_profile);

        img_main.setDrawingCacheEnabled(true);
        img_main.buildDrawingCache();
        Bitmap bitmap = img_main.getDrawingCache();

        img_profile.setImageBitmap(bitmap);

        getImage();
    }

    @Override
    public void onStart() {
        super.onStart();

        iniciarComponentes();

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){
                String nome = documentSnapshot.getString("nome");
                text_nome.setText(nome);
                edit_email.setText(email);
            }
        });
    }

    public void getImage(){
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String path = "usuarios/perfil/" + usuarioId + ".png";
        StorageReference reference = storage.getReference(path);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    String urlS = uri.toString();
                    URL url = new URL(urlS);
                    InputStream urlStream = url.openStream();
                    Bitmap image = BitmapFactory.decodeStream(urlStream);
                    img_profile.setImageBitmap(image);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void telaLogin(){
        Intent intent = new Intent(getActivity(), LoginScreen.class);
        startActivity(intent);
    }
}