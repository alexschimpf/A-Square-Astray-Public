package com.tendersaucer.asquareastray.ads;

public interface IAdManager {

    boolean showAd();

    void addAdListener(IAdListener listener);

    void removeAdListener(IAdListener listener);
}
