package kr.docs.smartad;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by shock on 2017. 10. 13..
 */

public class SmartAdBanner extends LinearLayout {
    static final public int AD_SIZE_AUTO      = 0;
    static final public int AD_SIZE_SMALL     = 1;
    static final public int AD_SIZE_LARGE     = 2;
    static final public int AD_SIZE_RECTANGLE = 3;

    private OnSmartAdBannerListener             mListener;
    private int                                 mAdSize;
    private boolean                             mIsAutoStart;
    private String                              mGoogleID;
    private String                              mFacebookID;
    private boolean                             mIsLoadedLayout = false;

    @SmartAd.SmartAdOrder
    private int                                 mAdOrder = SmartAd.AD_TYPE_RANDOM;

    private com.google.android.gms.ads.AdView   mGoogleAdView;
    private com.facebook.ads.AdView             mFacebookAdView;

    public SmartAdBanner(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);

        if (context instanceof OnSmartAdBannerListener) {
            mListener = (OnSmartAdBannerListener) context;
        }

        mAdSize         = AD_SIZE_SMALL;
        mGoogleID       = null;
        mFacebookID     = null;
    }

    public SmartAdBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(LinearLayout.VERTICAL);

        if (context instanceof OnSmartAdBannerListener) {
            mListener = (OnSmartAdBannerListener) context;
        }

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.SmartAdBanner);

        mAdSize        = types.getInt(R.styleable.SmartAdBanner_adv_BannerSize, AD_SIZE_AUTO);
        mIsAutoStart   = types.getBoolean(R.styleable.SmartAdBanner_adv_IsAutoStart, true);
        mGoogleID      = types.getString(R.styleable.SmartAdBanner_adv_GoogleID);
        mFacebookID    = types.getString(R.styleable.SmartAdBanner_adv_FacebookID);

        switch (types.getInt(R.styleable.SmartAdBanner_adv_AdOrder, SmartAd.AD_TYPE_RANDOM)) {
            case SmartAd.AD_TYPE_RANDOM  : mAdOrder = SmartAd.randomAdOrder();   break;
            case SmartAd.AD_TYPE_GOOGLE  : mAdOrder = SmartAd.AD_TYPE_GOOGLE;   break;
            case SmartAd.AD_TYPE_FACEBOOK: mAdOrder = SmartAd.AD_TYPE_FACEBOOK; break;
        }

        if (mIsAutoStart) showAd();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 뷰가 로드되고 레이아웃이 처음 적용되어 사이즈가 처음 조정 되었을때 베너 최소 크기에 대한 경고 메세지를 표시
//        if (mIsAutoStart) {
            try {
                if (!mIsLoadedLayout && w > 0) {
                    mIsLoadedLayout = true;

                    // 광고 공간에 대한 경고 메세지를 처리
                    float widthPX = w / getContext().getResources().getDisplayMetrics().density;
                    if (((mAdSize == AD_SIZE_RECTANGLE) && (widthPX < 300.0)) ||
                        ((mAdSize != AD_SIZE_RECTANGLE) && (widthPX < 320.0)))
                    {
                        Log.w("SmartAd",
                                "There is a problem with the width for displaying the ad!\n"+
                                "   - AD_SIZE_AUTO      : Min 320dp\n"+
                                "   - AD_SIZE_SMALL     : Min 320dp\n"+
                                "   - AD_SIZE_LARGE     : Min 320dp\n"+
                                "   - AD_SIZE_RECTANGLE : Min 300dp\n"+
                                "   - Current DP        : "+widthPX+"dp)");
                    }
                }
            } catch (Exception ex) {
                mIsLoadedLayout = false;
            }
//        }
    }

    public void showAd() {
        if (SmartAd.IsShowAd(this)) {
            switch (mAdOrder) {
                case SmartAd.AD_TYPE_GOOGLE  : showGoogle();   break;
                case SmartAd.AD_TYPE_FACEBOOK: showFacebook(); break;
            }
        } else onDone(SmartAd.AD_TYPE_PASS);
    }

    public void showAd(int adSize, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID) {
        this.mAdSize = adSize;
        this.mAdOrder = (adOrder == SmartAd.AD_TYPE_RANDOM) ? SmartAd.randomAdOrder() : adOrder;
        this.mGoogleID = googleID;
        this.mFacebookID = facebookID;

        showAd();
    }

    public void setOnSmartAdBannerListener(OnSmartAdBannerListener listener) {
        mListener = listener;
    }

    private void onDone(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdBannerDone(type);
            mListener = null;
        }
    }

    private void onFail(@SmartAd.SmartAdResult int type) {
        if (mListener!=null) {
            mListener.onSmartAdBannerFail(type);
            mListener = null;
        }
    }

    public void destroy() {
        if (mGoogleAdView!=null) mGoogleAdView.destroy();
        if (mFacebookAdView!=null) mFacebookAdView.destroy();
    }

    // Google **************************************************************************************

    private void showGoogle() {
        if (mGoogleID != null) {
            if (mGoogleAdView != null) {
                this.removeView(mGoogleAdView);
                mGoogleAdView = null;
            }

            mGoogleAdView = new com.google.android.gms.ads.AdView(getContext());
            this.addView(mGoogleAdView);
            mGoogleAdView.setAdSize(getGoogleAdSize());
            mGoogleAdView.setAdUnitId(mGoogleID);
            mGoogleAdView.setAdListener(mGoogleListener);
            mGoogleAdView.loadAd(SmartAd.getGoogleAdRequest());
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) showFacebook();
            else onFail(SmartAd.AD_TYPE_GOOGLE);
        }
    }

    private com.google.android.gms.ads.AdSize getGoogleAdSize() {
        switch (mAdSize) {
            case AD_SIZE_SMALL     : return com.google.android.gms.ads.AdSize.BANNER;
            case AD_SIZE_LARGE     : return com.google.android.gms.ads.AdSize.LARGE_BANNER;
            case AD_SIZE_RECTANGLE : return com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE;
        }
        return com.google.android.gms.ads.AdSize.SMART_BANNER;
    }

    private com.google.android.gms.ads.AdListener mGoogleListener = new com.google.android.gms.ads.AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            onDone(SmartAd.AD_TYPE_GOOGLE);
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            Log.e("SmartAd", "SmartAdBanner : type = Google, error code = "+i);

            if (mGoogleAdView!=null) {
                SmartAdBanner.this.removeView(mGoogleAdView);
                mGoogleAdView.destroy();
                mGoogleAdView = null;
            }

            if ((mAdOrder == SmartAd.AD_TYPE_GOOGLE) && (mFacebookID != null)) showFacebook();
            else onFail(SmartAd.AD_TYPE_GOOGLE);
        }
    };

    // Facebook ************************************************************************************

    private void showFacebook() {
        if (mFacebookID != null) {
            if (mFacebookAdView != null) {
                this.removeView(mFacebookAdView);
                mFacebookAdView = null;
            }

            mFacebookAdView = new com.facebook.ads.AdView(getContext(), mFacebookID, getFacebookAdSize());
            this.addView(mFacebookAdView);
            mFacebookAdView.setAdListener(mFacebookListener);
            mFacebookAdView.loadAd();
        } else {
            if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) showGoogle();
            else onFail(SmartAd.AD_TYPE_FACEBOOK);
        }
    }

    private com.facebook.ads.AdSize getFacebookAdSize() {
        switch (mAdSize) {
            case AD_SIZE_SMALL     : return new com.facebook.ads.AdSize(320, 50);//com.facebook.ads.AdSize.BANNER_HEIGHT_50;
            case AD_SIZE_LARGE     : return com.facebook.ads.AdSize.BANNER_HEIGHT_90;
            case AD_SIZE_RECTANGLE : return com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250;
        }
        return com.facebook.ads.AdSize.BANNER_HEIGHT_50;
    }

    private com.facebook.ads.AdListener mFacebookListener = new com.facebook.ads.AdListener() {
        @Override
        public void onAdLoaded(com.facebook.ads.Ad ad) {
            onDone(SmartAd.AD_TYPE_FACEBOOK);
        }

        @Override
        public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
            Log.e("SmartAd", "SmartAdBanner : type = Facebook, error code = "+adError.getErrorCode()+", error message = "+adError.getErrorMessage());

            if (mFacebookAdView!=null) {
                SmartAdBanner.this.removeView(mFacebookAdView);
                mFacebookAdView.destroy();
                mFacebookAdView = null;
            }

            if ((mAdOrder == SmartAd.AD_TYPE_FACEBOOK) && (mGoogleID != null)) showGoogle();
            else onFail(SmartAd.AD_TYPE_FACEBOOK);
        }

        @Override public void onAdClicked(com.facebook.ads.Ad ad) {}
        @Override public void onLoggingImpression(com.facebook.ads.Ad ad) {}
    };

    // Callback Listener ***************************************************************************

    public interface OnSmartAdBannerListener {
        void onSmartAdBannerDone(int adType);
        void onSmartAdBannerFail(int adType);
    }
}
