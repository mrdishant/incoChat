package com.incochat.incochat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.incochat.incochat.Adapters.MessagesAdapter;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.Chat;
import com.incochat.incochat.beans.Message;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profilepic;
    TextView nametitle,anonimityTxt;
    RecyclerView recyclerView;
    ImageView back,emoji;
    LinearLayout send;
    MaterialEditText message;
    FirebaseUser user;
    boolean anonymous;
    ArrayList<Message>messages;
    MessagesAdapter messagesAdapter;
    Message message1=new Message();
    private int Image_CODE=7567;
    String rphone,uphone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String recieverPhone1,recieverPhone2,recievername,recieverProfileUrl;
    TextView anonimity;
    ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        anonimity=findViewById(R.id.anonimity);
        anonimityTxt=findViewById(R.id.anonimityTxt);
        delete=findViewById(R.id.delete);
        recieverPhone1=getIntent().getStringExtra("phonenumber");
        recieverPhone2=recieverPhone1+"-Anonymous";
        Log.i("reciever1",recieverPhone1);
        Log.i("reciever2",recieverPhone2);

        recievername=getIntent().getStringExtra("name");

        recieverProfileUrl=getIntent().getStringExtra("profileurl");


        sharedPreferences=(SharedPreferences)getSharedPreferences("incochat",MODE_PRIVATE);
        editor=sharedPreferences.edit();


        user= FirebaseAuth.getInstance().getCurrentUser();

        if(recievername.equals("Anonymous")){
            anonimity.setVisibility(View.GONE);
            anonimityTxt.setVisibility(View.VISIBLE);
            rphone=recieverPhone2;
            uphone=user.getPhoneNumber()+"-Anonymous";
            anonymous=true;
        }else{


            if(sharedPreferences.getBoolean("privacy",false)){
                anonymous=true;
                rphone=recieverPhone2;
                uphone=user.getPhoneNumber()+"-Anonymous";
                anonimity.setText("On");
                anonimityTxt.setVisibility(View.VISIBLE);
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.on),null,null);
            }else{
                anonymous=false;
                if(recievername.equals("Anonymous")){
                    rphone=recieverPhone2;
                }else{
                    rphone=recieverPhone1;
                }
                uphone=user.getPhoneNumber();
                anonimity.setText("Off");
                anonimityTxt.setVisibility(View.GONE);
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.off),null,null);
            }

        }

        initviews();
    }

    private void initviews() {

        profilepic=(CircleImageView)findViewById(R.id.profileimage);
        nametitle=(TextView)findViewById(R.id.name);

        if(recieverProfileUrl!=null){
            Picasso.get().load(recieverProfileUrl).fit().centerCrop().into(profilepic);
        }else{
            profilepic.setImageResource(R.drawable.profile);
        }
        nametitle.setText(recievername);


        recyclerView=(RecyclerView)findViewById(R.id.messages);
        send=findViewById(R.id.sendmessage);
        message=(MaterialEditText)findViewById(R.id.message);
        back=findViewById(R.id.back);


        send.setOnClickListener(this);


        findViewById(R.id.camera).setOnClickListener(this);


        messages=new ArrayList<>();
        messagesAdapter=new MessagesAdapter(messages,getApplicationContext());
        messagesAdapter.setDeleteButton(delete);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(messagesAdapter);

        initmessages();

        delete.setOnClickListener(this);

        anonimity.setOnClickListener(this);
        back.setOnClickListener(this);



    }

    private void initmessages() {

        if(recievername.equals("Anonymous")){
            DocumentReference  documentReference=FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber())
                    .collection("Chats").document(recieverPhone2);

            messagesAdapter.setDeleteChatId(recieverPhone2);

            documentReference.collection("messages")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if(documentSnapshots==null||documentSnapshots.isEmpty()){
                                messages.clear();
                                messagesAdapter.notifyDataSetChanged();
                                return;
                            }

                            messages.clear();
                            for(DocumentSnapshot documentSnapshot:documentSnapshots){
                                messages.add(documentSnapshot.toObject(Message.class));
                                Log.d("Message"+messages.size(),messages.get(messages.size()-1)+"");
                            }
                            messagesAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount()-1);
                        }
                    });

        }else{
            DocumentReference  documentReference=FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber())
                    .collection("Chats").document(recieverPhone1);

            messagesAdapter.setDeleteChatId(recieverPhone1);

            documentReference.collection("messages")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if(documentSnapshots==null||documentSnapshots.isEmpty()){
                                messages.clear();
                                messagesAdapter.notifyDataSetChanged();
                                return;
                            }

                            messages.clear();
                            for(DocumentSnapshot documentSnapshot:documentSnapshots){
                                messages.add(documentSnapshot.toObject(Message.class));
                                Log.d("Message"+messages.size(),messages.get(messages.size()-1)+"");
                            }
                            messagesAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount()-1);
                        }
                    });

        }




    }


    @Override
    public void onClick(View view) {


        if(view == send){
            if(message.getText().toString().length()>0){


                Log.i("reciever1",recieverPhone1);
                Log.i("reciever2",recieverPhone2);
                Log.i("recuser",user.getPhoneNumber());
                Log.i("recuphone",uphone);



                message1.setId(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());
                message1.setUsersent(false);
                message1.setText(message.getText().toString());
                message1.setTime(new SimpleDateFormat("HH:mm").format(new Date()));
                message.setText(null);


                Chat chat=new Chat();
                chat.setSender(user.getPhoneNumber());
                chat.setAnonymous(anonymous);
                chat.setLastTxt(message1.getText());

                DocumentReference documentReference=FirebaseFirestore.getInstance().collection("Users").document(recieverPhone1)
                        .collection("Chats").document(uphone);

                documentReference.set(chat);
                documentReference.collection("messages").document(message1.getId()).set(message1);


                Chat chat1=new Chat();
                chat1.setSender(recieverPhone1);
                chat1.setAnonymous(anonymous);
                chat1.setLastTxt(message1.getText());
                message1.setId(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());

                DocumentReference documentReference1=FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber())
                        .collection("Chats").document(rphone);


                documentReference1.set(chat1);
                message1.setUsersent(true);
                documentReference1.collection("messages").document(message1.getId()).set(message1);


            }else{
                Toast.makeText(getApplicationContext(),"Please Enter Message",Toast.LENGTH_LONG).show();
            }

        }
        else if(view.getId()==R.id.camera){
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,Image_CODE);
        }else if(view ==back){
            onBackPressed();
        }else if(view == anonimity){


            if(sharedPreferences.getBoolean("privacy",false)){
                editor.putBoolean("privacy",false);
                anonimity.setText("Off");
                if(recievername.equals("Anonymous")){
                    rphone=recieverPhone2;
                }else{
                    rphone=recieverPhone1;
                }
                uphone=user.getPhoneNumber();
                anonimityTxt.setVisibility(View.GONE);
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.off),null,null);
                anonymous=false;
                editor.commit();

            }else{
                editor.putBoolean("privacy",true);
                anonimity.setText("On");
                rphone=recieverPhone2;
                anonimityTxt.setVisibility(View.VISIBLE);
                uphone=user.getPhoneNumber()+"-Anonymous";
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.on),null,null);
                anonymous=true;
                editor.commit();
            }




        }if(view==delete){
            Toast.makeText(getApplicationContext(),"Delete error",Toast.LENGTH_LONG).show();

            messagesAdapter.delete();

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Image_CODE ){
            if(resultCode== Activity.RESULT_OK){
                if(data!=null&&data.getData()!=null){
                 Intent intent=new Intent(ChatActivity.this,ImageActivity.class);
                 intent.putExtra("url",data.getData().toString());
                 intent.putExtra("name",recievername);
                 intent.putExtra("profileurl",recieverProfileUrl);
                 intent.putExtra("phonenumber",recieverPhone1);
                 startActivity(intent);
                }
            }else{
                Toast.makeText(getApplicationContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
