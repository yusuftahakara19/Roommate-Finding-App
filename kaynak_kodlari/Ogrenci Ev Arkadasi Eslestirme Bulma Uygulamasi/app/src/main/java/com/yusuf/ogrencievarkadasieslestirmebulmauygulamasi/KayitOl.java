package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class KayitOl extends AppCompatActivity {

    private EditText etEmail, etPassword, etName, etSurname, etEducation, etDistance, etStayDuration, etPhone;
    private RadioGroup radioGroupStatus;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etSurname = findViewById(R.id.et_surname);
        etEducation = findViewById(R.id.egitimBilgileri);
        etDistance = findViewById(R.id.kampuseIstenenEvUzakligi);
        etStayDuration = findViewById(R.id.evdeKalacagiSure);
        etPhone = findViewById(R.id.telefon);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        final String name = etName.getText().toString();
        final String surname = etSurname.getText().toString();
        final String education = etEducation.getText().toString();
        final String distance = etDistance.getText().toString();
        final String stayDuration = etStayDuration.getText().toString();
        final String phone = etPhone.getText().toString();

        int selectedRadioButtonId = radioGroupStatus.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadioButtonId);
        final String status = radioButton.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "E-posta adresini giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Şifreyi giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "İsim giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(surname)) {
            Toast.makeText(getApplicationContext(), "Soyad giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(education)) {
            Toast.makeText(getApplicationContext(), "Bölüm/Sınıf giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(distance)) {
            Toast.makeText(getApplicationContext(), "Kampüse İstenen Ev Uzaklığı (km) giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(stayDuration)) {
            Toast.makeText(getApplicationContext(), "Evde/Odada kalacağınız süreyi giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "Telefonunuzu giriniz!", Toast.LENGTH_SHORT).show();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(KayitOl.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String userId = currentUser.getUid();

                            // Kullanıcının diğer bilgilerini Firestore veritabanına kaydetme
                            DocumentReference userRef = mFirestore.collection("users").document(userId);
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("surname", surname);
                            userData.put("education", education);
                            userData.put("distance", distance);
                            userData.put("stayDuration", stayDuration);
                            userData.put("phone", phone);
                            userData.put("status", status);
                            userData.put("locationX", "41.024120");
                            userData.put("locationY", "28.892456");
                            userData.put("email",email);

                            userRef.set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendEmailVerification();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Kullanıcı bilgileri kaydedilirken bir hata oluştu!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Kullanıcı kaydı başarısız oldu. Tekrar deneyin!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Kaydınız başarıyla tamamlandı. Doğrulama e-postası gönderildi.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Doğrulama e-postası gönderilirken bir hata oluştu!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
