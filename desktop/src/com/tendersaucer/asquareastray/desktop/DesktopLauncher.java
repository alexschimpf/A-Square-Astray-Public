package com.tendersaucer.asquareastray.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tendersaucer.asquareastray.Game;
import com.tendersaucer.asquareastray.ads.DummyAdManager;
import com.tendersaucer.asquareastray.ads.IAdManager;
import com.tendersaucer.asquareastray.purchase.DummyPurchaseManager;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		config.resizable = false;

		config.width = 1200;
		config.height = 600;
		//config.width = 1024;
		//config.height = 768;

		DummyPurchaseManager purchaseManager = new DummyPurchaseManager();
		PurchaseManager.getInstance().init(purchaseManager);

		IAdManager adManager = new DummyAdManager();
		new LwjglApplication(new Game(adManager), config);
	}
}
