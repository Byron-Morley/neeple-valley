package com.liquidpixel.main.interfaces;

import com.liquidpixel.main.interfaces.services.ITradingService;

public interface IMarketLedger {

    void registerItem(String itemName, float basePrice);

    float getCurrentPrice(String itemName);

    void recordTrade(String itemName, int quantity, float price);

    void updatePrice(String itemName);

    void addToGlobalSupply(String name, int harvestedAmount);

    ITradingService getTradingService();

    float getTradePrice(String itemName, int quantity);
}
