package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EslesmeListele extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EslesmeAdapter adapter;
    private ArrayList<Eslesme> eslesmeList;

    private DocumentReference userRef2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eslesme_listele);

        mRecyclerView = findViewById(R.id.main_activity_recyclerViewE);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        eslesmeList = new ArrayList<>();
        adapter = new EslesmeAdapter(eslesmeList, this);
        mRecyclerView.setAdapter(adapter);

        getEslesmeFromFirestore();


    }

    private void getEslesmeFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("matchingRequests");


        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    eslesmeList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Eslesme eslesme = document.toObject(Eslesme.class);

                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String kullaniciId = currentUser.getUid();

                        if(eslesme.getReceiverUserID().equals(kullaniciId) || eslesme.getSenderUserID().equals(kullaniciId))
                         eslesmeList.add(eslesme);
                        Log.d("Firestore", "EKLENDİ " + eslesme.getTimestamp());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting users: " + task.getException());
                }
            }
        });
    }
}
