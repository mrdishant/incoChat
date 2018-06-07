package com.incochat.incochat1.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.incochat.incochat1.Activities.Help;
import com.incochat.incochat1.Activities.ProfileActivity;
import com.incochat.incochat1.R;
import com.incochat.incochat1.beans.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  implements View.OnClickListener{



    TextView name,phone;
    CircleImageView circleImageView;
    Switch privacy;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout invite,account,groupcard,chats,notifications,help;
    private int REQUEST_INVITE=7567;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences=(SharedPreferences)getActivity().getSharedPreferences("incochat", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        name=view.findViewById(R.id.name);
        phone=view.findViewById(R.id.phone);
        privacy=view.findViewById(R.id.switchPrivacy);
        circleImageView=view.findViewById(R.id.profileimage);

        invite=view.findViewById(R.id.invite);
        invite.setOnClickListener(this);

        account=view.findViewById(R.id.account);
        account.setOnClickListener(this);

        groupcard=view.findViewById(R.id.groupcard);
        chats=view.findViewById(R.id.chats);
        notifications=view.findViewById(R.id.notifications);
        help=view.findViewById(R.id.help);
        groupcard.setOnClickListener(this);
        chats.setOnClickListener(this);
        notifications.setOnClickListener(this);
        help.setOnClickListener(this);

        getUser();

        privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putBoolean("privacy",true);
                }else{
                    editor.putBoolean("privacy",false);
                }
                editor.commit();
            }
        });


        return view;
    }

    private void getUser() {

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return;
        }

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if(documentSnapshot!=null&& documentSnapshot.exists()){
                                setValues(documentSnapshot.toObject(User.class));
                            }
                    }
                });

    }

    private void setValues(User user) {

        name.setText(user.getName());
        phone.setText(user.getPhonenumber());
        if(user.getProfileurl()!=null && !user.getProfileurl().isEmpty()){
            Picasso.get().load(user.getProfileurl()).fit().centerCrop().into(circleImageView);
        }else{
            circleImageView.setImageResource(R.drawable.profile);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(sharedPreferences.getBoolean("privacy",false)){
                privacy.setChecked(true);
            }else{
                privacy.setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view==invite){
            try {

                FirebaseFirestore.getInstance().collection("Invitation Link")
                        .document("Link")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "incoChat");
                        String strShareMessage = "Check out incoChat Messenger for your smartphone.";
                        strShareMessage = strShareMessage + "Download it today from "+task.getResult().getString("link") ;//getPackageName();
                        i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                        startActivity(Intent.createChooser(i, "Share via"));
                    }
                });


            } catch(Exception e) {
                //e.toString();
            }



        }if(view==account){
            startActivity(new Intent(getContext(), ProfileActivity.class));
        }if(view==groupcard || view==chats ||view==notifications  ){
            comingsoon();
        }if(view==help ){
            startActivity(new Intent(getContext(), Help.class));
        }
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
               // .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                //.setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Hr", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("Sent", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }



    public void comingsoon(){
        Toast.makeText(getContext(),"Coming Soon",Toast.LENGTH_LONG).show();
    }

}
