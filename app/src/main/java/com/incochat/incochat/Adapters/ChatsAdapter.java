package com.incochat.incochat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.incochat.incochat.Activities.ChatActivity;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.Chat;
import com.incochat.incochat.beans.User;
import com.incochat.incochat.interfaces.listener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mrdis on 1/31/2018.
 */


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyChats> {

    ArrayList<Chat> arrayList;
    public ArrayList<String> profileUrls;
    Context context;

    public ChatsAdapter(ArrayList<Chat> arrayList, Context context) {
        profileUrls = new ArrayList<>();
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyChats onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyChats(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyChats holder, final int position) {

        FirebaseFirestore.getInstance().collection("Users").document(arrayList.get(position).getSender())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);

                            if (arrayList.get(position).isAnonymous()) {
                                holder.name.setText("Anonymous");
                                profileUrls.add(null);
                                holder.imageView.setImageResource(R.drawable.profile);
                            } else {
                                holder.name.setText(user.getName());
                                if (user.getProfileurl() != null) {
                                    profileUrls.add(user.getProfileurl());
                                    Picasso.get().load(user.getProfileurl()).fit().centerCrop().into(holder.imageView);
                                } else {
                                    holder.imageView.setImageResource(R.drawable.profile);
                                    profileUrls.add(null);
                                }
                            }
                        }
                    }
                });

        holder.number.setText(arrayList.get(position).getLastTxt());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("phonenumber", arrayList.get(position).getSender());
                intent.putExtra("name", holder.name.getText().toString());
                if(!arrayList.get(position).isAnonymous()){
                    intent.putExtra("profileurl", profileUrls.get(position));
                }
                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(arrayList.get(position).isAnonymous()){
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .collection("Chats").document(arrayList.get(position).getSender()+"-Anonymous").delete();
                }else{
                    FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .collection("Chats").document(arrayList.get(position).getSender()).delete();
                }

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyChats extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView name, number;
        CardView cardView;

        public MyChats(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.profilepic);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            cardView = itemView.findViewById(R.id.card);
        }


    }

}
