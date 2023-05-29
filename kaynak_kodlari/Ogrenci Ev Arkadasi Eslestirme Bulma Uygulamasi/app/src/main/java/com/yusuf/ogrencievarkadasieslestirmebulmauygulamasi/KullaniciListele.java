package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class KullaniciListele extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private KullanicilarAdapter adapter;
    private ArrayList<Kullanicilar> kullanicilarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanicilar);

        mRecyclerView = findViewById(R.id.main_activity_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        kullanicilarList = new ArrayList<>();
        adapter = new KullanicilarAdapter(kullanicilarList, this);
        mRecyclerView.setAdapter(adapter);

        getKullanicilarFromFirestore();
    }

    private void getKullanicilarFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    kullanicilarList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Kullanicilar user = document.toObject(Kullanicilar.class);
                        kullanicilarList.add(user);
                        Log.d("Firestore", "EKLENDİ " + user.getName());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting users: " + task.getException());
                }
            }
        });
    }
}
