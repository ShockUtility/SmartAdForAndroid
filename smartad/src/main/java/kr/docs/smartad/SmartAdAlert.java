package kr.docs.smartad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by shock on 2017. 9. 1..
 */

public class SmartAdAlert extends Dialog implements SmartAdBanner.OnSmartAdBannerListener {

    static public final int BUTTON_1 = 1;
    static public final int BUTTON_2 = 2;

    private int                     mAdSize;
    private String                  mGoogleID;
    private String                  mFacebookID;
    private boolean                 mIsFirstGoogle;
    private String                  mAlertTitle;
    private String                  mAction1Title;
    private String                  mAction2Title;
    private SmartAdAlertListener    mListener;

    private ProgressBar             mLoading;
    private TextView                mBtnAction1;
    private TextView                mBtnAction2;
    private long                    mAalertButtonDelayMilliseconds = 3000;

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
            adBanner.showAd(mAdSize, mGoogleID, mFacebookID, mIsFirstGoogle);

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
                if(mListener!=null) mListener.result(BUTTON_1);
                dismiss();
            }
        });

        if (mAction2Title!=null) {
            mBtnAction2.setText(mAction2Title);
            mBtnAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null) mListener.result(BUTTON_2);
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

    public SmartAdAlert(Context context,
                        String googleID, String facebookID, boolean isFirstGoogle,
                        String title, String action1Title, String action2Title,
                        final SmartAdAlertListener listener)
    {
        super(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);

        this.mAdSize         = SmartAdBanner.AD_SIZE_RECTANGLE;
        this.mGoogleID       = googleID;
        this.mFacebookID     = facebookID;
        this.mIsFirstGoogle  = isFirstGoogle;
        this.mAlertTitle     = title;
        this.mAction1Title   = action1Title;
        this.mAction2Title   = action2Title;
        this.mListener       = listener;
    }

    static public void select(Context context,
                              String googleID, String facebookID, boolean isFirstGoogle,
                              String title, String action1Title, String action2Title,
                              final SmartAdAlertListener callback)
    {
        new SmartAdAlert(context,
                googleID, facebookID, isFirstGoogle,
                title, action1Title, action2Title, callback).show();
    }

    static public void select(Context context,
                              String googleID, String facebookID,
                              String title, String action1Title, String action2Title,
                              final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context, googleID, facebookID, true, title, action1Title, action2Title, callback);
    }

    static public void confirm(Context context,
                               String googleID, String facebookID, boolean isFirstGoogle,
                               String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context,
                googleID, facebookID, isFirstGoogle,
                title, context.getString(android.R.string.ok), context.getString(android.R.string.cancel), callback);
    }

    static public void confirm(Context context,
                               String googleID, String facebookID,
                               String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.confirm(context, googleID, facebookID, true, title, callback);
    }

    static public void alert(Context context,
                             String googleID, String facebookID, boolean isFirstGoogle,
                             String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.select(context,
                googleID, facebookID, isFirstGoogle,
                title, context.getString(android.R.string.ok), null, callback);
    }

    static public void alert(Context context,
                             String googleID, String facebookID,
                             String title, final SmartAdAlertListener callback)
    {
        SmartAdAlert.alert(context, googleID, facebookID, true, title, callback);
    }

    // OnSmartAdBannerListener *********************************************************************

    @Override
    public void onSmartAdBannerDone(int type) {
        mLoading.setVisibility(View.GONE);
        mBtnAction1.setEnabled(true);
        mBtnAction2.setEnabled(true);
    }

    @Override
    public void onSmartAdBannerFail(String lastError) {
        mLoading.setVisibility(View.GONE);
        mBtnAction1.setEnabled(true);
        mBtnAction2.setEnabled(true);
    }

    public void setAalertButtonDelayMilliseconds(int milliseconds) {
        mAalertButtonDelayMilliseconds = milliseconds;
    }

    // Callback Listener ***************************************************************************

    public interface SmartAdAlertListener {
        void result(int buttonIndex);
    }
}