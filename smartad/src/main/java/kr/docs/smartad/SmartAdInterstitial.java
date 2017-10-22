package kr.docs.smartad;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdInterstitial implements com.facebook.ads.InterstitialAdListener {

    private OnSmartAdInterstitialListener mListener;

    private Context mContext;
    private boolean mIsAutoStart;
    private boolean mIsFirstGoogle;
    private String  mGoogleID;
    private String  mFacebookID;

    private com.google.android.gms.ads.InterstitialAd mGoogleAd;
    private com.facebook.ads.InterstitialAd           mFacebookAd;

    public SmartAdInterstitial(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle, boolean isAutoStart) {
        if (callback instanceof OnSmartAdInterstitialListener) {
            mListener = (OnSmartAdInterstitialListener) callback;
        } else if (context instanceof OnSmartAdInterstitialListener) {
            mListener = (OnSmartAdInterstitialListener) context;
        }

        this.mContext = context;
        this.mIsFirstGoogle = isFirstGoogle;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
        this.mIsAutoStart = isAutoStart;

        loadAd();
    }

    public void loadAd() {
        if (SmartAd.IsShowAd(this)) {
            if (mIsFirstGoogle) loadGoogle();
            else loadFacebook();
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

    static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle, boolean isAutoStart) {
        SmartAdInterstitial ad = new SmartAdInterstitial(context, callback, googleID, facebookID, isFirstGoogle, isAutoStart);
        return ad;
    }

    static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, String googleID, String facebookID) {
        return SmartAdInterstitial.showAdWidthCallback(context, callback, googleID, facebookID, true, true);
    }

    static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID, boolean isFirstGoogle) {
        return SmartAdInterstitial.showAdWidthCallback(context, null, googleID, facebookID, isFirstGoogle, true);
    }

    static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID) {
        return SmartAdInterstitial.showAdWidthCallback(context, null, googleID, facebookID, true, true);
    }

    private void onDone(int type) {
        if (mListener!=null) {
            mListener.OnSmartAdInterstitialDone(type);
        }
    }

    private void onFail(String lastError) {
        if (mListener!=null) {
            mListener.OnSmartAdInterstitialFail(lastError);
            destroy();
        }
    }

    // 구글 *****************************************************************************************

    private void loadGoogle() {
        mGoogleAd = new com.google.android.gms.ads.InterstitialAd(mContext);
        mGoogleAd.setAdUnitId(mGoogleID);
        mGoogleAd.setAdListener(mGoogleListener);
        mGoogleAd.loadAd(SmartAd.getGoogleAdRequest());
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

            if (mIsFirstGoogle) loadFacebook();
            else onFail("SmartAd Error : type=Google, message="+i);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            destroy();
        }
    };

    // 페이스북 **************************************************************************************

    private void loadFacebook() {
        mFacebookAd = new com.facebook.ads.InterstitialAd(mContext, mFacebookID);
        mFacebookAd.setAdListener(this);
        mFacebookAd.loadAd();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (mIsAutoStart) showLoadedAd();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        ad.destroy();
        mFacebookAd.destroy();
        mFacebookAd = null;

        if (!mIsFirstGoogle) loadGoogle();
        else onFail("SmartAd Error : type=Facebook, message="+adError.getErrorMessage());
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        ad.destroy();
        destroy();
    }

    @Override public void onAdClicked(Ad ad) {}
    @Override public void onLoggingImpression(Ad ad) {}
    @Override public void onInterstitialDisplayed(Ad ad) {}

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdInterstitialListener {
        void OnSmartAdInterstitialDone(int type);
        void OnSmartAdInterstitialFail(String lastError);
    }
}
