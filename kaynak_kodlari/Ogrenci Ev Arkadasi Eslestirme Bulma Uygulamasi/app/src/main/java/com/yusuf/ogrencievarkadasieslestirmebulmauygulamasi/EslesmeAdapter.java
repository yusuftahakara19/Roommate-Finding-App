package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EslesmeAdapter extends RecyclerView.Adapter<EslesmeAdapter.EslesmeHolder> {
    private ArrayList<Eslesme> eslesmeList;
    private Context context;

    public EslesmeAdapter(ArrayList<Eslesme> eslesmeList, Context context) {
        this.eslesmeList = eslesmeList;
        this.context = context;
    }
    public void setEslesmeList(ArrayList<Eslesme> eslesmeList) {
        this.eslesmeList = eslesmeList;
        notifyDataSetChanged(); // Adapter'a veri setinin güncellendiğini bildir
    }
    @NonNull
    @Override
    public EslesmeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.eslesme_item,parent,false);
        return new EslesmeHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EslesmeHolder holder, int position) {
        Eslesme eslesmeler = eslesmeList.get(position);
        holder.setData(eslesmeler);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String kullaniciId = currentUser.getUid();

        if(!eslesmeler.getSenderUserID().equals(kullaniciId)){


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                Timestamp timestamp = eslesmeler.getTimestamp();
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String tarih = sdf.format(date);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersRef = db.collection("users");

                String userID = eslesmeler.getSenderUserID();

                usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String kullaniciAdi = document.getString("name");
                                String soyad = document.getString("surname");
                                Log.d("Firestore", "Kullanıcı Adı: " + kullaniciAdi);
                            } else {
                                Log.d("Firestore", "Kullanıcı belgesi bulunamadı");
                            }
                        } else {
                            Log.d("Firestore", "Kullanıcı bilgileri alınamadı: " + task.getException());
                        }
                    }
                });

                builder.setTitle("EŞLEŞME İSTEĞİ");
                builder.setMessage("Eşleşmeyi kabul etmek istediğinize emin misiniz?");

                builder.setPositiveButton("Kabul Et", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference eslesmelerRef = db.collection("matchingRequests");

// Kullanıcının eşleşme bilgilerini temsil eden belgeyi sorgula
                        eslesmelerRef.whereEqualTo("senderUserID", eslesmeler.getSenderUserID())
                                .whereEqualTo("receiverUserID", eslesmeler.getReceiverUserID())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // Sorgu sonuçlarını işleyin
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Belgeyi güncelleyin
                                                eslesmelerRef.document(document.getId())
                                                        .update("status", "Kabul Edildi")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Güncelleme başarılı
                                                                Log.d("AAA", "Eşleşme durumu güncellendi.");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Güncelleme başarısız
                                                                Log.d("AAA", "Eşleşme durumu güncellenemedi: " + e.getMessage());
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("AAA", "Eşleşme bilgileri alınamadı: " + task.getException());
                                        }
                                    }
                                });
                    }
                });

                builder.setNegativeButton("Reddet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference eslesmelerRef = db.collection("matchingRequests");

// Kullanıcının eşleşme bilgilerini temsil eden belgeyi sorgula
                        eslesmelerRef.whereEqualTo("senderUserID", eslesmeler.getSenderUserID())
                                .whereEqualTo("receiverUserID", eslesmeler.getReceiverUserID())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // Sorgu sonuçlarını işleyin
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Belgeyi güncelleyin
                                                eslesmelerRef.document(document.getId())
                                                        .update("status", "Reddedildi")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Güncelleme başarılı
                                                                Log.d("AAA", "Eşleşme durumu güncellendi.");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Güncelleme başarısız
                                                                Log.d("AAA", "Eşleşme durumu güncellenemedi: " + e.getMessage());
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("AAA", "Eşleşme bilgileri alınamadı: " + task.getException());
                                        }
                                    }
                                });

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } );
    }


    }

    @Override
    public int getItemCount() {
        Log.d("Firestore", "SAYI " +eslesmeList.size());

        return eslesmeList.size();
    }

    class EslesmeHolder extends RecyclerView.ViewHolder{
        TextView gonderen,durum,tarih,alici;

        public EslesmeHolder(@NonNull View itemView) {
            super(itemView);
            gonderen = (TextView)itemView.findViewById(R.id.eslesme_item_SenderIsim);
            alici =(TextView)itemView.findViewById(R.id.eslesme_item_ReceiverIsim);
            durum=(TextView)itemView.findViewById(R.id.eslesme_item_Durum);
            tarih=(TextView)itemView.findViewById(R.id.eslesme_item_GondermeTarihi);
        }

        public void setData(Eslesme eslesmeler) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            String userID = eslesmeler.getSenderUserID();
            String userID2 = eslesmeler.getReceiverUserID();

            usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String kullaniciAdi = document.getString("name");
                            String soyad = document.getString("surname");
                            EslesmeHolder.this.gonderen.setText("İstek Gönderen : "+kullaniciAdi+" "+soyad);

                            Log.d("Firestore", "Kullanıcı Adı: " + kullaniciAdi);
                        } else {
                            Log.d("Firestore", "Kullanıcı belgesi bulunamadı");
                        }
                    } else {
                        Log.d("Firestore", "Kullanıcı bilgileri alınamadı: " + task.getException());
                    }
                }
            });
            usersRef.document(userID2).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String kullaniciAdi = document.getString("name");
                            String soyad = document.getString("surname");
                            EslesmeHolder.this.alici.setText("İstek Alan : "+kullaniciAdi+" "+soyad);

                            Log.d("Firestore", "Kullanıcı Adı: " + kullaniciAdi);
                        } else {
                            Log.d("Firestore", "Kullanıcı belgesi bulunamadı");
                        }
                    } else {
                        Log.d("Firestore", "Kullanıcı bilgileri alınamadı: " + task.getException());
                    }
                }
            });


            if(eslesmeler.getStatus().equals("pending"))
                this.durum.setText("Bekliyor");
            else
                this.durum.setText(eslesmeler.getStatus());

            // Timestamp'i TextView'e ayarla
            com.google.firebase.Timestamp timestamp = eslesmeler.getTimestamp();
            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(date);
                this.tarih.setText(formattedDate);
            } else {
                this.tarih.setText("");
            }
        }

    }
}
