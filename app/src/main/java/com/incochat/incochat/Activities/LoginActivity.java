package com.incochat.incochat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.incochat.incochat.R;
import com.incochat.incochat.beans.User;
import com.incochat.incochat.helpers.ConnectionCheck;
import com.incochat.incochat.helpers.DialogLoading;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 7567;
    MaterialEditText phone_number, code, name;
    TextView country_codes, welcome_message, icon, wrongnum, detailsmessage;
    Button submit, scode, sname;
    LinearLayout phonelayout, codelayout, namelayout;
    CircleImageView hiimage;
    RotateLoading phoneloading, codeloading;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    boolean valid = false, validcode = false, validname = false;
    String verificationid;
    FirebaseUser user;
    User u;
    boolean newuser = true;
    String profileurl=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        requestPermission();

    }

    private void initcallbacks() {

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                phoneloading.stop();
                phonelayout.setVisibility(View.GONE);
                codelayout.setVisibility(View.VISIBLE);
                changemessage();
                signinwithcrediatial(phoneAuthCredential);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneloading.stop();
                phonelayout.setVisibility(View.GONE);
                codelayout.setVisibility(View.VISIBLE);
                changemessage();
                verificationid = s;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(), "Sorry But We are Full", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Verification Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        };

    }


    private void initviews() {

        phone_number = (MaterialEditText) findViewById(R.id.pnumber);
        country_codes = (TextView) findViewById(R.id.countrycodes);
        welcome_message = (TextView) findViewById(R.id.welcomemessage);
        detailsmessage = (TextView) findViewById(R.id.detailsmessage);
        hiimage = (CircleImageView) findViewById(R.id.hiimage);
        icon = (TextView) findViewById(R.id.icon);
        phoneloading = (RotateLoading) findViewById(R.id.phoneloading);
        submit = (Button) findViewById(R.id.submit);

        code = (MaterialEditText) findViewById(R.id.vcode);
        scode = (Button) findViewById(R.id.submitcode);
        wrongnum = (TextView) findViewById(R.id.wrongnumber);
        codeloading = (RotateLoading) findViewById(R.id.codeloading);


        name = (MaterialEditText) findViewById(R.id.name);
        sname = (Button) findViewById(R.id.submitname);


        phonelayout = (LinearLayout) findViewById(R.id.phonelayout);
        codelayout = (LinearLayout) findViewById(R.id.codelayout);
        namelayout = (LinearLayout) findViewById(R.id.namelayout);


        welcome_message.setTypeface(EasyFonts.caviarDreams(getApplicationContext()));
        icon.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));

    }


    private void initlisteners() {

        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    submit.setBackgroundResource(R.drawable.btnback);
                    valid = true;
                } else {
                    submit.setBackgroundResource(R.drawable.btnback2);
                    valid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3) {
                    sname.setBackgroundResource(R.drawable.btnback);
                    validname = true;
                } else {
                    sname.setBackgroundResource(R.drawable.btnback2);
                    validname = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    scode.setBackgroundResource(R.drawable.btnback);
                    validcode = true;
                } else {
                    scode.setBackgroundResource(R.drawable.btnback2);
                    validcode = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setOnClickListener(this);
        country_codes.setOnClickListener(this);
        scode.setOnClickListener(this);
        sname.setOnClickListener(this);
        wrongnum.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (!ConnectionCheck.isConnected(LoginActivity.this)) {
            ConnectionCheck.showDialog(LoginActivity.this);
            ConnectionCheck.retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ConnectionCheck.isConnected(LoginActivity.this)) {
                        Toast.makeText(getApplicationContext(), "Cool You are connected now", Toast.LENGTH_LONG).show();
                        ConnectionCheck.dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (view == submit) {
            if (valid) {
                submit.setVisibility(View.GONE);
                phoneloading.setVisibility(View.VISIBLE);
                phoneloading.start();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(country_codes.getText().toString() + phone_number.getText().toString(), 60, TimeUnit.SECONDS, LoginActivity.this, callbacks);


            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            }
        }

        if (view == scode) {
            if (validcode) {
                codeloading.setVisibility(View.VISIBLE);
                codeloading.start();
                scode.setVisibility(View.GONE);
                signinwithcrediatial(PhoneAuthProvider.getCredential(verificationid, code.getText().toString()));
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Code", Toast.LENGTH_SHORT).show();
            }
        }

        if (view == wrongnum) {
            code.setText(null);
            phonelayout.setVisibility(View.VISIBLE);
            codelayout.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            phoneloading.setVisibility(View.GONE);
            changemessage();
        }

        if (view == hiimage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_CODE);
        }

        if (view == sname) {

            if (!validname) {
                Toast.makeText(getApplicationContext(), "Please Enter a Valid Name", Toast.LENGTH_SHORT).show();
                return;
            }

            DialogLoading.show(LoginActivity.this);

            namelayout.setVisibility(View.GONE);
            hiimage.setVisibility(View.INVISIBLE);
            detailsmessage.setVisibility(View.VISIBLE);
            detailsmessage.setText("Finalizing");
            DialogLoading.show(LoginActivity.this);
            if (newuser) {
                u = new User();
                u.setName(name.getText().toString());
                u.setPhonenumber(user.getPhoneNumber());
                u.setToken(FirebaseInstanceId.getInstance().getToken());
                u.setProfileurl(profileurl);
                FirebaseFirestore.getInstance().collection("Users").document(u.getPhonenumber())
                        .set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        DialogLoading.dismiss();
                    }
                })
                ;

            } else {
                u.setToken(FirebaseInstanceId.getInstance().getToken());
                u.setName(name.getText().toString());
                u.setProfileurl(profileurl);
                FirebaseFirestore.getInstance().collection("Users").document(u.getPhonenumber())
                        .set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        DialogLoading.dismiss();
                    }
                })
                ;

            }

        }

    }

    private void changemessage() {

        if (phonelayout.getVisibility() == View.VISIBLE) {
            hiimage.setVisibility(View.VISIBLE);
            hiimage.setOnClickListener(null);
            welcome_message.setText("Welcome To Incochat");
            detailsmessage.setText("Connect with world anonymously");
        } else if (codelayout.getVisibility() == View.VISIBLE) {
            hiimage.setVisibility(View.GONE);
            welcome_message.setText("Enter Your SMS Code");
            detailsmessage.setText("Verification Code is sent to \"" + country_codes.getText() + phone_number.getText() + "\"\nplease enter the Code below");
        } else if (namelayout.getVisibility() == View.VISIBLE) {
            hiimage.setVisibility(View.VISIBLE);
            welcome_message.setVisibility(View.INVISIBLE);
            detailsmessage.setText("Profile Picture");
            hiimage.setOnClickListener(this);
        }

    }

    private void signinwithcrediatial(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            codeloading.stop();
                            codeloading.setVisibility(View.GONE);
                            scode.setVisibility(View.VISIBLE);
                            codelayout.setVisibility(View.GONE);
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            handleuser();
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "Wrong Code Please Check", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void handleuser() {

        welcome_message.setVisibility(View.INVISIBLE);
        detailsmessage.setText("Preparing");
        DialogLoading.show(LoginActivity.this);

        FirebaseFirestore.getInstance().collection("Users").document(user.getPhoneNumber())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DialogLoading.dismiss();
                if (documentSnapshot.exists()) {
                    u=documentSnapshot.toObject(User.class);
                    newuser = false;
                    if (u.getProfileurl()!= null) {
                        Picasso.get().load(u.getProfileurl()).fit().placeholder(R.drawable.profile).into(hiimage);
                        profileurl =u.getProfileurl();
                    }
                    name.setText(u.getName());
                } else {
                    hiimage.setImageResource(R.drawable.profilepic);
                }
                namelayout.setVisibility(View.VISIBLE);
                changemessage();
            }
        });


    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    7567);
        }else{
            initviews();
            initlisteners();
            initcallbacks();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    DialogLoading.show(LoginActivity.this);
                    DialogLoading.dialog.setCancelable(false);
                    Picasso.get().load(data.getData()).fit().centerCrop().into(hiimage);
                    upload(data.getData());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upload(Uri data) {

        FirebaseStorage.getInstance().getReference("Users/" + user.getPhoneNumber()+"/Profilepic").putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DialogLoading.dismiss();
                        profileurl = taskSnapshot.getDownloadUrl().toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DialogLoading.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==7567 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_LONG).show();
            requestPermission();
        }else{
            requestPermission();
            Toast.makeText(getApplicationContext(),"Permission Required",Toast.LENGTH_LONG).show();
        }
    }

}
