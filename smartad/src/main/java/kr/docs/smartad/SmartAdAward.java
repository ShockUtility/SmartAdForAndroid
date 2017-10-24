package kr.docs.smartad;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdAward implements com.google.android.gms.ads.reward.RewardedVideoAdListener, com.facebook.ads.RewardedVideoAdListener {

    private OnSmartAdAwardListener                            mListener;
    private Context                                           mContext;
    private String                                            mGoogleID;
    private String                                            mFacebookID;
    private AlertDialog                                       mLoadingAlert;

    @SmartAd.SmartAdType
    private int                                               mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private com.google.android.gms.ads.reward.RewardedVideoAd mGoogleAd;
    private com.facebook.ads.RewardedVideoAd                  mFacebookAd;
    private boolean                                           mIsAwardShow;
    private boolean                                           mIsAwardClick;

    public SmartAdAward(Context context, Object callback, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID) {
        if (callback instanceof OnSmartAdAwardListener) {
            mListener = (OnSmartAdAwardListener) callback;
        } else if (context instanceof OnSmartAdAwardListener) {
            mListener = (OnSmartAdAwardListener) context;
        }

        this.mContext = context;
        this.mAdOrder = (adOrder==SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdType() : adOrder;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
    }

    public SmartAdAward(Context context, Object callback, String googleID, String facebookID) {
        this(context, callback, SmartAd.AD_TYPE_RANDOM, googleID, facebookID);
    }

    public void showAd() {
        mIsAwardShow = false;
        mIsAwardClick = false;

        if (SmartAd.IsShowAd(this)) {
            mLoadingAlert = SmartAd.loadingAlert(mContext);

            switch (mAdOrder) {
                case SmartAd.AD_TYPE_GOOGLE  : showGoogle();   break;
                case SmartAd.AD_TYPE_FACEBOOK: showFacebook(); break;
            }
        } else onDone(SmartAd.AD_TYPE_PASS);
    }

    static public void showAdWidthCallback(Context context, Object callback, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID) {
        SmartAdAward ad = new SmartAdAward(context, callback, adOrder, googleID, facebookID);
        ad.showAd();
    }
    static public void showAdWidthCallback(Context context, Object callback, String googleID, String facebookID) {
        SmartAdAward.showAdWidthCallback(context, callback, SmartAd.AD_TYPE_RANDOM, googleID, facebookID);
    }

    static public void showAd(Context context, @SmartAd.SmartAdType int adOrder, String googleID, String facebookID) {
        SmartAdAward.showAdWidthCallback(context, null, adOrder, googleID, facebookID);
    }

    static public void showAd(Context context, String googleID, String facebookID) {
        SmartAdAward.showAdWidthCallback(context, null, SmartAd.AD_TYPE_RANDOM, googleID, facebookID);
    }

    private void onDone(int type) {
        if (mListener!=null) {
            mListener.onSmartAdAwardDone(type, mIsAwardShow, mIsAwardClick);
            mListener = null;
        }
        mGoogleAd = null;
        mFacebookAd = null;
    }

    private void onFail(String lastError) {
        if (mListener!=null) {
            mListener.onSmartAdAwardFail(lastError);
            mListener = null;
        }
        mGoogleAd = null;
        mFacebookAd = null;
    }

    // 구글 *****************************************************************************************

    private void showGoogle() {
        mGoogleAd = com.google.android.gms.ads.MobileAds.getRewardedVideoAdInstance(mContext);
        mGoogleAd.setRewardedVideoAdListener(this);
        mGoogleAd.loadAd(mGoogleID, SmartAd.getGoogleAdRequest());
    }

    @Override
    public void onRewardedVideoAdLoaded() { // 광고가 준비 되었다
        if (mLoadingAlert!=null) mLoadingAlert.dismiss();
        mGoogleAd.show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) { // 광고 로딩 실패
        if (mAdOrder == SmartAd.AD_TYPE_GOOGLE) {
            showFacebook();
        } else {
            if (mLoadingAlert!=null) mLoadingAlert.dismiss();
            onFail("SmartAd Error : type=Google, message="+i);
        }
    }

    @Override
    public void onRewarded(com.google.android.gms.ads.reward.RewardItem rewardItem) { // 광고를 모두 시청 했다
        mIsAwardShow = true;
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
        mFacebookAd = new com.facebook.ads.RewardedVideoAd(mContext, "YOUR_PLACEMENT_ID");
        mFacebookAd.setAdListener(this);
        mFacebookAd.loadAd();
    }

    @Override
    public void onAdLoaded(com.facebook.ads.Ad ad) {
        if (mLoadingAlert!=null) mLoadingAlert.dismiss();
        mFacebookAd.show();
    }

    @Override
    public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
        if (mAdOrder == SmartAd.AD_TYPE_FACEBOOK) {
            showGoogle();
        } else {
            if (mLoadingAlert!=null) mLoadingAlert.dismiss();
            onFail("SmartAd Error : type=Facebook, message="+adError.getErrorMessage());
        }
    }

    @Override
    public void onRewardedVideoCompleted() {
        mIsAwardShow = true;
    }

    @Override
    public void onAdClicked(com.facebook.ads.Ad ad) {
        mIsAwardClick = true;
    }

    @Override
    public void onRewardedVideoClosed() {
        onDone(SmartAd.AD_TYPE_FACEBOOK);
    }

    @Override public void onLoggingImpression(com.facebook.ads.Ad ad) {}

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdAwardListener {
        void onSmartAdAwardDone(int type, boolean isAwardShow, boolean isAwardClick);
        void onSmartAdAwardFail(String lastError);
    }
}
