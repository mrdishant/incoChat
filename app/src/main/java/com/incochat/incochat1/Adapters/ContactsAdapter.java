package com.incochat.incochat1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.incochat.incochat1.Activities.ChatActivity;
import com.incochat.incochat1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrdis on 1/24/2018.
 */


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyView> {

    ArrayList<String> contacts;
    Context context;

    public ContactsAdapter(ArrayList<String> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(final MyView holder, int position) {

        FirebaseFirestore.getInstance().collection("Users").document(contacts.get(position))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(final DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot == null || !documentSnapshot.exists()) {
                            return;
                        }
                        if (documentSnapshot.getString("profileurl") != null && !documentSnapshot.getString("profileurl").isEmpty()) {
                            Picasso.get().load(documentSnapshot.getString("profileurl")).fit().centerCrop().into(holder.imageView);
                        }
                        holder.name.setText(documentSnapshot.getString("name"));
                        holder.number.setText(documentSnapshot.getString("phonenumber"));

                        holder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("phonenumber",documentSnapshot.getString("phonenumber"));
                                intent.putExtra("name",documentSnapshot.getString("name"));
                                intent.putExtra("profileurl",documentSnapshot.getString("profileurl"));
                                context.startActivity(intent);
                            }
                        });

                    }
                });


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    class MyView extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        CardView cardView;
        TextView name, number;


        public MyView(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilepic);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            cardView = itemView.findViewById(R.id.card);
        }


    }

}
