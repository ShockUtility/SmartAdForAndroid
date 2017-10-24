package kr.docs.smartad;

import android.content.Context;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdInterstitial implements com.facebook.ads.InterstitialAdListener {

    private OnSmartAdInterstitialListener             mListener;
    private Context                                   mContext;
    private boolean                                   mIsAutoStart;
    private String                                    mGoogleID;
    private String                                    mFacebookID;

    @SmartAd.SmartAdType
    private int                                       mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private com.google.android.gms.ads.InterstitialAd mGoogleAd;
    private com.facebook.ads.InterstitialAd           mFacebookAd;

    public SmartAdInterstitial(Context context, Object callback, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID, boolean isAutoStart) {
        if (callback instanceof OnSmartAdInterstitialListener) {
            mListener = (OnSmartAdInterstitialListener) callback;
        } else if (context instanceof OnSmartAdInterstitialListener) {
            mListener = (OnSmartAdInterstitialListener) context;
        }

        this.mContext = context;
        this.mAdOrder = (adOrder==SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdType() : adOrder;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
        this.mIsAutoStart = isAutoStart;

        loadAd();
    }

    public void loadAd() {
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
        if ((mGoogleAd!=null) && (mGoogleAd.isLoaded())) {
            mGoogleAd.show();
            onDone(SmartAd.AD_TYPE_GOOGLE);
        } else if ((mFacebookAd!=null) && (mFacebookAd.isAdLoaded())) {
            mFacebookAd.show();
            onDone(SmartAd.AD_TYPE_FACEBOOK);
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

    static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID, boolean isAutoStart) {
        SmartAdInterstitial ad = new SmartAdInterstitial(context, callback, adOrder, googleID, facebookID, isAutoStart);
        return ad;
    }

    static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, String googleID, String facebookID) {
        return SmartAdInterstitial.showAdWidthCallback(context, callback, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, true);
    }

    static public SmartAdInterstitial showAd(Context context, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID, boolean isAutoStart) {
        return SmartAdInterstitial.showAdWidthCallback(context, null, adOrder, googleID, facebookID, isAutoStart);
    }

    static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID) {
        return SmartAdInterstitial.showAdWidthCallback(context, null, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, true);
    }

    private void onDone(int type) {
        if (mListener!=null) {
            mListener.onSmartAdInterstitialDone(type);
        }
    }

    private void onFail(String lastError) {
        if (mListener!=null) {
            mListener.onSmartAdInterstitialFail(lastError);
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
            else onFail("SmartAd Error : Don't have google id!");
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
            mGoogleAd = null;

            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) loadFacebook();
            else onFail("SmartAd Error : type=Google, message="+i);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            if (mListener!=null) mListener.onSmartAdInterstitialClose();
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
            else onFail("SmartAd Error : Don't have facebook id!");
        }
    }

    @Override
    public void onAdLoaded(com.facebook.ads.Ad ad) {
        if (mIsAutoStart) showLoadedAd();
    }

    @Override
    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
        ad.destroy();
        mFacebookAd.destroy();
        mFacebookAd = null;

        if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) loadGoogle();
        else onFail("SmartAd Error : type=Facebook, message="+adError.getErrorMessage());
    }

    @Override
    public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
        ad.destroy();
        if (mListener!=null) mListener.onSmartAdInterstitialClose();
        destroy();
    }

    @Override public void onAdClicked(com.facebook.ads.Ad ad) {}
    @Override public void onLoggingImpression(com.facebook.ads.Ad ad) {}
    @Override public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {}

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdInterstitialListener {
        void onSmartAdInterstitialDone(int type);
        void onSmartAdInterstitialFail(String lastError);
        void onSmartAdInterstitialClose();
    }
}
