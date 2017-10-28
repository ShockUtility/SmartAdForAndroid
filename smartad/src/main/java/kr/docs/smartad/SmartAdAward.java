package kr.docs.smartad;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdAward implements com.google.android.gms.ads.reward.RewardedVideoAdListener, com.facebook.ads.RewardedVideoAdListener {

    private OnSmartAdAwardListener                            mListener;
    private Context                                           mContext;
    private String                                            mGoogleID;
    private String                                            mFacebookID;
    private AlertDialog                                       mLoadingAlert;

    @SmartAd.SmartAdOrder
    private int                                               mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private com.google.android.gms.ads.reward.RewardedVideoAd mGoogleAd;
    private com.facebook.ads.RewardedVideoAd                  mFacebookAd;
    private boolean                                           mIsAwardShown;
    private boolean                                           mIsAwardClicked;

    public SmartAdAward(Context context, @SmartAd.SmartAdOrder int adOrder,
                        String googleID, String facebookID, final OnSmartAdAwardListener callback)
    {
        if (callback != null) {
            mListener = callback;
        } else if (context instanceof OnSmartAdAwardListener) {
            mListener = (OnSmartAdAwardListener) context;
        }

        this.mContext = context;
        this.mAdOrder = (adOrder==SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdOrder() : adOrder;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
    }

    public SmartAdAward(Context context, String googleID, String facebookID, final OnSmartAdAwardListener callback) {
        this(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, callback);
    }

    public void showAd() {
        mIsAwardShown = false;
        mIsAwardClicked = false;

        if (SmartAd.IsShowAd(this)) {
            mLoadingAlert = SmartAd.loadingAlert(mContext);

            switch (mAdOrder) {
                case SmartAd.AD_TYPE_GOOGLE  : showGoogle();   break;
                case SmartAd.AD_TYPE_FACEBOOK: showFacebook(); break;
            }
        } else onDone(SmartAd.AD_TYPE_PASS);
    }

    static public SmartAdAward showAdWidthCallback(Context context, @SmartAd.SmartAdOrder int adOrder,
                                                   String googleID, String facebookID,
                                                   final OnSmartAdAwardListener callback)
    {
        SmartAdAward ad = new SmartAdAward(context, adOrder, googleID, facebookID, callback);
        ad.showAd();
        return ad;
    }
    static public SmartAdAward showAdWidthCallback(Context context, String googleID, String facebookID,
                                                   final OnSmartAdAwardListener callback)
    {
        return SmartAdAward.showAdWidthCallback(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID, callback);
    }

    static public SmartAdAward showAd(Context context, @SmartAd.SmartAdOrder int adOrder,
                                      String googleID, String facebookID)
    {
        return SmartAdAward.showAdWidthCallback(context, adOrder, googleID, facebookID, null);
    }

    static public SmartAdAward showAd(Context context, String googleID, String facebookID) {
        return SmartAdAward.showAd(context, SmartAd.AD_TYPE_RANDOM, googleID, facebookID);
    }

    private void onDone(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdAwardDone(type, mIsAwardShown, mIsAwardClicked);
            mListener = null;
        }
        mGoogleAd = null;
        mFacebookAd = null;
    }

    private void onFail(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdAwardFail(type);
            mListener = null;
        }
        mGoogleAd = null;
        mFacebookAd = null;
    }

    // 구글 *****************************************************************************************

    private void showGoogle() {
        if (mGoogleID != null) {
            mGoogleAd = com.google.android.gms.ads.MobileAds.getRewardedVideoAdInstance(mContext);
            mGoogleAd.setRewardedVideoAdListener(this);
            mGoogleAd.loadAd(mGoogleID, SmartAd.getGoogleAdRequest());
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) showFacebook();
            else onFail(SmartAd.AD_TYPE_GOOGLE);
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() { // 광고가 준비 되었다
        if (mLoadingAlert!=null) mLoadingAlert.dismiss();
        mGoogleAd.show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) { // 광고 로딩 실패
        Log.e("SmartAd", "SmartAdAward : type = Google, error code = "+i);

        if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) showFacebook();
        else {
            if (mLoadingAlert!=null) mLoadingAlert.dismiss();
            onFail(SmartAd.AD_TYPE_GOOGLE);
        }
    }

    @Override
    public void onRewarded(com.google.android.gms.ads.reward.RewardItem rewardItem) {
        mIsAwardShown = true;
    }

    @Override
    public void onRewardedVideoAdClosed() {
        onDone(SmartAd.AD_TYPE_GOOGLE);
    }

    @Override public void onRewardedVideoAdOpened() {}
    @Override public void onRewardedVideoStarted() {}
    @Override public void onRewardedVideoAdLeftApplication() {}

    // 페이스북 : 아직 국내에 보상 광고가 들어오지 않았다 ******************************************************

    private void showFacebook() {
        if (mFacebookID != null) {
            mFacebookAd = new com.facebook.ads.RewardedVideoAd(mContext, mFacebookID);
            mFacebookAd.setAdListener(this);
            mFacebookAd.loadAd();
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) showGoogle();
            else onFail(SmartAd.AD_TYPE_FACEBOOK);
        }
    }

    @Override
    public void onAdLoaded(com.facebook.ads.Ad ad) {
        if (mLoadingAlert!=null) mLoadingAlert.dismiss();
        mFacebookAd.show();
    }

    @Override
    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
        Log.e("SmartAd", "SmartAdAward : type = Facebook, error code = "+adError.getErrorCode()+", error message = "+adError.getErrorMessage());

        if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) showGoogle();
        else {
            if (mLoadingAlert!=null) mLoadingAlert.dismiss();
            onFail(SmartAd.AD_TYPE_FACEBOOK);
        }
    }

    @Override
    public void onRewardedVideoCompleted() {
        mIsAwardShown = true;
    }

    @Override
    public void onAdClicked(com.facebook.ads.Ad ad) {
        mIsAwardClicked = true;
    }

    @Override
    public void onRewardedVideoClosed() {
        onDone(SmartAd.AD_TYPE_FACEBOOK);
    }

    @Override public void onLoggingImpression(com.facebook.ads.Ad ad) {}

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdAwardListener {
        void onSmartAdAwardDone(int adType, boolean isAwardShown, boolean isAwardClicked);
        void onSmartAdAwardFail(int adType);
    }
}
