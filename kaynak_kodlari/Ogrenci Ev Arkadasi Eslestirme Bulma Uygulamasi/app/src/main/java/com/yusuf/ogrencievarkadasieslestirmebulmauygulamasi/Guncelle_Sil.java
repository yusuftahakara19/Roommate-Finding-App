package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Guncelle_Sil extends AppCompatActivity {

    private TextView nameTextView;

    private TextView surname;

    private TextView bolumSinif;

    private TextView kampuseUzaklik;

    private TextView evdekiSure;

    private TextView phone;
    private TextView locationX;
    private TextView locationY;
    private EditText newPassword;

    private String durum;
    private RadioButton rdButton1;
    private RadioButton rdButton2;

    private ImageView profileImageView;
    private Button updateButton;
    private Button changePasswordButton;
    private Button deleteAccountButton;
    private Button cameraButton;
    private Button galleryButton;
    private Button locationButton;
    private RadioGroup radioGroupStatus;
    private FirebaseFirestore db;
    private DocumentReference userRef;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 100;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;

    private Uri photoUri;
    private String kullaniciId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


// Mevcut kullanıcının ID'sini alma
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        kullaniciId = currentUser.getUid();

        nameTextView = findViewById(R.id.et_name);
        surname = findViewById(R.id.et_surname);
        bolumSinif = findViewById(R.id.egitimBilgileri);
        kampuseUzaklik = findViewById(R.id.kampuseIstenenEvUzakligi);
        evdekiSure = findViewById(R.id.evdeKalacagiSure);
        phone = findViewById(R.id.telefon);
        rdButton1 = findViewById(R.id.radioOption1);
        rdButton2 = findViewById(R.id.radioOption2);
        locationX = findViewById(R.id.tv_location_x);
        locationY = findViewById(R.id.tv_location_y);
        profileImageView = findViewById(R.id.iv_profile_photo);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        newPassword = findViewById(R.id.et_password);


        updateButton = findViewById(R.id.updateInfoButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        locationButton = findViewById(R.id.btn_get_location);
        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Firestore bağlantısını oluşturun
        db = FirebaseFirestore.getInstance();

        // Firestore ayarlarını yapılandırın (gerektiğinde kullanabilirsiniz)
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // Kullanıcı referansını alın
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String storagePath = "photos/" + userId + ".jpg"; // Firestore Storage'daki yol
        StorageReference imageRef = storageRef.child(storagePath);

// Resmi indirme işlemini burada gerçekleştirin ve ImageView'a yükleyin
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Byte dizisini Bitmap'e dönüştürün
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Bitmap'i ImageView'e yükleyin

                profileImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {



            }
        });



        userRef = db.collection("users").document(userId);

        // Kullanıcı verilerini Firestore'dan alın ve nesneleri doldurun
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String uzaklik = documentSnapshot.getString("distance");
                    String education = documentSnapshot.getString("education");
                    String locationXS = documentSnapshot.getString("locationX");
                    String locationYS = documentSnapshot.getString("locationY");
                    String phoneS = documentSnapshot.getString("phone");
                    String status = documentSnapshot.getString("status");
                    String surnameS = documentSnapshot.getString("surname");
                    String stayDuration = documentSnapshot.getString("stayDuration");

                    String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                    // Verileri nesnelere doldurun
                    nameTextView.setText(name);
                    surname.setText(surnameS);
                    bolumSinif.setText(education);
                    kampuseUzaklik.setText(uzaklik);
                    evdekiSure.setText(stayDuration);
                    phone.setText(phoneS);
                    locationX.setText(locationXS);
                    locationY.setText(locationYS);
                    if (status.equals("Kalacak Ev/Oda arıyor")) {
                        rdButton1.setChecked(true);
                    } else {
                        rdButton2.setChecked(true);
                    }

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Profil resmini yükleyin
                        Glide.with(Guncelle_Sil.this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.default_profile)
                                .into(profileImageView);
                    }
                }
            }
        });

        // Diğer butonların tıklama olaylarını burada tanımlayabilirsiniz
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference userRef = db.collection("users").document(kullaniciId); // Kullanıcının belgesinin referansı

// Yeni değerleri alın
                String yeniAd = nameTextView.getText().toString();
                String yeniSoyad = surname.getText().toString();
                String newEducation = bolumSinif.getText().toString();
                String newDistance = kampuseUzaklik.getText().toString();
                String newTime = evdekiSure.getText().toString();
                String newPhone = phone.getText().toString();
                int selectedRadioButtonId = radioGroupStatus.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedRadioButtonId);
                final String newStatus = radioButton.getText().toString();
                String newX = locationX.getText().toString();
                String newY = locationY.getText().toString();
// Güncelleme işlemi
                userRef.update(
                                "distance", newDistance,
                                "education", newEducation,
                                "locationX", newX,
                                "locationY", newY,
                                "name", yeniAd,
                                "phone", newPhone,
                                "status", newStatus,
                                "stayDuration", newTime,
                                "surname", yeniSoyad
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Güncelleme başarılı
                                Toast.makeText(getApplicationContext(), "Bilgiler güncellendi.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Güncelleme başarısız
                                Toast.makeText(getApplicationContext(), "Bilgiler güncellenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = newPassword.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Şifre güncelleme başarılı
                                        Toast.makeText(getApplicationContext(), "Şifre başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Şifre güncelleme başarısız
                                        Toast.makeText(getApplicationContext(), "Şifre güncelleme hatası: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Guncelle_Sil.this);
                builder.setTitle("Kaydı Sil");
                builder.setMessage("Hesabınızı silmek istediğinize emin misiniz?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Firestore veritabanından kullanıcının verilerini silme
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String kullaniciId = user.getUid();
                            DocumentReference docRef = db.collection("users").document(kullaniciId);
                            docRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Firestore'dan kullanıcının verileri başarıyla silindi
                                     //       Toast.makeText(getApplicationContext(), "Firestore'dan veriler başarıyla silindi", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Firestore'dan kullanıcının verilerini silme hatası
                                            //Toast.makeText(getApplicationContext(), "Kullanici" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            // Firebase Authentication üzerinden kullanıcıyı silme
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Hesap silme başarılı
                                                Toast.makeText(getApplicationContext(), "Hesap başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Guncelle_Sil.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Hesap silme başarısız
                                                Toast.makeText(getApplicationContext(), "Hesap silme hatası: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
                builder.setNegativeButton("Hayır", null);
                builder.show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Guncelle_Sil.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Guncelle_Sil.this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    openCamera();
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Galeriye erişim için intent oluştur
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Guncelle_Sil.this,KonumGuncelle.class);
                finish();
                startActivity(intent);
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Guncelle_Sil.this,AnaSayfa.class);
        finish();
        startActivity(intent);
    }


    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Kamera uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Galeriden fotoğraf seçildiğinde çalışır
            Uri selectedImageUri = data.getData();
            try {
                // Uri'yi Bitmap'e dönüştür
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);

                // Bitmap'i byte dizisine dönüştür
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Firestore'a fotoğrafı kaydet
                uploadImageToFirestore(imageData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirestore(byte[] imageData) {
        // Firestore'da yeni bir belge oluştur
        Map<String, Object> photoData = new HashMap<>();
        photoData.put("timestamp", System.currentTimeMillis());

        // Firestore'da belgeyi kaydet
        firestore.collection("photos")
                .add(photoData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Kaydedilen belgenin ID'sini al
                        String documentId = documentReference.getId();
                        // Storage referansını oluştur
                        StorageReference imageRef = storageRef.child("photos/" + kullaniciId + ".jpg");
                        // Fotoğrafı Storage'a yükle
                        UploadTask uploadTask = imageRef.putBytes(imageData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Fotoğrafı Storage'a yüklerken hata oluştu.", e);
                                Toast.makeText(Guncelle_Sil.this, "Fotoğrafı kaydederken hata oluştu.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Guncelle_Sil.this, "Fotoğraf başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Fotoğrafı Firestore'a kaydederken hata oluştu.", e);
                        Toast.makeText(Guncelle_Sil.this, "Fotoğrafı kaydederken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





