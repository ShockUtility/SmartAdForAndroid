package kr.docs.smartad;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shock on 2017. 10. 22..
 */

public class SmartAd {
    static final public String TEST_BANNER_FACEBOOK = "YOUR_PLACEMENT_ID";
    static final public String TEST_BANNER_GOOGLE   = "ca-app-pub-3940256099942544/1712485313";

    static final public int AD_TYPE_PASS     = -1;
    static final public int AD_TYPE_RANDOM   = 0;
    static final public int AD_TYPE_GOOGLE   = 1;
    static final public int AD_TYPE_FACEBOOK = 2;

    @IntDef({AD_TYPE_GOOGLE, AD_TYPE_FACEBOOK})
    public @interface SmartAdTestType {}

    @IntDef({AD_TYPE_RANDOM, AD_TYPE_GOOGLE, AD_TYPE_FACEBOOK})
    public @interface SmartAdType {}

    static private List<String> mGoogleTestDevices = new ArrayList<String>();

    static public IsShowAdListener IsShowAdFunc = null;

    static protected boolean IsShowAd(Object owner) {
        if (IsShowAdFunc!=null) {
            for (Class c: IsShowAdFunc.getAvailClass()) {
                if (owner.getClass() == c) {
                    return IsShowAdFunc.isShowAd();
                }
            }
        }
        return true;
    }

    static public void addTestDevice(@SmartAdTestType int type, String id) {
        switch (type) {
            case AD_TYPE_GOOGLE:
                mGoogleTestDevices.add(id);
                break;
            case AD_TYPE_FACEBOOK:
                com.facebook.ads.AdSettings.addTestDevice(id);
                break;
        }
    }

    static public com.google.android.gms.ads.AdRequest getGoogleAdRequest() {
        com.google.android.gms.ads.AdRequest.Builder req = new com.google.android.gms.ads.AdRequest.Builder();

        for (int i=0; i<mGoogleTestDevices.size(); i++) {
            req.addTestDevice(mGoogleTestDevices.get(i));
        }

        return req.build();
    }

    static protected AlertDialog loadingAlert(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.loading_smartad, null);
        alert.setView(v);

        return alert.show();
    }

    static public @SmartAdType int randomAdType() {
        return (new Random()).nextBoolean() ? AD_TYPE_GOOGLE : AD_TYPE_FACEBOOK;
    }

    public interface IsShowAdListener {
        Class[] getAvailClass();
        boolean isShowAd();
    }
}
