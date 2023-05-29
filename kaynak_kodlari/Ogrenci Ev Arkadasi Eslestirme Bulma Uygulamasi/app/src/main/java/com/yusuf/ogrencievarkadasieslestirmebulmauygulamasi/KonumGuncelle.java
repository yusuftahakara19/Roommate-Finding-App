package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class KonumGuncelle extends AppCompatActivity implements OnMapReadyCallback {

    private EditText enlemEditText;
    private EditText boylamEditText;
    private MapView mapView;
    private Marker selectedMarker;
    private Button mevcutKonumuGetirButton;
    private Button konumGuncelle;
    private   GoogleMap googleMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konum_guncelle);

        db = FirebaseFirestore.getInstance();

        enlemEditText = findViewById(R.id.enlemEditText);
        boylamEditText = findViewById(R.id.boylamEditText);
        mevcutKonumuGetirButton = findViewById(R.id.mevcutKonumButton);
        konumGuncelle = findViewById(R.id.konumGuncelleButton);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        // Konum Güncelle butonuna tıklama olayını ekle
        konumGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText'teki verileri al
                String enlem = enlemEditText.getText().toString().trim();
                String boylam = boylamEditText.getText().toString().trim();
                if(!enlem.isEmpty()&& !boylam.isEmpty()){
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String kullaniciId = currentUser.getUid();
                    DocumentReference userRef = db.collection("users").document(kullaniciId); // Kullanıcının belgesinin referansı
                    userRef.update(
                                    "locationX", enlem,
                                    "locationY", boylam
                            )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Güncelleme başarılı
                                    Toast.makeText(getApplicationContext(), "Konum bilgisi güncellendi.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Güncelleme başarısız
                                    Toast.makeText(getApplicationContext(), "Konum bilgileri güncellenirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

            }
        });


        mevcutKonumuGetirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Konum izinlerini kontrol et
                if (checkLocationPermission()) {
                    // LocationManager ve LocationListener oluştur
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            // Konumu al
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();


                            enlemEditText.setText(String.valueOf(String.valueOf(latitude)));
                            boylamEditText.setText(String.valueOf(String.valueOf(longitude)));
                            // Konumu kullanarak işlemleri gerçekleştir
                            // Örneğin, konumu haritada işaretle
                            LatLng currentLocation = new LatLng(latitude, longitude);
                            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Mevcut Konum"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10f));

                            // Konum güncellendi mesajı göster
                            Toast.makeText(getApplicationContext(), "Mevcut Konum Getirildi", Toast.LENGTH_SHORT).show();
                            locationManager.removeUpdates(this);

                        }

                        // Diğer LocationListener metotları
                        // ...

                    };

                    // Konum güncellemelerini başlat
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }

            private boolean checkLocationPermission() {
                if (ContextCompat.checkSelfPermission(KonumGuncelle.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // İzin iste
                    ActivityCompat.requestPermissions(KonumGuncelle.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                    return false;
                } else {
                    return true;
                }
            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(KonumGuncelle.this,Guncelle_Sil.class);
        finish();
        startActivity(intent);
    }
    @Override
    public void onMapReady(GoogleMap map) {
         this.googleMap = map;

        // Haritayı yapılandırın
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Haritada tıklanma olayını dinleyin
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }

                // Tıklanan konumu işaretleyin
                selectedMarker = googleMap.addMarker(new MarkerOptions().position(latLng));

                // Enlem ve boylamı güncelleyin
                enlemEditText.setText(String.valueOf(latLng.latitude));
                boylamEditText.setText(String.valueOf(latLng.longitude));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}