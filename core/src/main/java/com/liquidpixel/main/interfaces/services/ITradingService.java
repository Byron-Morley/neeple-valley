package com.liquidpixel.main.interfaces.services;

import com.liquidpixel.main.components.items.SettlementComponent;

public interface ITradingService {
    void registerItem(String itemName, float basePrice);

    float getCurrentPrice(String itemName);

    void recordTrade(String itemName, int quantity, float price);

    void updatePrice(String itemName);

    void addToGlobalSupply(String name, int harvestedAmount);

    boolean executeTrade(SettlementComponent buyer, SettlementComponent seller, String itemName, int quantity);

    float getTradePrice(String itemName, int quantity);

    float getDisplayPrice(String itemName);
}
