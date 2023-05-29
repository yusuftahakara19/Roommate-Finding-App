package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class HaritadaGoster extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private Query usersQuery;
    private Map<String, Marker> markers;
    private Circle circle; // Circle nesnesini tanımlayın
    private Marker currentUserMarker; // Mevcut kullanıcı işaretçisi
    private EditText radiusEditText;

    private FirebaseFirestore db;
    private DocumentReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haritada_goster);
        radiusEditText = findViewById(R.id.distanceEditText);
        // Firestore veritabanı bağlantısını oluştur
        firestore = FirebaseFirestore.getInstance();

        // Kullanıcılar koleksiyonundan sorgu oluştur
        usersQuery = firestore.collection("users");

        // İşaretçi (marker) koleksiyonunu oluştur
        markers = new HashMap<>();

        // Harita görünümünü al
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mapFragment, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);

        radiusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Metin değişmeden önceki durumu işlemek için gerekli olan kodları buraya yazabilirsiniz.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Metin değiştiğinde yapılması gereken işlemleri burada gerçekleştirebilirsiniz.
                // Yeni girilen değeri alıp çemberi yeniden çizmek için drawCircle() metodunu çağırabilirsiniz.
                String radiusString = charSequence.toString();
                if (!radiusString.isEmpty()) {
                    int radius = Integer.parseInt(radiusString);
                    drawCircle(radius);
                }
            }

            private void drawCircle(double radius) {

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                userRef = db.collection("users").document(userId);

                // Kullanıcı verilerini Firestore'dan alın ve nesneleri doldurun
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String locationXS = documentSnapshot.getString("locationX");
                            String locationYS = documentSnapshot.getString("locationY");

                            Location currentUserLocation = new Location("");
                            currentUserLocation.setLatitude(Double.valueOf(locationXS));
                            currentUserLocation.setLongitude(Double.valueOf(locationYS));

                            if (currentUserLocation != null) {
                                LatLng center = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());

                                // Önceki daireyi kaldırın (eğer mevcutsa)
                                if (circle != null) {
                                    circle.remove();
                                }

                                // Daireyi haritaya çizin
                                CircleOptions circleOptions = new CircleOptions()
                                        .center(center)
                                        .radius(radius*1000)
                                        .strokeWidth(2)
                                        .strokeColor(Color.BLUE)
                                        .fillColor(Color.argb(70, 0, 0, 255));
                                circle = mMap.addCircle(circleOptions);

                                // Mevcut kullanıcı işaretçisini yeşil ikonla güncelleyin
                                if (currentUserMarker != null) {
                                    currentUserMarker.remove();
                                }
                                currentUserMarker = mMap.addMarker(new MarkerOptions()
                                        .position(center)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title("Mevcut Kullanıcı"));
                                currentUserMarker.showInfoWindow();

                                // Daire ve kullanıcı işaretçisi görünümünü güncelleyin
                                updateMapViewport(center, radius);
                            }
                        }
                    }
                });
            }

            private void updateMapViewport(LatLng center, double radius) {
                // Haritanın görüntüsünü güncelleyin ve çemberi tamamen görünür hale getirin
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(center);
                LatLngBounds bounds = builder.build();
                int padding = (int) (radius * 0.15); // Çemberin etrafında biraz boşluk bırakmak için padding değeri ayarlayın
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Metin değiştikten sonraki durumu işlemek için gerekli olan kodları buraya yazabilirsiniz.
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.clear();

            // Kullanıcıları al ve harita üzerinde göster
            usersQuery.addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    // Hata durumunda yapılacak işlemleri burada belirtebilirsiniz
                    return;
                }

                // İşaretçi koleksiyonunu temizle
                clearMarkers();

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            String userId = dc.getDocument().getId();
                            Kullanicilar kullanici = dc.getDocument().toObject(Kullanicilar.class);

                            // Kullanıcıyı harita üzerinde göster
                            showUserOnMap(userId, kullanici);
                            break;
                        case MODIFIED:
                            // Kullanıcının konum bilgisi veya durumu güncellendiğinde işlemler burada yapılabilir
                            break;
                        case REMOVED:
                            // Kullanıcı silindiğinde işlemler burada yapılabilir
                            break;
                    }
                }
            });

            // Kullanıcı tıklamalarını dinle
            mMap.setOnMarkerClickListener(marker -> {
                String userId = null;
                for (Map.Entry<String, Marker> entry : markers.entrySet()) {
                    if (entry.getValue().equals(marker)) {
                        userId = entry.getKey();
                        break;
                    }
                }
                if (userId != null) {
                    // Kullanıcı bilgilerini gösteren dialogu aç
                    showUserDetailsDialog(userId);
                }
                return true;
            });
        }
    }
    private void showUserOnMap(String userId, Kullanicilar kullanici) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Mevcut kullanıcının kimlik bilgisini alın
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        // Kullanıcının e-posta adresini alın
        firestore.collection("users").document(currentUserId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userEmail = documentSnapshot.getString("email");
                // Kullanıcının e-posta adresini kullanabilirsiniz

                // Kullanıcının konum bilgisi varsa işlemler yapılır
                if (kullanici.getLocationX() != null && kullanici.getLocationY() != null) {
                    double latitude = Double.parseDouble(kullanici.getLocationX());
                    double longitude = Double.parseDouble(kullanici.getLocationY());

                    // Konum bilgisi LatLng nesnesine dönüştürülür
                    LatLng latLng = new LatLng(latitude, longitude);

                    // İşaretçi ikonunu belirle
                    float markerColor = BitmapDescriptorFactory.HUE_RED;
                    if (kullanici.getStatus() != null && kullanici.getStatus().equals("Ev/Oda arkadaşı arıyor")) {
                        markerColor = BitmapDescriptorFactory.HUE_BLUE;
                    }
                    if (userEmail.equals(kullanici.getEmail())) {
                        markerColor = BitmapDescriptorFactory.HUE_GREEN;
                    }

                    // İşaretçiyi oluştur ve haritaya ekle
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(kullanici.getName())
                            .snippet(kullanici.getPhone())
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor));
                    Marker marker = mMap.addMarker(markerOptions);

                    // İşaretçiyi koleksiyona ekle
                    markers.put(userId, marker);

                    // Haritanın görüntüsünü güncelle
                    updateMapViewport();
                }
            }
        }).addOnFailureListener(e -> {
            // Hata durumunda işlemler
        });
    }

    private void clearMarkers() {
        // Tüm işaretçileri haritadan kaldır
        for (Marker marker : markers.values()) {
            marker.remove();
        }
        markers.clear();
    }

    private void updateMapViewport() {
        // Tüm işaretçilerin bulunduğu bir sınırlayıcı (bounds) oluştur
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers.values()) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        // Haritanın görüntüsünü işaretçilerin olduğu sınırlayıcıya (bounds) göre ayarla
        int padding = 100; // Harita kenarları ile işaretçiler arasındaki boşluk miktarı
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private void showUserDetailsDialog(String userId) {
        // Kullanıcı detaylarını al
        firestore.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Kullanicilar kullanici = documentSnapshot.toObject(Kullanicilar.class);
                if (kullanici != null) {
                    // Kullanıcı detaylarını gösteren dialogu oluştur
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Kullanıcı Detayları");
                    View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_details, null);
                    builder.setView(dialogView);

                    // Dialog içindeki görüntüleyicileri al
                    TextView txtAdSoyad = dialogView.findViewById(R.id.textViewName);
                    TextView txtEmail = dialogView.findViewById(R.id.textViewEmail);
                    TextView txtTelefon = dialogView.findViewById(R.id.textViewPhone);
                    TextView txtEducation = dialogView.findViewById(R.id.textViewBolum);
                    TextView txtMail = dialogView.findViewById(R.id.textViewEmail);
                    TextView txtSure = dialogView.findViewById(R.id.textViewSure);
                    TextView txtUzaklik = dialogView.findViewById(R.id.textViewDistance);
                    TextView txtDurum = dialogView.findViewById(R.id.textViewDurum);

                    // Görüntüleyicilere kullanıcı detaylarını yerleştir
                    txtAdSoyad.setText("Ad Soyad : "+kullanici.getName()+ " " + kullanici.getSurname());
                    txtEducation.setText("Bölüm/Sınıf : "+kullanici.getEducation());
                    txtEmail.setText("Email : "+kullanici.getEmail());
                    txtTelefon.setText("Telefon : "+kullanici.getPhone());
                    txtSure.setText( "Evde kalacağı/paylaşabileceği süre : " + kullanici.getStayDuration()+" saat");
                    txtSure.setText( "Kampüse Uzaklık : "+ kullanici.getDistance()+" km");
                    txtDurum.setText( "Durum :"+ kullanici.getStatus());

                    // Dialogu göster
                    builder.setPositiveButton("Eşleşme Talebi Oluştur", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            createMatchingRequest(userId);
                        }

                        private void createMatchingRequest(String receiverUserID) {

                            // Eşleşme talebi dokümanını oluştur
                            Map<String, Object> matchingRequest = new HashMap<>();
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            String kullaniciId = currentUser.getUid();
                            matchingRequest.put("senderUserID", kullaniciId); // Talebi gönderen kullanıcının ID'si
                            matchingRequest.put("receiverUserID", receiverUserID); // Talebin alıcısı kullanıcının ID'si
                            matchingRequest.put("status", "pending"); // Talebin başlangıç durumu (beklemede)
                            matchingRequest.put("timestamp", FieldValue.serverTimestamp()); // Talebin gönderildiği zaman damgası

                            // Eşleşme talebini Firestore'a ekle
                            firestore.collection("matchingRequests").add(matchingRequest)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getApplicationContext(), "Eşleşme Talebi başarıyla gönderildi.", Toast.LENGTH_SHORT).show();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Eşleşme Talebi gönderilemedi. Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();

                                    });
                        }

                    });
                    builder.setNegativeButton("Mail", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + kullanici.getEmail()));
                            HaritadaGoster.this.startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("WhatsApp", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String phoneNumberWithCountryCode = "+90" + kullanici.getPhone(); // replace with your country code and phone number
                            String message = "Merhaba "+kullanici.getName()+" "+kullanici.getSurname(); // replace with your message
                            String url = "https://api.whatsapp.com/send?phone=" + phoneNumberWithCountryCode + "&text=" + message;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            HaritadaGoster.this.startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
