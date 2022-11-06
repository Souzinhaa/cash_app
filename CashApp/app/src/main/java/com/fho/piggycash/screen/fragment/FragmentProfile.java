package com.fho.piggycash.screen.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.fho.piggycash.R;
import com.fho.piggycash.screen.activity.ActivityLogin;
import com.fho.piggycash.service.AppService;
import com.fho.piggycash.util.ToastUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class FragmentProfile extends Fragment {

    private final AppService appService = AppService.getInstance();

    private TextView text_name;
    private AppCompatButton bt_exit;

    public FragmentProfile(){
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blue));

        initComponents();

        bt_exit.setOnClickListener(v -> {
            ToastUtil.showToast(requireView(), "Obrigado por usar nosso APP :)");
            loginScreen();
            FirebaseAuth.getInstance().signOut();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initComponents();
        appService.showUserData(text_name);
    }

    public void initComponents(){
        initImage();
        text_name = requireActivity().findViewById(R.id.text_name_perfil);
        bt_exit = requireActivity().findViewById(R.id.bt_exit);


        EditText edit_email = requireActivity().findViewById(R.id.edit_email);
        edit_email.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
    }

    public void initImage(){
        ImageView img_profile = requireActivity().findViewById(R.id.img_profile);
        ImageView img_main = requireActivity().findViewById(R.id.img_main_profile);

        img_main.setDrawingCacheEnabled(true);
        img_main.buildDrawingCache();
        Bitmap bitmap = img_main.getDrawingCache();

        img_profile.setImageBitmap(bitmap);

        appService.getProfileImage(img_profile);
    }

    private void loginScreen(){
        Intent intent = new Intent(getActivity(), ActivityLogin.class);
        startActivity(intent);
    }
}