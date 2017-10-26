[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![GitHub release](https://img.shields.io/github/release/ShockUtility/SmartAdForAndroid.svg)](https://github.com/ShockUtility/SmartAdForAndroid)
[![English](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/en.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid)
[![Korea](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/kr.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_kr.md)
[![Japan](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/jp.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_jp.md)

# SmartAd 소개
SmartAd 는 iOS 와 Android 에서 AdMob 과 Audience Network 광고 프레임웍을 간편하게 사용하기 위한 라이브러리 입니다.

- [SmartAd for Android](https://github.com/ShockUtility/SmartAdForAndroid) -> [Demo Project](https://github.com/ShockUtility/SmartAdDemoForAndroid)
- [SmartAd for Swift](https://github.com/ShockUtility/SmartAdForSwift) -> [Demo Project](https://github.com/ShockUtility/SmartAdDemoForSwift)

# 설치
```java
dependencies {
    compile 'kr.docs:smart-ad:0.2.7'
} 
```

# 의존성
```java
dependencies {
    compile 'com.google.android.gms:play-services-ads:11.+'
    compile 'com.facebook.android:audience-network-sdk:4.+'
} 
```

# 지원되는 광고 형식
## Google AdMob
- AdView (기본 베너)
- InterstitialAd (전면 광고)
- RewardedVideoAd (보상 광고)

## Facebook Audience Network
- AdView (기본 베너)
- InterstitialAd (삽입 광고)
- RewardedVideoAd (보상 광고)

# 사용법

## 기본 베너 (SmartAdBanner)
UI 화면에 뷰를 추가하고 'SmartAdBanner' 클래스를 선택한 후 5개의 프로퍼티만 셋팅하면 코딩 없이 바로 동작 됩니다.

```xml
<kr.docs.smartad.SmartAdBanner
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:adv_AdOrder="Random"
    app:adv_BannerSize="Auto"
    app:adv_IsAutoStart="true"
    app:adv_FacebookID="YOUR_PLACEMENT_ID"
    app:adv_GoogleID="YOUR_PLACEMENT_ID"
/>
```

| SmartAdBanner              | Google (AdView)                 | Facebook (AdView)              |
|-----------------------------|--------------------------------|---------------------------------|
| AD_SIZE_AUTO              | SMART_BANNER               | BANNER_HEIGHT_50          |
| AD_SIZE_SMALL            | BANNER                             | BANNER_HEIGHT_50          |
| AD_SIZE_LARGE            | LARGE_BANNER               | BANNER_HEIGHT_90          |
| AD_SIZE_RECTANGLE   | MEDIUM_RECTANGLE      | RECTANGLE_HEIGHT_250  |

`* 주의 : 4가지 크기의 광고가 지원되며 뷰의 크기가 표시할 광고보다 작은 경우 각각의 프레임웍에 의해서 광고가 표시되지 않을 수 있다.`
<br>
`* 주의 : 가급적이면 뷰의 layout_height 는 wrap_content 로 설정하고 adv_AdSize 를 알맞게 설정 한다면 각각의 프레임웍에 맞는 광고 사이즈로 표기되며 광고가 없을 경우 화면에 표기되지 않을 것입니다.`
<br>

## 전면 광고 (SmartAdInterstitial)
전면 광고 호출하는 예제 코드는 다음과 같다.
```java
// Simple
SmartAdInterstitial mAd = SmartAdInterstitial.showAd(this, "googleID", "facebookID");
// Custom show
SmartAdInterstitial mAd = SmartAdInterstitial.showAd(this, SmartAd.AD_TYPE_GOOGLE, "googleID", "facebookID", false);
mAd.showLoadedAd();
// with Callback
SmartAdInterstitial mAd = SmartAdInterstitial.showAdWidthCallback(this, "googleID", "facebookID",
                            new SmartAdInterstitial.OnSmartAdInterstitialListener() {
                                @Override
                                public void onSmartAdInterstitialDone(int adType) {
                                    // Success...
                                }

                                @Override
                                public void onSmartAdInterstitialFail(int adType) {
                                    // Fail...
                                }

                                @Override
                                public void onSmartAdInterstitialClose(int adType) {
                                    // Close...
                                }
                            });
...
@Override
public void onDestroy() {
    if (mAd!=null) mAd.destroy();  // 이 부분이 없을 경우 호출한 화면이 닫힌 후 다른 화면에서 광고가 표시될 수 있다.
    super.onDestroy();
}
```
상황에 따라 결과값을 반환 받고 싶을 경우 호출하는 클래스에 OnSmartAdInterstitialListener 를 implements 한다.
```java
public class MainActivity extends AppCompatActivity implements SmartAdInterstitial.OnSmartAdInterstitialListener {
    @Override
    public void onSmartAdInterstitialDone(int type) {
        // Success...
    }

    @Override
    public void onSmartAdInterstitialFail(int type) {
        // Fail...
    }
    
    @Override
    public void onSmartAdInterstitialClose(int type) {
        // Close...
    }
}
```
 
다음의 함수들을 통해서 상황에 맞는 광고 호출이 가능합니다.
```java
static public SmartAdInterstitial showAdWidthCallback(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAdWidthCallback(Context context, String googleID, String facebookID, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAd(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart)
static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID)

public SmartAdInterstitial(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
public void showLoadedAd()
public void destroy()
```

`* 주의 : destroy() 처리를 안하는 경우 호출한 Activity 가 닫힌 후에도 광고가 호출되는 문제가 발생한다.`

## 보상 광고 (SmartAdAward)
보상 광고 호출하는  예제 코드는 다음과 같다.
```java
// Simple
SmartAdAward.showAd(this, SmartAd.AD_TYPE_FACEBOOK, "googleID", "facebookID");
// with Callback
SmartAdAward.showAdWidthCallback(this, SmartAd.AD_TYPE_RANDOM, "googleID", "facebookID",
                                 new SmartAdAward.OnSmartAdAwardListener() {
                                     @Override
                                     public void onSmartAdAwardDone(int adType, boolean isAwardShown, boolean isAwardClicked) {
                                         // Awarded...
                                     }

                                    @Override
                                    public void onSmartAdAwardFail(int adType) {
                                        // Fail...
                                    }
                                });
```

보상 광고의 결과값을 얻기 위해서 OnSmartAdAwardListener 를 implements 한다.
```java
public class MainActivity extends AppCompatActivity implements SmartAdInterstitial.OnSmartAdAwardListener {
    @Override
    public void onSmartAdAwardDone(int adType, boolean isAward) {
        // Awarded...
    }

    @Override
    public void onSmartAdAwardFail(int adType) {
        // Fail...
    }
}
```

다음의 함수들을 통해서 상황에 맞는 광고 호출이 가능합니다.
```java
static public SmartAdAward showAdWidthCallback(Context context, int adOrder, String googleID, String facebookID, final OnSmartAdAwardListener callback)
static public SmartAdAward showAdWidthCallback(Context context, String googleID, String facebookID, final OnSmartAdAwardListener callback)
static public SmartAdAward showAd(Context context, int adOrder, String googleID, String facebookID)
static public SmartAdAward showAd(Context context, String googleID, String facebookID)

public SmartAdAward(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID, final OnSmartAdAwardListener callback)
public SmartAdAward(Context context, String googleID, String facebookID, OnSmartAdAwardListener callback)
public void showAd()
```

## 얼럿 광고 (SmartAdAlert)
확인 버튼만 있는 알림 얼럿
```java
SmartAdAlert.alert(this,
                   SmartAd.AD_TYPE_RANDOM,
                   "googleID",
                   "facebookID",
                   "Alert Dialog",
                   new SmartAdAlert.SmartAdAlertListener() {
                       @Override
                       public void result(int buttonType) {
                           switch (buttonType) {
                               case SmartAdAlert.BUTTON_OK:
                                   Toast.makeText(MainActivity.this, "SmartAdAlert Alert : OK", Toast.LENGTH_LONG).show();
                                   break;
                               case SmartAdAlert.BUTTON_BACK:
                                   Toast.makeText(MainActivity.this, "SmartAdAlert Alert : Back", Toast.LENGTH_LONG).show();
                                   break;
                           }
                       }
                   });
```
확인/취소 얼럿
```java
SmartAdAlert.confirm(this,
                     SmartAd.AD_TYPE_GOOGLE,
                     "googleID",
                     "facebookID",
                     "Confirm Dialog",
                     new SmartAdAlert.SmartAdAlertListener() {
                         @Override
                         public void result(int buttonType) {
                             switch (buttonType) {
                                 case SmartAdAlert.BUTTON_OK:
                                     Toast.makeText(MainActivity.this, "SmartAdAlert Confirm : OK", Toast.LENGTH_LONG).show();
                                     break;
                                 case SmartAdAlert.BUTTON_CANCEL:
                                     Toast.makeText(MainActivity.this, "SmartAdAlert Confirm : Cancel", Toast.LENGTH_LONG).show();
                                     break;
                                 case SmartAdAlert.BUTTON_BACK:
                                     Toast.makeText(MainActivity.this, "SmartAdAlert Confirm : Back", Toast.LENGTH_LONG).show();
                                     break;
                             }
                         }
                     });
```
선택 버튼 커스터마이징 얼럿
```java
SmartAdAlert.select(this,
                    SmartAd.AD_TYPE_FACEBOOK,
                    "googleID",
                    "facebookID",
                    "Select Dialog",
                    "Yes",
                    "No",
                    new SmartAdAlert.SmartAdAlertListener() {
                        @Override
                        public void result(int buttonType) {
                            switch (buttonType) {
                                case SmartAdAlert.BUTTON_OK:
                                    Toast.makeText(MainActivity.this, "SmartAdAlert Select : OK", Toast.LENGTH_LONG).show();
                                    break;
                                case SmartAdAlert.BUTTON_CANCEL:
                                    Toast.makeText(MainActivity.this, "SmartAdAlert Select : Cancel", Toast.LENGTH_LONG).show();
                                    break;
                                case SmartAdAlert.BUTTON_BACK:
                                    Toast.makeText(MainActivity.this, "SmartAdAlert Select : Back", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
```
## 테스트 장비 추가
```java
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR);
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   "XXXXXE00ED1B543E38E01E0741305BC0");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXXf179a62345bb89544cd03ed16ba");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXX814b5bde5d5fb24a9c3e003ea06");
```

## 광고 호출 커스텀 함수 등록
SmartAd 의 모든 광고 모듈은 광고를 표시하기 전에 SmartAd.IsShowAdFunc 를 참조한다. IsShowAdFunc 는 기본적으로 null 이므로
모든 광고가 표기되는데 인앱 결제나 특정 상황에서 광고를 중단 시키기 위해서 이 함수를 다음과 같이 커스터마이징 하면 모든 광고 호출을
손쉽게 차단 할 수 있다.
```java
SmartAd.IsShowAdFunc = new SmartAd.IsShowAdListener() { // 광고 활성화 함수 적용
    @Override
    public Class[] getAvailClass() {
        // 적용할 클래스를 나열해 준다. 
        // 아래의 경우 SmartAdAward 를 제외한 모든 광고 클래스에 적용한 예다.
        return new Class[] {SmartAdBanner.class, SmartAdAlert.class, SmartAdInterstitial.class};
    }

    @Override
    public boolean isShowAd() {
        // 사용자의 상황에 맞게 내용을 커스터마이징 하면 된다.
        // 아래 내용은 하나의 예일 뿐이다.
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return mPrefs.getBoolean("isShowAd", true);
    }
};
```

## 태마 설정
```xml
<!-- SmartAdAlert Theme -->
<color name="ads_Alert_Background">#c8c8c8</color>
<color name="ads_Alert_Title_Background">#615aa0</color>
<color name="ads_Alert_Title_Text">#ffffff</color>
<color name="ads_Alert_Button_Background">#FFFFFF</color>
<color name="ads_Alert_Button_Text">#615aa0</color>
```

# License
```code
The MIT License

Copyright (c) 2009-2017 ShockUtility.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
