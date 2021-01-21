package com.tendersaucer.asquareastray.purchase;

import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;

public class Offers {

    public class SKU {
        public static final String REMOVE_ADS = "remove_ads";
    }

    public static final Offer REMOVE_ADS = new Offer()
            .setType(OfferType.ENTITLEMENT).setIdentifier(SKU.REMOVE_ADS);
}
