package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Kullanicilar {
    private String name;
    private String surname;
    private String education;
    private String phone;
    private String status;
    private String stayDuration;
    private String locationX;
    private String locationY;
    private String distance;

    private String email;

    public Kullanicilar() {
        // Boş yapıcı metod gereklidir Firestore'dan verileri alırken
    }



    public Kullanicilar(String email, String name, String surname, String education, String phone, String status, String stayDuration, String locationX, String locationY, String distance) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.education = education;
        this.phone = phone;
        this.status = status;
        this.stayDuration = stayDuration;
        this.locationX = locationX;
        this.locationY = locationY;
        this.distance = distance;
    }

    // Getter ve setter metodları
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(String stayDuration) {
        this.stayDuration = stayDuration;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String  getDistance() {
        return distance;
    }


    static public ArrayList<Kullanicilar> getArray() {
        ArrayList<Kullanicilar> kullanicilarArray = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Kullanicilar user = document.toObject(Kullanicilar.class);
                        kullanicilarArray.add(user);
                        Log.d("Firestore", "EKLEDİ " +user.getName());
                    }
                } else {
                    Log.d("Firestore", "Error getting users: " + task.getException());
                }
            }
        });

        return kullanicilarArray;
    }

}
