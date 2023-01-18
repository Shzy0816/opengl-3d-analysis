package com.shenyutao.opengldemo.tool;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;


public class DialogUtils {

    public static KProgressHUD getLoadingDialog(Context context,String Message){
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(Message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

}
