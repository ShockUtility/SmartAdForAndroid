package kr.docs.smartad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by shock on 2017. 9. 1..
 */

public class SmartAdAlert extends Dialog implements SmartAdBanner.OnSmartAdBannerListener {

    static public final int BUTTON_OK       = 1;
    static public final int BUTTON_CANCEL   = 2;
    static public final int BUTTON_BACK     = 3;

    private SmartAdAlertListener            mListener;
    private String                          mGoogleID;
    private String                          mFacebookID;
    private String                          mAlertTitle;
    private String                          mAction1Title;
    private String                          mAction2Title;

    @SmartAd.SmartAdOrder
    private int                             mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private ProgressBar                     mLoading;
    private TextView                        mBtnAction1;
    private TextView                        mBtnAction2;
    private long                            mAalertButtonDelayMilliseconds = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_smartad);

        TextView txtAlertTitle = findViewById(R.id.txtTitle);
        txtAlertTitle.setText(mAlertTitle);

        mLoading = findViewById(R.id.pbLoading);
        final SmartAdBanner adBanner = findViewById(R.id.smartAdBanner);
        adBanner.setOnSmartAdBannerListener(this);

        mBtnAction1 = findViewById(R.id.btnAction1);
        mBtnAction2 = findViewById(R.id.btnAction2);

        if (SmartAd.IsShowAd(this)) {
            mLoading.setVisibility(View.VISIBLE);
            adBanner.showAd(SmartAdBanner.AD_SIZE_SMALL, mAdOrder, mGoogleID, mFacebookID);

            // Facebook error may not be detected in some cases!!!
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBtnAction1.setEnabled(true);
                    mBtnAction2.setEnabled(true);
                }
            }, mAalertButtonDelayMilliseconds);
        } else {
            adBanner.setVisibility(View.GONE);
            mBtnAction1.setEnabled(true);
            mBtnAction2.setEnabled(true);
        }

        mBtnAction1.setText(mAction1Title);
        mBtnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null) mListener.result(BUTTON_OK);
                dismiss();
            }
        });

        if (mAction2Title!=null) {
            mBtnAction2.setText(mAction2Title);
            mBtnAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null) mListener.result(BUTTON_CANCEL);
                    dismiss();
                }
            });
        } else {
            mBtnAction2.setVisibility(View.GONE);
        }

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adBanner.destroy();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mListener != null) mListener.result(BUTTON_BACK);
        super.onBackPressed();
    }

    public SmartAdAlert(Context context,
                        @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID,
                        String title, String action1Title, String action2Title,
                        final SmartAdAlertListener listener)
    {
        super(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);

        this.mAdOrder        = (adOrder==SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdOrder() : adOrder;
        this.mGoogleID       = googleID;
        this.mFacebookID     = facebookID;
        this.mAlertTitle     = title;
        this.mAction1Title   = action1Title;
        this.mAction2Title   = action2Title;
        this.mListener       = listener;
    }

    static public void select(Context context,
                              @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID,
                              String title, String action1Title, String action2Title,
                              final SmartAdAlertListener callback)
    {
        new SmartAdAlert(context,
                adOrder, googleID, facebookID,
                title, action1Title, action2Title, callback).show();
    }

    static public void select(Context context,
                              String googleID, String facebookID,
                              String title, String action1Title, String action2Title,
                              final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, title, action1Title, action2Title, callback);
    }

    static public void confirm(Context context,
                               @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID,
                               String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context,
                adOrder, googleID, facebookID,
                title, context.getString(android.R.string.ok), context.getString(android.R.string.cancel), callback);
    }

    static public void confirm(Context context,
                               String googleID, String facebookID,
                               String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.confirm(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, title, callback);
    }

    static public void alert(Context context,
                             @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID,
                             String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context,
                adOrder, googleID, facebookID,
                title, context.getString(android.R.string.ok), null, callback);
    }

    static public void alert(Context context,
                             String googleID, String facebookID,
                             String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.alert(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, title, callback);
    }

    // OnSmartAdBannerListener *********************************************************************

    @Override
    public void onSmartAdBannerDone(int type) {
        mLoading.setVisibility(View.GONE);
        mBtnAction1.setEnabled(true);
        mBtnAction2.setEnabled(true);
    }

    @Override
    public void onSmartAdBannerFail(int type) {
        mLoading.setVisibility(View.GONE);
        mBtnAction1.setEnabled(true);
        mBtnAction2.setEnabled(true);
    }

    // Callback Listener ***************************************************************************

    public interface SmartAdAlertListener {
        void result(int buttonType);
    }
}