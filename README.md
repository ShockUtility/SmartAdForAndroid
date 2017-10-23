[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![GitHub release](https://img.shields.io/github/release/ShockUtility/SmartAdForAndroid.svg)](https://github.com/ShockUtility/SmartAdForAndroid)

# SmartAd 소개
SmartAd 는 iOS 와 Android 에서 AdMob 과 Audience Network 광고 프레임웍을 간편하게 사용하기 위한 라이브러리 입니다.

- [SmartAd for Swift](https://github.com/ShockUtility/SmartAdForSwift)
- [SmartAd for Android](https://github.com/ShockUtility/SmartAdForAndroid)

# 설치
```java
dependencies {
    compile 'kr.docs:smart-ad:0.0.1'
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
## AdMob
- AdView (기본 베너)
- InterstitialAd (전면 광고)
- RewardedVideoAd (보상 광고)

## Audience Network
- AdView (기본 베너)
- InterstitialAd (삽입 광고)
- RewardedVideoAd (보상 광고) *** 지원 예정 ***

# 사용법

## 기본 베너 (SmartAdBanner)
![Screenshot](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/screenshot/screen_01.png?raw=true)<br>
UI 화면에 뷰를 추가하고 'SmartAdBanner' 클래스를 선택한 후 5개의 프로퍼티만 셋팅하면 코딩 없이 바로 동작 됩니다.

| SmartAdBanner     | Google (NativeExpressAdView)             | Facebook (NativeAd)       |
|-----------------|------------------------------------------|---------------------------|
| AD_SIZE_AUTO              | SMART_BANNER  (280&#126;1200 x 80&#126;612)     | auto width x BANNER_HEIGHT_50   |
| AD_SIZE_SMALL            | BANNER  (280&#126;1200 x 132&#126;1200)   | auto width x BANNER_HEIGHT_50   |
| AD_SIZE_LARGE            | LARGE_BANNER    (280&#126;1200 x 250&#126;1200)   | auto width x BANNER_HEIGHT_90   |
| AD_SIZE_RECTANGLE   | MEDIUM_RECTANGLE    (280&#126;1200 x 250&#126;1200)   | auto width x RECTANGLE_HEIGHT_250   |

`* 주의 : 4가지 크기의 광고가 지원되며 뷰의 크기가 광고보다 작은 경우 각각의 프레임웍에 의해서 광고가 표시되지 않을 수 있다.`
<br>
`* 주의 : 가급적이면 뷰의 layout_height 는 wrap_content 로 설정하고 adv_AdSize 를 알맞게 설정 한다면 각각의 프레임웍에 맞는 광고 사이즈로 표기되며 광고가 없을 경우 화면에 표기되지 않을 것입니다.`
<br>
`* 주의 : 광고의 넓이가 화면보다 작은 경우 구글 광고가 표시되지 않는 문제가 발생합니다. 이 경우 adv_FixedWidth 프로퍼티에 광고의 넓이를 설정하면 문제를 해결 할 수 있습니다.`

## 전면 광고 (SmartAdInterstitial)
Activity 에서 호출하는 가장 간단한 코드는 다음과 같다.
```java
SmartAdInterstitial mAd = SmartAdInterstitial.showAd(this, "googleID", "facebookID");
...
@Override
public void onDestroy() {
    if (mAd!=null) mAd.destroy(); 
    super.onDestroy();
}
```
상황에 따라 결과값을 반환 받고 싶을 경우 호출하는 클래스에 OnSmartAdInterstitialListener 를 implements 한다.
```java
public class MainActivity extends AppCompatActivity implements SmartAdInterstitial.OnSmartAdInterstitialListener {
    @Override
    public void OnSmartAdInterstitialDone(int type) {
        // Success...
    }

    @Override
    public void OnSmartAdInterstitialFail(String lastError) {
        // Fail...
    }
}
```
 
다음의 함수들을 통해서 상황에 맞는 광고 호출이 가능합니다.
```java
// 초기화 이후 원하는 시점에 호출 해야 하는 경우
public SmartAdInterstitial(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle)
public void showAd()

// 클래스에서 호출하고 콜백이 필요한 경우
static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle)
static public SmartAdInterstitial showAdWidthCallback(Context context, Object callback, String googleID, String facebookID)

// Activity 에서 호출하는 경우
static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID, boolean isFirstGoogle)
static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID)
```

`* 주의 : destroy() 처리를 안하는 경우 호출한 Activity 가 닫힌 후에도 광고가 호출되는 문제가 발생한다.`

## 보상 광고 (SmartAdAward)
Activity 에서 호출하는 가장 간단한 코드는 다음과 같다.
```java
SmartAdAward.showAd(this, "googleID", "facebookID");
```

보상광고의 결과값을 얻기 위해서 다음과 같은 루틴이 필요하다.
```java
public class MainActivity extends AppCompatActivity implements SmartAdInterstitial.OnSmartAdAwardListener {
    @Override
    public void OnSmartAdAwardDone(int type, boolean isAward) {
        // Awarded
    }

    @Override
    public void OnSmartAdAwardFail(String lastError) {
        // Not Awarded
    }
}
```

다음의 함수들을 통해서 상황에 맞는 광고 호출이 가능합니다.
```java
// 초기화 이후 원하는 시점에 호출 해야 하는 경우
public SmartAdAward(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle);
public void showAd();

// 클래스에서 호출하고 콜백이 필요한 경우
static public void showAdWidthCallback(Context context, Object callback, String googleID, String facebookID, boolean isFirstGoogle);
static public void showAdWidthCallback(Context context, Object callback, String googleID, String facebookID);

// Activity 에서 호출하는 경우
static public void showAd(Context context, String googleID, String facebookID, boolean isFirstGoogle);
static public void showAd(Context context, String googleID, String facebookID);
```

`* 주의 : 아직 Audience Network 의 보상 광고는 준비되지 않았다.`

## 얼럿 광고 (SmartAdAlert)
확인 버튼만 있는 알림 얼럿
```java
SmartAdAlert.alert(this,
        "googleID",
        "facebookID"
        "alert title",
        new SmartAdAlert.SmartAdAlertListener() {
            @Override
            public void result(int buttonIndex) {
                if (buttonIndex==SmartAdAlert.BUTTON_1) {
                    // Done
                }
            }
        });
```
확인/취소 얼럿
```java
SmartAdAlert.confirm(this,
        "googleID",
        "facebookID"
        "alert title",
        new SmartAdAlert.SmartAdAlertListener() {
            @Override
            public void result(int buttonIndex) {
                if (buttonIndex==SmartAdAlert.BUTTON_1) {
                    // OK
                } else {
                    // Cancel
                }
            }
        });
```
선택 버튼 커스터마이징 얼럿
```java
SmartAdAlert.select(this,
        "googleID",
        "facebookID",
        "alert title",
        "button title 1", 
        "button title 2", 
        new SmartAdAlert.SmartAdAlertListener() {
            @Override
            public void result(int buttonIndex) {
                if (buttonIndex==SmartAdAlert.BUTTON_1) {
                    // button1
                } else {
                    // button2
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
SmartAd 의 모든 광고 모듈을 광고를 표시하기 전에 SmartAd.IsShowAdFunc 를 참조한다. IsShowAdFunc 는 기본적으로 null 이므로
모든 광고가 표기되는데 인앱 결제나 특정 상황에서 광고를 중단 시키기 위해서 이 함수를 다음과 같이 커스터마이징 하면 광고 호출을 손쉽게 제어 할 수 있다.
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
