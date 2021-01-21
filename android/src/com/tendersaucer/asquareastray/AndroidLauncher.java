package com.tendersaucer.asquareastray;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.pay.android.googlebilling.PurchaseManagerGoogleBilling;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tendersaucer.asquareastray.ads.IAdListener;
import com.tendersaucer.asquareastray.ads.IAdManager;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;

public class AndroidLauncher extends AndroidApplication implements IAdManager {

	private static final String INTERSTITIAL_AD_UNIT_ID = "";

	private InterstitialAd interstitialAd;
	private Array<IAdListener> adListeners;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adListeners = new Array<>();

		if (Globals.ENABLE_ADS) {
			MobileAds.initialize(this, new OnInitializationCompleteListener() {
				@Override
				public void onInitializationComplete(InitializationStatus initializationStatus) {
					Gdx.app.log(Globals.LOG_TAG, "Done initializing mobile ads");
				}
			});

			interstitialAd = new InterstitialAd(this);
			interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
			interstitialAd.loadAd(new AdRequest.Builder().build());
			interstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdOpened() {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							for (IAdListener listener : adListeners) {
								listener.onAdOpened();
							}
						}
					});
				}

				@Override
				public void onAdClosed() {
					handleAdClosedOrFailed();
				}

				@Override
				public void onAdFailedToLoad(LoadAdError error) {
					Gdx.app.error(Globals.LOG_TAG, "Failed to load ad: " + error.toString());

					handleAdClosedOrFailed();
				}
			});
		}

		PurchaseManagerGoogleBilling purchaseManager = new PurchaseManagerGoogleBilling(this);
		PurchaseManager.getInstance().init(purchaseManager);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		config.hideStatusBar = true;
		config.useImmersiveMode = true;
		View gameView = initializeForView(new Game(this), config);

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView);

		setContentView(layout);
	}

	@Override
	public boolean showAd() {
		tryReloadAd();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
					}
				} catch(Exception e) {
					Gdx.app.error(Globals.LOG_TAG, "Failed to show ad", e);
					handleAdClosedOrFailed();
				}
			}
		});

		return true;
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

	private void handleAdClosedOrFailed() {
		Gdx.app.log(Globals.LOG_TAG, "Handling ad closed or failed...");

		tryReloadAd();

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				for (IAdListener listener : adListeners) {
					listener.onAdClosed();
				}
			}
		});
	}

	private void tryReloadAd() {
		Gdx.app.log(Globals.LOG_TAG, "Trying to reload ad...");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (!interstitialAd.isLoaded() && !interstitialAd.isLoading()) {
						interstitialAd.loadAd(new AdRequest.Builder().build());
					}
				} catch (Exception e) {
					Gdx.app.error(Globals.LOG_TAG, "Failed to reload ad", e);
				}
			}
		});
	}
}
