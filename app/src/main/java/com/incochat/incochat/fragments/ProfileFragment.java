package com.incochat.incochat.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.incochat.incochat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    TextView privacy;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

        privacy=(TextView)view.findViewById(R.id.privacy);

        if(sharedPreferences.getBoolean("privacy",false)){
            privacy.setTextColor(Color.GREEN);
        }

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sharedPreferences.getBoolean("privacy",false)){
                    editor.putBoolean("privacy",false);
                    privacy.setTextColor(Color.GRAY);
                }else{
                    editor.putBoolean("privacy",true);
                    privacy.setTextColor(Color.GREEN);
                }
                editor.commit();

            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(sharedPreferences.getBoolean("privacy",false)){
                privacy.setTextColor(Color.GREEN);
            }else{
                privacy.setTextColor(Color.GRAY);
            }
        }
    }
}
