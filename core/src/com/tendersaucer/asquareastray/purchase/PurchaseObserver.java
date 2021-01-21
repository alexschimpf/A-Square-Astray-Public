package com.tendersaucer.asquareastray.purchase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Transaction;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.screen.MainMenuScreen;

public class PurchaseObserver implements com.badlogic.gdx.pay.PurchaseObserver {

    private static final PurchaseObserver instance = new PurchaseObserver();

    private PurchaseObserver() {
    }

    public static PurchaseObserver getInstance() {
        return instance;
    }

    @Override
    public void handleInstall() {
    }

    @Override
    public void handleInstallError(Throwable e) {
        Gdx.app.log(Globals.LOG_TAG, "Error installing purchase manager: " + e);
    }

    @Override
    public void handleRestore(Transaction[] transactions) {
        for (Transaction transaction : transactions) {
            handlePurchase(transaction);
        }
        Persistence.getInstance().putBoolean("purchases_restored", true);
        EventManager.getInstance().notify(new ScreenChangeEvent(new MainMenuScreen()));
    }

    @Override
    public void handleRestoreError(Throwable e) {
        Gdx.app.log(Globals.LOG_TAG, "Error restoring purchases: " + e);
    }

    @Override
    public void handlePurchase(Transaction transaction) {
        if (transaction.isPurchased()) {
            String sku = transaction.getIdentifier();
            if (sku.equals(Offers.SKU.REMOVE_ADS)) {
                Persistence.getInstance().putBoolean("purchased_remove_ads", true);
                EventManager.getInstance().notify(new ScreenChangeEvent(new MainMenuScreen()));
            }
        }
    }

    @Override
    public void handlePurchaseError(Throwable e) {
        Gdx.app.log(Globals.LOG_TAG, "Error handling purchase: " + e);
    }

    @Override
    public void handlePurchaseCanceled() {
        Gdx.app.log(Globals.LOG_TAG, "Purchase was cancelled");
    }
}
