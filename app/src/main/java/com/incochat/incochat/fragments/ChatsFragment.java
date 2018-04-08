package com.incochat.incochat.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.incochat.incochat.Activities.ChatActivity;
import com.incochat.incochat.Adapters.ChatsAdapter;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.Chat;
import com.incochat.incochat.beans.User;
import com.incochat.incochat.interfaces.listener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    RecyclerView recyclerView;
    ArrayList<Chat> arrayList;
    ChatsAdapter chatsAdapter;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chats, container, false);;

        recyclerView=(RecyclerView)view.findViewById(R.id.chats);
        arrayList=new ArrayList<>();


        chatsAdapter=new ChatsAdapter(arrayList,getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatsAdapter);

        initlist();

        return view;
    }

    private void initlist() {

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if(documentSnapshots==null||documentSnapshots.isEmpty()){
                                arrayList.clear();
                                chatsAdapter.profileUrls.clear();
                                chatsAdapter.notifyDataSetChanged();
                                return;
                            }

                            arrayList.clear();
                            chatsAdapter.profileUrls.clear();
                            for(DocumentSnapshot documentSnapshot:documentSnapshots){
                              arrayList.add(documentSnapshot.toObject(Chat.class));
                            }
                            chatsAdapter.notifyDataSetChanged();
                    }
                });

    }

}
