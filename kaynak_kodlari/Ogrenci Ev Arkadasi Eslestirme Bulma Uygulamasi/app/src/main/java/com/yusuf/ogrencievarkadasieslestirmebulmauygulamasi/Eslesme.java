package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Eslesme {
    private String senderUserID;
    private String receiverUserID;
    private String status;
    private com.google.firebase.Timestamp timestamp;

    public String getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        this.senderUserID = senderUserID;
    }

    public String getReceiverUserID() {
        return receiverUserID;
    }

    public void setReceiverUserID(String receiverUserID) {
        this.receiverUserID = receiverUserID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Eslesme() {
        // Boş parametresiz yapıcı metod (gereklidir)
    }
    public Eslesme(String senderUserID, String receiverUserID, String status, com.google.firebase.Timestamp timestamp) {
        this.senderUserID = senderUserID;
        this.receiverUserID = receiverUserID;
        this.status = status;
        this.timestamp = timestamp;
    }

    static public ArrayList<Eslesme> getArray() {
        ArrayList<Eslesme> eslesmeArray = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("matchingRequests");

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Eslesme eslesme = document.toObject(Eslesme.class);
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        String kullaniciId = currentUser.getUid();
                     //   if(eslesme.getReceiverUserID().equals(kullaniciId))
                            eslesmeArray.add(eslesme);
                        Log.d("Firestore", "EKLEDİ " +eslesme.getReceiverUserID());
                    }
                } else {
                    Log.d("Firestore", "Error getting users: " + task.getException());
                }
            }
        });

        return eslesmeArray;
    }
}
