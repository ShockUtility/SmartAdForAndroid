package kr.docs.smartad;

import android.app.AlertDialog;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

/**
 * Created by shock on 2017. 8. 30..
 */

public class SmartAdAward implements RewardedVideoAdListener {

    private OnSmartAdAwardListener mListener;

    private Context mContext;
    private String  mGoogleID;
    private String  mFacebookID;            // Facebook is not ready yet!!!
    private boolean mIsFirstGoogle;
    private AlertDialog mLoadingAlert;

    private RewardedVideoAd mGoogleAd;
    private boolean mIsAward;

    public SmartAdAward(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle) {
        if (callback instanceof OnSmartAdAwardListener) {
            mListener = (OnSmartAdAwardListener) callback;
        } else if (context instanceof OnSmartAdAwardListener) {
            mListener = (OnSmartAdAwardListener) context;
        }

        this.mContext = context;
        this.mIsFirstGoogle = isFirstGoogle;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;
    }

    public void showAd() {
        mIsAward = false;

        if (SmartAd.IsShowAd(this)) {
            mLoadingAlert = SmartAd.loadingAlert(mContext);

            if (mIsFirstGoogle) showGoogle();
            else showFacebook();
        } else onDone(SmartAd.AD_TYPE_PASS, false);
    }

    static public void showAdWidthCallback(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle) {
        SmartAdAward ad = new SmartAdAward(context, callback, googleID, facebookID, isFirstGoogle);
        ad.showAd();
    }
    static public void showAdWidthCallback(Context context, Object callback, String googleID, String facebookID) {
        SmartAdAward.showAdWidthCallback(context, callback, googleID, facebookID, true);
    }

    static public void showAd(Context context, String googleID, String facebookID, boolean isFirstGoogle) {
        SmartAdAward.showAdWidthCallback(context, null, googleID, facebookID, isFirstGoogle);
    }

    static public void showAd(Context context, String googleID, String facebookID) {
        SmartAdAward.showAdWidthCallback(context, null, googleID, facebookID, true);
    }

    private void onDone(int type, boolean isAward) {
        if (mListener!=null) {
            mListener.OnSmartAdAwardDone(type, isAward);
            mListener = null;
        }
        mGoogleAd = null;
    }

    private void onFail(String lastError) {
        if (mListener!=null) {
            mListener.OnSmartAdAwardFail(lastError);
            mListener = null;
        }
        mGoogleAd = null;
    }

    // 구글 *****************************************************************************************

    private void showGoogle() {
        mGoogleAd = MobileAds.getRewardedVideoAdInstance(mContext);
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
        if (mLoadingAlert!=null) mLoadingAlert.dismiss();
        onFail("SmartAd Error : type=Google, message="+i);
    }

    @Override
    public void onRewarded(RewardItem rewardItem) { // 광고를 모두 시청 했다
        mIsAward = true;
    }

    @Override
    public void onRewardedVideoAdClosed() {
        onDone(SmartAd.AD_TYPE_GOOGLE, mIsAward);
    }

    @Override public void onRewardedVideoAdOpened() {}

    @Override public void onRewardedVideoStarted() {}

    @Override public void onRewardedVideoAdLeftApplication() {}

    // 페이스북 : 아직 국내에 보상 광고가 들어오지 않았다 ******************************************************

    private void showFacebook() {
        onFail("SmartAd Error : type=Facebook, message=Facebook will be ready soon!!!");
    }

    // 반환 인터페이스 *********************************************************************************

    public interface OnSmartAdAwardListener {
        void OnSmartAdAwardDone(int type, boolean isAward);
        void OnSmartAdAwardFail(String lastError);
    }
}
