package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.pay.ios.apple.PurchaseManageriOSApple;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.ads.IAdListener;
import com.tendersaucer.asquareastray.ads.IAdManager;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.pods.google.mobileads.GADInterstitial;
import org.robovm.pods.google.mobileads.GADInterstitialDelegateAdapter;
import org.robovm.pods.google.mobileads.GADMobileAds;
import org.robovm.pods.google.mobileads.GADRequest;
import org.robovm.pods.google.mobileads.GADRequestError;


public class IOSLauncher extends IOSApplication.Delegate implements IAdManager {

    private static final String PROD_INTERSTITIAL_AD_UNIT_ID = "";

    private IOSApplication app;
    private Array<IAdListener> adListeners;
    private GADInterstitial interstitialAd;

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    protected IOSApplication createApplication() {
        adListeners = new Array<>();

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        config.statusBarVisible = false;

        if (Globals.ENABLE_ADS) {
            GADMobileAds.disableSDKCrashReporting();
            loadInterstitialAd();
        }

        PurchaseManageriOSApple purchaseManager = new PurchaseManageriOSApple();
        PurchaseManager.getInstance().init(purchaseManager);

        app = new IOSApplication(new Game(this), config);
        return app;
    }

    @Override
    public boolean showAd() {
        Gdx.app.log(Globals.LOG_TAG, "Attempting to show ad...");
        if (interstitialAd.isReady()) {
            Gdx.app.log(Globals.LOG_TAG, "Ad is ready...");

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    for (IAdListener listener : adListeners) {
                        listener.onAdOpened();
                    }
                }
            });

            Gdx.app.log(Globals.LOG_TAG, "Showing ad...");
            UIViewController viewController = app.getUIViewController();
            interstitialAd.present(viewController);
            return true;
        } else {
            Gdx.app.log(Globals.LOG_TAG, "Ad is not ready. Loading ad request...");
            loadInterstitialAd();
            return false;
        }
    }

    @Override
    public void addAdListener(IAdListener listener) {
        if (!adListeners.contains(listener, true)) {
            adListeners.add(listener);
        }
    }

    @Override
    public void removeAdListener(IAdListener listener) {
        adListeners.removeValue(listener, true);
    }

    private GADRequest getAdRequest() {
        GADRequest request = new GADRequest();
        //request.setTestDevices(Arrays.asList(GADRequest.getSimulatorID(), "4a4b12793536f8344dfac2409ec166c0"));
        return request;
    }

    private void loadInterstitialAd() {
        if (Gdx.app != null) {
            Gdx.app.log(Globals.LOG_TAG, "Loading interstitial ad...");
        }

        interstitialAd = new GADInterstitial(PROD_INTERSTITIAL_AD_UNIT_ID);
        interstitialAd.loadRequest(getAdRequest());
        interstitialAd.setDelegate(new GADInterstitialDelegateAdapter() {
            @Override
            public void didFailToReceiveAd(GADInterstitial ad, GADRequestError error) {
                super.didFailToReceiveAd(ad, error);
                Gdx.app.log(Globals.LOG_TAG, "Failed to receive ad: " + error.toString());
            }

            @Override
            public void didFailToPresentScreen(GADInterstitial ad) {
                super.didFailToPresentScreen(ad);
                Gdx.app.log(Globals.LOG_TAG, "Failed to present ad to screen...");
                onAdClosed();
            }

            @Override
            public void didDismissScreen(GADInterstitial ad) {
                super.didDismissScreen(ad);
                Gdx.app.log(Globals.LOG_TAG, "Ad dismissed...");
                loadInterstitialAd();
                onAdClosed();
            }
        });
    }

    private void onAdClosed() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for (IAdListener listener : adListeners) {
                    listener.onAdClosed();
                }
            }
        });
    }
}