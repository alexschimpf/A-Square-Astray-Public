package com.tendersaucer.asquareastray.purchase;

import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManagerConfig;

public class PurchaseManager {

    private static final PurchaseManager instance = new PurchaseManager();

    private com.badlogic.gdx.pay.PurchaseManager purchaseManager;

    private PurchaseManager() {
    }

    public static PurchaseManager getInstance() {
        return instance;
    }

    public void init(com.badlogic.gdx.pay.PurchaseManager purchaseManager) {
        this.purchaseManager = purchaseManager;
    }

    public void purchase(String identifier) {
        purchaseManager.purchase(identifier);
    }

    public void restorePurchases() {
        purchaseManager.purchaseRestore();
    }

    public Information getInformation(String identifier) {
        return purchaseManager.getInformation(identifier);
    }

    public boolean isOfferAvailable(String identifier) {
        Information information = purchaseManager.getInformation(identifier);
        return information != null && !information.equals(Information.UNAVAILABLE);
    }

    public void install(PurchaseManagerConfig config) {
        purchaseManager.install(PurchaseObserver.getInstance(), config, true);
    }

    public boolean isInstalled() {
        return purchaseManager.installed();
    }

    public String getStoreName() {
        return purchaseManager.storeName();
    }

    public void dispose() {
        purchaseManager.dispose();
    }
}
