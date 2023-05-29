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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class KullanicilarAdapter extends RecyclerView.Adapter<KullanicilarAdapter.KullanicilarHolder> {
   private ArrayList<Kullanicilar> kullanicilarList;
   private Context context;

    public KullanicilarAdapter(ArrayList<Kullanicilar> kullanicilarList, Context context) {
        this.kullanicilarList = kullanicilarList;
        this.context = context;
    }

    @NonNull
    @Override
    public KullanicilarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(context).inflate(R.layout.kullanici_item,parent,false);
        return new KullanicilarHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KullanicilarHolder holder, int position) {
        Kullanicilar kullanicilar = kullanicilarList.get(position);
        holder.setData(kullanicilar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Kullanıcı Bilgileri");
                builder.setMessage(

                        "İsim Soyisim : " + kullanicilar.getName() + " " + kullanicilar.getSurname() + "\n" +
                                "Bölüm/Sınıf : " + kullanicilar.getEducation() + "\n" +
                                "Telefon : " + kullanicilar.getPhone() + "\n" +
                                "Mail : "+ kullanicilar.getEmail()  +"\n"+
                                "Evde kalacağı/paylaşabileceği süre : " + kullanicilar.getStayDuration()+" saat" + "\n" +
                                "Kampüse Uzaklık : "+ kullanicilar.getDistance()+" km" + "\n" +
                                "Durum :"+ kullanicilar.getStatus()
                );
                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + kullanicilar.getEmail()));
                        context.startActivity(intent);
                    }
                });
                builder.setNeutralButton("WhatsApp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumberWithCountryCode = "+90" + kullanicilar.getPhone(); // replace with your country code and phone number
                        String message = "Merhaba "+kullanicilar.getName()+" "+kullanicilar.getSurname(); // replace with your message
                        String url = "https://api.whatsapp.com/send?phone=" + phoneNumberWithCountryCode + "&text=" + message;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("Firestore", "SAYI " +kullanicilarList.size());

        return kullanicilarList.size();
    }

    class KullanicilarHolder extends RecyclerView.ViewHolder{
        TextView kullaniciIsmi,kullaniciEgitim,kullaniciDurum,kampuseUzaklik;
        ImageView kullaniciResim;
        public KullanicilarHolder(@NonNull View itemView) {
            super(itemView);
            kullaniciIsmi = (TextView)itemView.findViewById(R.id.kullanici_item_textViewKullaniciIsim);
            kullaniciEgitim=(TextView)itemView.findViewById(R.id.kullanici_item_textViewKullaniciEgitim);
            kullaniciDurum=(TextView)itemView.findViewById(R.id.kullanici_item_textViewKullanıcıDurum);
            kampuseUzaklik=(TextView)itemView.findViewById(R.id.kullanici_item_textViewKullanıcıUzaklik);

            kullaniciResim=(ImageView)itemView.findViewById(R.id.kullanici_item_imageView);
        }

        public void setData(Kullanicilar kullanicilar) {
            this.kullaniciIsmi.setText(kullanicilar.getName()+" "+kullanicilar.getSurname());
            this.kullaniciEgitim.setText(kullanicilar.getEducation());
            this.kullaniciDurum.setText(kullanicilar.getStatus());
            this.kampuseUzaklik.setText("Kampüse Uzaklık : "+kullanicilar.getDistance()+"km");

            String status = kullanicilar.getStatus();


            if (status.equals("Kalacak Ev/Oda arıyor")) {
                kullaniciResim.setImageResource(R.drawable.room);
            } else {
                kullaniciResim.setImageResource(R.drawable.friend);
            }









        }

    }
}
