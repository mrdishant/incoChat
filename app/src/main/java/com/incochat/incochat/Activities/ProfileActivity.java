package com.incochat.incochat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.User;
import com.incochat.incochat.helpers.DialogLoading;
import com.incochat.incochat.interfaces.Dialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView name,phone;
    CircleImageView profileImage;
    LinearLayout editName;
    ImageView update;
    MaterialEditText nameEdit;
    private int Image_CODE=7567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initViews();




    }

    private void initViews() {

        profileImage=findViewById(R.id.profilepic);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        editName=findViewById(R.id.editLayout);
        nameEdit=findViewById(R.id.nameEdit);
        update=findViewById(R.id.updatename);

        name.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        update.setOnClickListener(this);


        getUser();


    }

    private void getUser() {

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
        if(user.getProfileurl()!=null){
            Picasso.get().load(user.getProfileurl()).fit().centerCrop().into(profileImage);
        }else{
            profileImage.setImageResource(R.drawable.profile);
        }

    }


    @Override
    public void onClick(View view) {
        if(view==name){
            name.setVisibility(View.GONE);
            editName.setVisibility(View.VISIBLE);
        }if(view==profileImage){
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,Image_CODE);
        }
        if (view == update) {
            DialogLoading.show(ProfileActivity.this);

            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .update("name",nameEdit.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    DialogLoading.dismiss();
                    if(task.isSuccessful()){
                        editName.setVisibility(View.GONE);
                        name.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(getApplicationContext(),"Some Error Occured Please Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Image_CODE ){
            if(resultCode== Activity.RESULT_OK){
                if(data!=null&&data.getData()!=null){
                    DialogLoading.show(ProfileActivity.this);
                    DialogLoading.dialog.setCancelable(false);
                    Toast.makeText(getApplicationContext(),"Uploading",Toast.LENGTH_LONG).show();
                    FirebaseStorage.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/profilepic")
                            .putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                    .update("profileurl",taskSnapshot.getDownloadUrl().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DialogLoading.dismiss();
                                            if(!task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Some error Please Try Again",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Some error Please Try Again",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                Toast.makeText(getApplicationContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
