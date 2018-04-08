package com.incochat.incochat.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.incochat.incochat.Activities.ChatActivity;
import com.incochat.incochat.Activities.HomeActivity;
import com.incochat.incochat.Adapters.ContactsAdapter;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.User;
import com.incochat.incochat.interfaces.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    ContactsAdapter contactsAdapter;
    ArrayList<String> users;
    ArrayList<String> contacts;
    RecyclerView recyclerView;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        contacts = new ArrayList<>();
        users = new ArrayList<>();

        contactsAdapter = new ContactsAdapter(users, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactsAdapter);

        getdata();
        setData();

        return view;
    }


    private void setData() {
        /*users.clear();
        if(user.getContacts()==null){
            Toast.makeText(getContext(),"Null",Toast.LENGTH_LONG).show();
            return;
        }
        for(String s: user.getContacts()){
            FirebaseFirestore.getInstance().collection("Users").document(s)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if(documentSnapshot!=null&&documentSnapshot.exists()){
                                users.add(documentSnapshot.toObject(User.class));
                                contactsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
        //contactsAdapter.notifyDataSetChanged();
*/

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .collection("Contacts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots == null || documentSnapshots.isEmpty()) {
                    users.clear();
                    contactsAdapter.notifyDataSetChanged();
                    return;
                } else {
                    users.clear();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                        users.add(documentSnapshot.getId());
                    }
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void getdata() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
        while (c.moveToNext() && c.getCount() > 0) {
            if (c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).length() >= 10) {

                if (!c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).startsWith("+91")) {
                    if (contacts.contains("+91" + c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replace(" ", "").replace("-", ""))) {
                        continue;
                    }
                    contacts.add("+91" + c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replace(" ", "").replace("-", ""));
                } else {
                    if (contacts.contains(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replace(" ", "").replace("-", ""))) {
                        continue;
                    }
                    contacts.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replace(" ", "").replace("-", ""));
                }
            }

        }

        for (int i = 0; i < contacts.size(); i++) {
            Log.i("User" + contacts.get(i), "Hello");
            final String g = contacts.get(i);
            if(g.equals(FirebaseAuth.getInstance().getUid())){
                continue;
            }
            FirebaseFirestore.getInstance().collection("Users").document(g)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Time", new Date());
                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .collection("Contacts").document(g).set(hashMap);
                    }
                }
            });
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        } else {

        }
    }
}
