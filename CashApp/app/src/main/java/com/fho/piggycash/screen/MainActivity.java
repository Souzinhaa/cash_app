package com.fho.piggycash.screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fho.piggycash.R;
import com.fho.piggycash.adapter.TransacaoAdapter;
import com.fho.piggycash.model.TransactionModel;
import com.fho.piggycash.model.TransactionModelData;
import com.fho.piggycash.model.UserData;
import com.fho.piggycash.service.SignUp;
import com.fho.piggycash.util.MaskEditUtil;
import com.fho.piggycash.util.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private FragmentContainerView fragment_view;
    private ImageView img_profile, img_home;
    private FragmentManager fm;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        iniciarComponentes();

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaMainFragment();
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaProfileFragment();
            }
        });
    }

    private void iniciarComponentes(){
        iniciarImagem();
        fm = getSupportFragmentManager();
        fragment_view = findViewById(R.id.fragment_view);
        img_home = findViewById(R.id.img_home);
        window = getWindow();
        chamaMainFragment();
    }

    private void iniciarImagem(){
        img_profile = findViewById(R.id.img_main_profile);
        getImage();
    }

    private void chamaMainFragment() {
        fm.beginTransaction()
                .replace(R.id.fragment_view, MainFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void chamaProfileFragment() {
        fm.beginTransaction()
                .replace(R.id.fragment_view, ProfileFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    public void getImage(){
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String path = "usuarios/perfil/" + usuarioId + ".png";
        StorageReference reference = storage.getReference(path);

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {String urlS = uri.toString();
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
}