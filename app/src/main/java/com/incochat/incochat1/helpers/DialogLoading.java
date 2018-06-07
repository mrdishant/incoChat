package com.incochat.incochat1.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.incochat.incochat1.R;


/**
 * Created by mrdis on 2/5/2018.
 */

public class DialogLoading {

    public static Dialog dialog;

    public static void show(Activity activity) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialogloading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void dismiss() {
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
