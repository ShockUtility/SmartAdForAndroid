[![License](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/MIT.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/Android.png?raw=true)](https://developer.android.com)
[![Android Arsenal](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/Arsenal.png?raw=true)](https://android-arsenal.com/details/1/6387)
[![GitHub release](https://img.shields.io/github/release/ShockUtility/SmartAdForAndroid.svg)](https://github.com/ShockUtility/SmartAdForAndroid)

[![English](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/en.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid)
[![Korea](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/kr.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_kr.md)
[![Japan](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/jp.png?raw=true)](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/README_jp.md)

# SmartAd
SmartAd is an easy-to-use library for AdMob and Audience Network advertising frameworks on iOS and Android.

- [SmartAd for Android](https://github.com/ShockUtility/SmartAdForAndroid) -> [Demo Project](https://github.com/ShockUtility/SmartAdDemoForAndroid)
- [SmartAd for Swift](https://github.com/ShockUtility/SmartAdForSwift)
![Screenshot](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/screen_00.png?raw=true)

# Install
```java
dependencies {
    compile 'kr.docs:smart-ad:0.3.3'
} 
```

# Dependencies
```java
dependencies {
    compile 'com.google.android.gms:play-services-ads:11.+'
    compile 'com.facebook.android:audience-network-sdk:4.+'
} 
```

# Supported ad formats
## Google AdMob
- AdView
- InterstitialAd
- RewardedVideoAd

## Facebook Audience Network
- AdView
- InterstitialAd
- RewardedVideoAd

# Usage

## SmartAdBanner
![Screenshot](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/screen_01.png?raw=true)
Add the view to the UI screen, select the 'SmartAdBanner' class and set only 5 properties, and it works without coding.

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

| SmartAdBanner       | Google (AdView)       | Facebook (AdView)     |
|---------------------|-----------------------|-----------------------|
| AD_SIZE_AUTO        | SMART_BANNER          | BANNER_HEIGHT_50      |
| AD_SIZE_SMALL       | BANNER                | BANNER_HEIGHT_50      |
| AD_SIZE_LARGE       | LARGE_BANNER          | BANNER_HEIGHT_90      |
| AD_SIZE_RECTANGLE   | MEDIUM_RECTANGLE      | RECTANGLE_HEIGHT_250  |

`* Note: If four sizes of ads are supported and the size of the view is smaller than the size of the ads to be displayed, then each framework may not show ads.`
<br>
`* Note: preferably layout_height of the view is set to wrap_content and adv_AdSize is set appropriately, the ad size will be displayed for each framework, and if there is no advertisement, it will not be displayed on the screen.`
<br>

## SmartAdInterstitial
Here is the example code that calls the interstitial.
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
    if (mAd!=null) mAd.destroy();  // This prevents ads from being displayed after the screen is closed.
    super.onDestroy();
}
```
Use OnSmartAdInterstitialListener if you want to return the result.
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
 
The following functions allow you to make ad calls.
```java
static public SmartAdInterstitial showAdWidthCallback(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAdWidthCallback(Context context, String googleID, String facebookID, final OnSmartAdInterstitialListener callback)
static public SmartAdInterstitial showAd(Context context, int adOrder, String googleID, String facebookID, boolean isAutoStart)
static public SmartAdInterstitial showAd(Context context, String googleID, String facebookID)

public SmartAdInterstitial(Context context, @SmartAd.SmartAdOrder int adOrder, String googleID, String facebookID, boolean isAutoStart, final OnSmartAdInterstitialListener callback)
public void showLoadedAd()
public void destroy()
```

`* Note: If you do not handle destroy (), the problem is that your ads will still be called after the calling activity has been closed.`

## SmartAdAward
Here is the example code that calls the award ad.
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

Use OnSmartAdAwardListener if you want to return the result.
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

The following functions allow you to make ad calls.
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
![Screenshot](https://github.com/ShockUtility/SmartAdForAndroid/blob/master/res/screen_02.png?raw=true)
Alert with OK button only.
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
Alert with OK & Cancel.
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
Customizing Alert
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
## Add test device.
```java
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR);
SmartAd.addTestDevice(SmartAd.AD_TYPE_GOOGLE,   "XXXXXE00ED1B543E38E01E0741305BC0");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXXf179a62345bb89544cd03ed16ba");
SmartAd.addTestDevice(SmartAd.AD_TYPE_FACEBOOK, "XXXXX814b5bde5d5fb24a9c3e003ea06");
```

## Register the ad activation function
You can register and use this function to stop ads in-app billing or under certain circumstances.
```java
SmartAd.IsShowAdFunc = new SmartAd.IsShowAdListener() {
    @Override
    public Class[] getAvailClass() {
        // List the classes to be applied.
        // Here's an example that applies to all ad classes except SmartAdAward.
        return new Class[] {SmartAdBanner.class, SmartAdAlert.class, SmartAdInterstitial.class};
    }

    @Override
    public boolean isShowAd() {
        // You can customize the content to suit your situation.
        // The following is just an example.
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        return mPrefs.getBoolean("isShowAd", true);
    }
};
```

## Use Alert Theme
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
