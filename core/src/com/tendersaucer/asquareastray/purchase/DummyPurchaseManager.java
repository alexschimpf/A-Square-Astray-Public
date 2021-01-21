package com.tendersaucer.asquareastray.purchase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.tendersaucer.asquareastray.Globals;

public class DummyPurchaseManager implements PurchaseManager {

    private PurchaseObserver observer;

    @Override
    public String storeName() {
        return null;
    }

    @Override
    public void install(PurchaseObserver observer, PurchaseManagerConfig config, boolean autoFetchInformation) {
        Gdx.app.log(Globals.LOG_TAG, "Installing purchase manager");
        this.observer = observer;
    }

    @Override
    public boolean installed() {
        return true;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void purchase(String identifier) {
        Gdx.app.log(Globals.LOG_TAG, "Purchasing: " + identifier + "...");

        Transaction transaction = new Transaction();
        transaction.setIdentifier(identifier);
        observer.handlePurchase(transaction);
    }

    @Override
    public void purchaseRestore() {
        Gdx.app.log(Globals.LOG_TAG, "Restoring purchase...");

        Transaction transaction = new Transaction();
        transaction.setIdentifier(Offers.SKU.REMOVE_ADS);
        Transaction[] transactions = new Transaction[] { transaction };
        observer.handleRestore(transactions);
    }

    @Override
    public Information getInformation(String identifier) {
        if (identifier.equals(Offers.SKU.REMOVE_ADS)) {
            return Information.newBuilder().priceAsDouble(1.0).localName("test").build();
        }
        return null;
    }
}
