package kr.docs.smartad;

import android.content.Context;
import android.util.Log;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdInterstitial implements com.facebook.ads.InterstitialAdListener {

    private OnSmartAdInterstitialListener             mListener;
    private Context                                   mContext;
    private boolean                                   mIsAutoStart;
    private String                                    mGoogleID;
    private String                                    mFacebookID;

    @SmartAd.SmartAdOrder
    private int                                       mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private com.google.android.gms.ads.InterstitialAd mGoogleAd;
    private com.facebook.ads.InterstitialAd           mFacebookAd;

    public SmartAdInterstitial(Context context, @SmartAd.SmartAdOrder int adOrder,
                               String googleID, String facebookID, boolean isAutoStart,
                               final OnSmartAdInterstitialListener callback)
    {
        if (callback != null) {
            mListener = callback;
        } else if (context instanceof OnSmartAdInterstitialListener) {
            mListener = (OnSmartAdInterstitialListener) context;
        }

        this.mContext = context;
        this.mAdOrder = (adOrder==SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdOrder() : adOrder;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
        this.mIsAutoStart = isAutoStart;

        loadAd();
    }

    private void loadAd() {
        if (SmartAd.IsShowAd(this)) {
            switch (mAdOrder) {
                case SmartAd.AD_TYPE_GOOGLE  : loadGoogle();   break;
                case SmartAd.AD_TYPE_FACEBOOK: loadFacebook(); break;
            }
        } else {
            onDone(SmartAd.AD_TYPE_PASS);
            destroy();
        }
    }

    public void showLoadedAd() {
        if (SmartAd.IsShowAd(this)) {
            if ((mGoogleAd != null) && (mGoogleAd.isLoaded())) {
                mGoogleAd.show();
                onDone(SmartAd.AD_TYPE_GOOGLE);
            } else if ((mFacebookAd != null) && (mFacebookAd.isAdLoaded())) {
                mFacebookAd.show();
                onDone(SmartAd.AD_TYPE_FACEBOOK);
            }
        } else {
            onDone(SmartAd.AD_TYPE_PASS);
            destroy();
        }
    }

    public void destroy() {
        if (mGoogleAd!=null) {
            mGoogleAd.setAdListener(null);
            mGoogleAd = null;
        }
        if (mFacebookAd!=null) {
            mFacebookAd.setAdListener(null);
            mFacebookAd.destroy();
            mFacebookAd = null;
        }
        mListener = null;
    }

    static public SmartAdInterstitial showAdWidthCallback(Context context, @SmartAd.SmartAdOrder int adOrder,
                                                          String googleID, String facebookID, boolean isAutoStart,
                                                          final OnSmartAdInterstitialListener callback)
    {
        SmartAdInterstitial ad = new SmartAdInterstitial(context, adOrder, googleID, facebookID, isAutoStart, callback);
        return ad;
    }

    static public SmartAdInterstitial showAdWidthCallback(Context context, String googleID, String facebookID,
                                                          final OnSmartAdInterstitialListener callback)
    {
        return SmartAdInterstitial.showAdWidthCallback(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, true, callback);
    }

    static public SmartAdInterstitial showAd(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID,
                                             String facebookID, boolean isAutoStart)
    {
        return SmartAdInterstitial.showAdWidthCallback(context, adOrder, googleID, facebookID, isAutoStart, null);
    }

    static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID) {
        return SmartAdInterstitial.showAd(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, true);
    }

    private void onDone(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdInterstitialDone(type);
        }
    }

    private void onFail(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdInterstitialFail(type);
            destroy();
        }
    }

    // 구글 *****************************************************************************************

    private void loadGoogle() {
        if (mGoogleID != null) {
            mGoogleAd = new com.google.android.gms.ads.InterstitialAd(mContext);
            mGoogleAd.setAdUnitId(mGoogleID);
            mGoogleAd.setAdListener(mGoogleListener);
            mGoogleAd.loadAd(SmartAd.getGoogleAdRequest());
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) loadFacebook();
            else onFail(SmartAd.AD_TYPE_GOOGLE);
        }
    }

    private com.google.android.gms.ads.AdListener mGoogleListener = new com.google.android.gms.ads.AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            if (mIsAutoStart) showLoadedAd();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            Log.e("SmartAd", "SmartAdInterstitial : type = Google, error code = "+i);
            mGoogleAd = null;

            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) loadFacebook();
            else onFail(SmartAd.AD_TYPE_GOOGLE);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            if (mListener!=null) mListener.onSmartAdInterstitialClose(SmartAd.AD_TYPE_GOOGLE);
            destroy();
        }
    };

    // 페이스북 **************************************************************************************

    private void loadFacebook() {
        if (mFacebookID != null) {
            mFacebookAd = new com.facebook.ads.InterstitialAd(mContext, mFacebookID);
            mFacebookAd.setAdListener(this);
            mFacebookAd.loadAd();
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) loadGoogle();
            else onFail(SmartAd.AD_TYPE_FACEBOOK);
        }
    }

    @Override
    public void onAdLoaded(com.facebook.ads.Ad ad) {
        if (mIsAutoStart) showLoadedAd();
    }

    @Override
    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
        Log.e("SmartAd", "SmartAdInterstitial : type = Facebook, error code = "+adError.getErrorCode()+", error message = "+adError.getErrorMessage());

        ad.destroy();
        if(mFacebookAd !=null){
            mFacebookAd.destroy();
            mFacebookAd = null;
        }

        if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) loadGoogle();
        else onFail(SmartAd.AD_TYPE_FACEBOOK);
    }

    @Override
    public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
        ad.destroy();
        if (mListener!=null) mListener.onSmartAdInterstitialClose(SmartAd.AD_TYPE_FACEBOOK);
        destroy();
    }

    @Override public void onAdClicked(com.facebook.ads.Ad ad) {}
    @Override public void onLoggingImpression(com.facebook.ads.Ad ad) {}
    @Override public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {}

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdInterstitialListener {
        void onSmartAdInterstitialDone(int adType);
        void onSmartAdInterstitialFail(int adType);
        void onSmartAdInterstitialClose(int adType);
    }
}
