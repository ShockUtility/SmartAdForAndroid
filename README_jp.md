[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![GitHub release](https://img.shields.io/github/release/ShockUtility/SmartAdForAndroid.svg)](https://github.com/ShockUtility/SmartAdForAndroid)
[![English](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/en.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid)
[![Korea](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/kr.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_kr.md)
[![Japan](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/jp.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_jp.md)

# SmartAd紹介
SmartAdはiOSとAndroidでAdMobとAudience Networkの広告フレームワークを簡単に使用するためのライブラリです。

- [SmartAd for Swift](https://github.com/ShockUtility/SmartAdForSwift)
- [SmartAd for Android](https://github.com/ShockUtility/SmartAdForAndroid) -> [Demo Project](https://github.com/ShockUtility/SmartAdDemoForAndroid)

# インストール
```java
dependencies {
    compile 'kr.docs:smart-ad:0.2.2'
} 
```

# 依存性
```java
dependencies {
    compile 'com.google.android.gms:play-services-ads:11.+'
    compile 'com.facebook.android:audience-network-sdk:4.+'
} 
```

# サポートされている広告フォーマット
## Google AdMob
- AdView 
- InterstitialAd
- RewardedVideoAd

## Facebook Audience Network
- AdView 
- InterstitialAd
- RewardedVideoAd

# 使い方

## SmartAdBanner
UI画面にビューを追加し、「SmartAdBanner」クラスを選択した後、5つのプロパティだけ設定すれば、コーディングなしですぐに動作します。

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

`* 注意：4つのサイズの広告がサポートされ、ビューのサイズが表示される広告よりも小さい場合、それぞれのフレームワークによって、広告が表示されないことがあります。`
<br>
`* 注意：できればビューのlayout_heightはwrap_contentに設定しadv_AdSizeを適切に設定すれば、それぞれのフレームワークに合った広告サイズで表記され、広告がない場合は、画面に表記されていないことです。`
<br>

## SmartAdInterstitial
前面広告呼び出すサンプルコードは、以下の通りである。
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
    if (mAd!=null) mAd.destroy();  // この部分がない場合は、呼び出した画面が閉じた後、他の画面では、広告が表示されることができる。
    super.onDestroy();
}
```
状況に応じて、結果の値を返す受けたい場合、呼び出しクラスにOnSmartAdInterstitialListenerをimplementsする。
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
 
次の関数を介して、状況に応じ広告呼び出しが可能です。
```java
static public SmartAdInterstitial showAdWidthCallback(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAdWidthCallback(Context context, String googleID, String facebookID, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAd(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart)
static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID)

public SmartAdInterstitial(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
public void showLoadedAd()
public void destroy()
```

`* 注意：destroy（）の処理をしない場合、呼び出したActivityが閉じた後も広告が呼び出される問題が発生する。`

## SmartAdAward
補償広告呼び出すサンプルコードは、以下の通りである。
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

補償広告の結果を得るためにOnSmartAdAwardListenerをimplementsする。
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

次の関数を介して、状況に応じ広告呼び出しが可能です。
```java
static public SmartAdAward showAdWidthCallback(Context context, int adOrder, String googleID, String facebookID, final OnSmartAdAwardListener callback)
static public SmartAdAward showAdWidthCallback(Context context, String googleID, String facebookID, final OnSmartAdAwardListener callback)
static public SmartAdAward showAd(Context context, int adOrder, String googleID, String facebookID)
static public SmartAdAward showAd(Context context, String googleID, String facebookID)

public SmartAdAward(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID, final OnSmartAdAwardListener callback)
public SmartAdAward(Context context, String googleID, String facebookID, OnSmartAdAwardListener callback)
public void showAd()
```

## SmartAdAlert
OKボタンだけあるアラート
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
確認/キャンセルアラート
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
カスタムアラート
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
## 試験装置を追加
```java
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR);
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   "XXXXXE00ED1B543E38E01E0741305BC0");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXXf179a62345bb89544cd03ed16ba");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXX814b5bde5d5fb24a9c3e003ea06");
```

## 広告呼び出しカスタム関数の登録
SmartAdのすべての広告モジュールは、広告を表示する前にSmartAd.IsShowAdFuncを参照する。IsShowAdFuncは基本的にnullであるため、
すべての広告が表記されアプリ内課金や特定の状況では、広告を停止しさせるために、この関数を次のようにカスタマイズすると、すべての広告の呼び出しを
簡単にブロックすることができる。
```java
SmartAd.IsShowAdFunc = new SmartAd.IsShowAdListener() { 
    @Override
    public Class[] getAvailClass() {
        //適用するクラスを一覧表示してくれる。
        //以下の場合SmartAdAwardを除くすべての広告のクラスに適用した例である。
        return new Class[] {SmartAdBanner.class, SmartAdAlert.class, SmartAdInterstitial.class};
    }

    @Override
    public boolean isShowAd() {
        //ユーザーの状況に合わせて内容をカスタマイズすればよい。
        //以下の内容は、一つの例にすぎ。
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return mPrefs.getBoolean("isShowAd", true);
    }
};
```

## テマ設定
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
