package com.incochat.incochat1.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.incochat.incochat1.R;

import java.util.List;

public class Help extends AppCompatActivity implements View.OnClickListener {

    LinearLayout contact,terms,appinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        contact=findViewById(R.id.contact);
        terms=findViewById(R.id.terms);
        appinfo=findViewById(R.id.appinfo);

        contact.setOnClickListener(this);
        terms.setOnClickListener(this);
        appinfo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view==contact){

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            startActivity(intent);

        }else if(view==terms){
            FirebaseFirestore.getInstance().collection("Invitation Link")
                    .document("Link")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(task.getResult().getString("link2")));
                                startActivity(intent);
                            }
                        }
                    });
        }else if(view==appinfo){
            startActivity(new Intent(getApplicationContext(),AppInfo.class));
        }
    }
}
