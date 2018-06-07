package com.incochat.incochat1.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.incochat.incochat1.R;
import com.incochat.incochat1.beans.Chat;
import com.incochat.incochat1.beans.Message;
import com.incochat.incochat1.helpers.DialogLoading;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    String picurl;
    CircleImageView profilepic;

    TextView nametitle, anonimityTxt;
    ImageView back;
    LinearLayout send,download;
    FirebaseUser user;
    boolean anonymous;
    Message message1 = new Message();
    String rphone, uphone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String recieverPhone1, recieverPhone2, recievername, recieverProfileUrl;
    TextView anonimity;
    ProgressDialog mProgressDialog;
    boolean viewable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        send = findViewById(R.id.sendmessage);
        download = findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage().execute(getIntent().getStringExtra("url"));
            }
        });

        imageView = (ImageView) findViewById(R.id.imageloaded);
        anonimity = findViewById(R.id.anonimity);

        anonimityTxt = findViewById(R.id.anonimityTxt);

        sharedPreferences = (SharedPreferences) getSharedPreferences("incochat", MODE_PRIVATE);
        editor = sharedPreferences.edit();



        recieverPhone1 = getIntent().getStringExtra("phonenumber");

        recieverPhone2 = recieverPhone1 + "-Anonymous";


        recievername = getIntent().getStringExtra("name");
        recieverProfileUrl = getIntent().getStringExtra("profileurl");
        viewable = getIntent().getBooleanExtra("viewable", false);


        user = FirebaseAuth.getInstance().getCurrentUser();



        if (viewable) {
            send.setVisibility(View.GONE);
            download.setVisibility(View.VISIBLE);
            findViewById(R.id.toolbar).setVisibility(View.GONE);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0);
            imageView.setLayoutParams(params);

        } else {

            if (recievername.equals("Anonymous")) {
                anonimity.setVisibility(View.GONE);
                anonimityTxt.setVisibility(View.VISIBLE);
                rphone = recieverPhone2;
                uphone = user.getPhoneNumber() + "-Anonymous";
                anonymous = true;
            } else {


                if (sharedPreferences.getBoolean("privacy", false)) {
                    anonymous = true;
                    rphone = recieverPhone2;
                    uphone = user.getPhoneNumber() + "-Anonymous";
                    anonimity.setText("On");
                    anonimityTxt.setVisibility(View.VISIBLE);
                    anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.on), null, null);
                } else {
                    anonymous = false;
                    if (recievername.equals("Anonymous")) {
                        rphone = recieverPhone2;
                    } else {
                        rphone = recieverPhone1;
                    }
                    uphone = user.getPhoneNumber();
                    anonimity.setText("Off");
                    anonimityTxt.setVisibility(View.GONE);
                    anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.off), null, null);
                }

            }

            initviews();
        }



        initloadimage();

    }

    private void initviews() {

        profilepic = (CircleImageView) findViewById(R.id.profileimage);
        nametitle = (TextView) findViewById(R.id.name);

        if (recieverProfileUrl != null) {
            Picasso.get().load(recieverProfileUrl).fit().centerCrop().into(profilepic);
        } else {
            profilepic.setImageResource(R.drawable.profile);
        }
        nametitle.setText(recievername);


        back = findViewById(R.id.back);


        send.setOnClickListener(this);

        anonimity.setOnClickListener(this);
        back.setOnClickListener(this);


    }


    private void initloadimage() {


        if (!getIntent().getStringExtra("url").equals(null)) {
            Picasso.get().load(getIntent().getStringExtra("url")).into(imageView);
        }


    }

    private void uploadimage() {

        FirebaseStorage.getInstance().getReference("Users/" + user.getPhoneNumber() + "/Chats/" + recieverPhone1 + "/" + System.currentTimeMillis())
                .putFile(Uri.parse(getIntent().getStringExtra("url")))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        picurl = taskSnapshot.getDownloadUrl().toString();


                        message1.setBucketId("gs://" + taskSnapshot.getMetadata().getBucket() + "/" + taskSnapshot.getMetadata().getPath());
                        message1.setUrl(picurl);

                        message1.setUsersent(false);
                        message1.setText("");
                        message1.setId(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());
                        message1.setTime(new SimpleDateFormat("HH:mm").format(new Date()));


                        Chat chat = new Chat();
                        chat.setSender(user.getPhoneNumber() + "");
                        chat.setAnonymous(anonymous);
                        chat.setLastTxt("📷 Image");

                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(recieverPhone1)
                                .collection("Chats").document(uphone);

                        documentReference.set(chat);
                        documentReference.collection("messages").document(message1.getId()).set(message1);


                        Chat chat1 = new Chat();
                        chat1.setSender(recieverPhone1);
                        chat1.setAnonymous(anonymous);
                        chat1.setLastTxt("📷 Image");

                        DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber())
                                .collection("Chats").document(rphone);


                        documentReference1.set(chat1);
                        message1.setUsersent(true);
                        message1.setId(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).toString());
                        documentReference1.collection("messages").document(message1.getId()).set(message1)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DialogLoading.dismiss();
                        Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        ;

    }


    @Override
    public void onClick(View view) {

        if (view == send) {
            Toast.makeText(getApplicationContext(), "Sending Message", Toast.LENGTH_LONG).show();
            DialogLoading.show(ImageActivity.this);
            if (DialogLoading.dialog != null) {
                DialogLoading.dialog.setCancelable(false);
            }
            uploadimage();
        }


        if (view == back) {
            onBackPressed();
        } else if (view == anonimity) {


            if (sharedPreferences.getBoolean("privacy", false)) {
                editor.putBoolean("privacy", false);
                anonimity.setText("Off");
                if (recievername.equals("Anonymous")) {
                    rphone = recieverPhone2;
                } else {
                    rphone = recieverPhone1;
                }
                uphone = user.getPhoneNumber();
                anonimityTxt.setVisibility(View.GONE);
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.off), null, null);
                anonymous = false;
                editor.commit();

            } else {
                editor.putBoolean("privacy", true);
                anonimity.setText("On");
                rphone = recieverPhone2;
                anonimityTxt.setVisibility(View.VISIBLE);
                uphone = user.getPhoneNumber() + "-Anonymous";
                anonimity.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.on), null, null);
                anonymous = true;
                editor.commit();
            }

        }


    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ImageActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Downloading Image");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            // Close progressdialog



            FileOutputStream out = null;
            try {

                File root = null;
                root = Environment.getExternalStorageDirectory();
                File fileDir1 = new File(root.getAbsolutePath() + "/" + "incoChat");
                if (!fileDir1.exists())
                    fileDir1.mkdirs();
                File fileDir = new File(root.getAbsolutePath() + "/" + "incoChat"+"/Images");
                if (!fileDir.exists())
                    fileDir.mkdirs();
                String directoryPath = root.getAbsolutePath() + "/" + "incoChat"+"/Images";

                File sdCard = Environment.getExternalStorageDirectory();
                //String directoryPath = sdCard.getAbsolutePath()+"/zodix/packageSlips/";

                File directory = new File(directoryPath);
                if(!directory.exists()){
                    directory.mkdirs();
                }
                String fileName = directoryPath+"/"+"IMG"+System.currentTimeMillis()+ ".jpg";
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }


            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Image Saved",Toast.LENGTH_LONG).show();
        }
    }

}
