package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

// AnaSayfaActivity.java

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AnaSayfa extends AppCompatActivity {
    private TextView nameTextView;
    private Button settingsButton;
    private Button anasayfa;
    private Button haritadaGoster;
    private Button eslesmeleriGoster;
    private ImageView logoImageView;
    private static final int MY_PERMISSION_REQUEST_CODE = 100; // Örnek istek kodu
    private static final int REQUEST_CODE = 1;

    private String adSoyad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.VIBRATE}, MY_PERMISSION_REQUEST_CODE);
        }


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        logoImageView = findViewById(R.id.logoImageView);
        anasayfa = findViewById(R.id.listUsersButton);
        nameTextView = findViewById(R.id.nameTextView);
        settingsButton = findViewById(R.id.settingsButton);
        haritadaGoster = findViewById(R.id.mapSearchButton);
        eslesmeleriGoster = findViewById(R.id.eslesmeTalepleri);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("matchingRequests")
                .whereEqualTo("receiverUserID", userId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Eşleşme taleplerini dinlerken bir hata oluştu", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Log.e(TAG, "Eşleşme var", error);
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            // İzin zaten verilmişse, bildirimi göster
                            showMatchingRequestsNotification(value.size());
                        } else {
                            // İzin verilmemişse, izin isteği göster
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
                        }
                    }
                });

        firestore.collection("matchingRequests")
                .whereEqualTo("senderUserID", userId)
                .whereEqualTo("status", "Kabul Edildi")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Eşleşme taleplerini dinlerken bir hata oluştu", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Log.e(TAG, "Eşleşme var", error);
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            // İzin zaten verilmişse, bildirimi göster
                            showMatchingRequestsNotification2(value.size());
                        } else {
                            // İzin verilmemişse, izin isteği göster
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
                        }
                    }
                });

        firestore.collection("matchingRequests")
                .whereEqualTo("senderUserID", userId)
                .whereEqualTo("status", "Reddedildi")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Eşleşme taleplerini dinlerken bir hata oluştu", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Log.e(TAG, "Eşleşme var", error);
                        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            // İzin zaten verilmişse, bildirimi göster
                            showMatchingRequestsNotification3(value.size());
                        } else {
                            // İzin verilmemişse, izin isteği göster
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
                        }
                    }
                });

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            adSoyad = documentSnapshot.getString("name");
                            adSoyad += " " + documentSnapshot.getString("surname");
                            // Do something with the adSoyad value
                        } else {
                            // Document does not exist
                        }
                        nameTextView.setText(adSoyad);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String storagePath = "photos/" + userId + ".jpg";
        StorageReference imageRef = storageRef.child(storagePath);


        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                logoImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AnaSayfa.this, Guncelle_Sil.class);
                finish();
                startActivity(intent);
            }
        });

        anasayfa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnaSayfa.this, KullaniciListele.class);
                startActivity(intent);
            }
        });

        haritadaGoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnaSayfa.this, HaritadaGoster.class);
                startActivity(intent);
            }
        });

        eslesmeleriGoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnaSayfa.this, EslesmeListele.class);
                startActivity(intent);
            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel Name";
            String channelDescription = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showMatchingRequestsNotification(int adet) {
        String channelId = "channel_id";
        String channelName = "Channel Name";
        String channelDescription = "Channel Description";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.default_profile)
                .setContentTitle("Eşleşme Talebi")
                .setContentText("Bekleyen "+adet+" adet eşleşme talebi var.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // İzin zaten verilmişse, bildirimi göster
        } else {
            // İzin verilmemişse, izin isteği göster
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Kullanıcı daha önce izni reddetmediyse, izin isteği göster
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            } else {
                // Kullanıcı daha önce izni reddetti, açıklama gösterme seçeneği yok
                // İşleme devam etmek için alternatif bir yaklaşım seçebilirsiniz
            }
        }
        notificationManager.notify(0, builder.build());
    }

    private void showMatchingRequestsNotification2(int adet) {
        String channelId = "channel_id";
        String channelName = "Channel Name";
        String channelDescription = "Channel Description";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.default_profile)
                .setContentTitle("Eşleşme Talebi")
                .setContentText(adet+" eşleşme talebiniz kabul edildi!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // İzin zaten verilmişse, bildirimi göster
        } else {
            // İzin verilmemişse, izin isteği göster
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Kullanıcı daha önce izni reddetmediyse, izin isteği göster
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            } else {
                // Kullanıcı daha önce izni reddetti, açıklama gösterme seçeneği yok
                // İşleme devam etmek için alternatif bir yaklaşım seçebilirsiniz
            }
        }
        notificationManager.notify(1, builder.build());
    }

    private void showMatchingRequestsNotification3(int adet) {
        String channelId = "channel_id";
        String channelName = "Channel Name";
        String channelDescription = "Channel Description";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.default_profile)
                .setContentTitle("Eşleşme Talebi")
                .setContentText(adet+" eşleşme talebiniz reddedildi!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // İzin zaten verilmişse, bildirimi göster
        } else {
            // İzin verilmemişse, izin isteği göster
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Kullanıcı daha önce izni reddetmediyse, izin isteği göster
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            } else {
                // Kullanıcı daha önce izni reddetti, açıklama gösterme seçeneği yok
                // İşleme devam etmek için alternatif bir yaklaşım seçebilirsiniz
            }
        }
        notificationManager.notify(2, builder.build());
    }


}
