package com.incochat.incochat1.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.incochat.incochat1.R;
import com.incochat.incochat1.fragments.ChatsFragment;
import com.incochat.incochat1.fragments.ContactsFragment;
import com.incochat.incochat1.fragments.ProfileFragment;
import com.incochat.incochat1.helpers.ConnectionCheck;
import com.incochat.incochat1.helpers.DialogLoading;
import com.incochat.incochat1.interfaces.Dialog;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class HomeActivity extends AppCompatActivity implements Dialog {

    NavigationTabBar navigationTabBar;
    ViewPager viewPager;
    Sections sections;
    TextView icon,india;
    ImageView sync;
    ContactsFragment contactsFragment;
    ChatsFragment chatsFragment;
    ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        inittabbar();

        if (!ConnectionCheck.isConnected(HomeActivity.this)) {
            ConnectionCheck.showDialog(HomeActivity.this);
            ConnectionCheck.retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ConnectionCheck.isConnected(HomeActivity.this)) {
                        Toast.makeText(getApplicationContext(), "Cool You are connected now", Toast.LENGTH_LONG).show();
                        ConnectionCheck.dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    private void initViews() {

        contactsFragment=new ContactsFragment();
        chatsFragment=new ChatsFragment();
        profileFragment=new ProfileFragment();

        viewPager=(ViewPager)findViewById(R.id.viewpager);
        sections=new Sections(getSupportFragmentManager());
        viewPager.setAdapter(sections);
        icon=findViewById(R.id.icon);
        india=findViewById(R.id.india);
        sync=findViewById(R.id.sync);
        icon.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();

                }catch (Exception ex){

                }
            }
        });

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }
        });


    }

    private void inittabbar() {

        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.contacts),
                        Color.TRANSPARENT
                ).title("Contacts")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.chat),
                        Color.TRANSPARENT
                ).title("Chats")

                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.profile),
                        Color.TRANSPARENT
                ).title("Profile")

                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 1);

        navigationTabBar.setTitleMode(NavigationTabBar.TitleMode.ALL);
        navigationTabBar.setIsTitled(true);
        navigationTabBar.setIsSwiped(true);
        navigationTabBar.setBgColor(Color.TRANSPARENT);
        navigationTabBar.setTitleSize(20);
        navigationTabBar.setIconSizeFraction(0.6f);


        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    sync.setVisibility(View.VISIBLE);
                    india.setVisibility(View.GONE);
                    navigationTabBar.setBackgroundColor(Color.WHITE);
                }else if(position==1){
                    sync.setVisibility(View.GONE);
                    india.setVisibility(View.VISIBLE);
                    navigationTabBar.setBackgroundColor(Color.TRANSPARENT);
                }else if(position==2){
                    sync.setVisibility(View.GONE);
                    india.setVisibility(View.VISIBLE);
                    navigationTabBar.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    class Sections extends FragmentPagerAdapter{

        public Sections(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return contactsFragment;
                case 1:
                    return chatsFragment;
                case 2:
                    return profileFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void showDialog(){
        DialogLoading.show(HomeActivity.this);
    }

    public void dismiss(){
        DialogLoading.dismiss();
    }

    public void sync(){
        Toast.makeText(getApplicationContext(),"Syncing Contacts",Toast.LENGTH_LONG).show();
        contactsFragment.getdata();
    }


}
