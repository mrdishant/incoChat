package com.incochat.incochat1.Util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by mrdis on 1/25/2018.
 */

public class PermissionApi {

    Activity activity;
    public PermissionApi(Activity activity){
        this.activity=activity;
    }

    public void contactsPermission(){

        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    1);
        }

    }

    public Activity getActivity() {
        return activity;
    }


}
