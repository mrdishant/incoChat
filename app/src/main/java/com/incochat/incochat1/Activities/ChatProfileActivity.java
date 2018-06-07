package com.incochat.incochat1.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.incochat.incochat1.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatProfileActivity extends AppCompatActivity {

    String name,number,picurl;
    TextView nameT,phone;
    CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);

        name=getIntent().getStringExtra("name") ;
        number=getIntent().getStringExtra("phonenumber");
        picurl=getIntent().getStringExtra("profileurl");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        profileImage=findViewById(R.id.profilepic);
        nameT=findViewById(R.id.name);
        phone=findViewById(R.id.phone);


        nameT.setText(name);
        phone.setText(number);
        if(picurl!=null && !picurl.isEmpty()){
            Picasso.get().load(picurl).fit().centerCrop().into(profileImage);
        }else{
            profileImage.setImageResource(R.drawable.profile);
        }



    }
}
