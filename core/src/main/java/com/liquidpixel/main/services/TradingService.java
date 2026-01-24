package com.liquidpixel.main.services;

import com.liquidpixel.main.components.items.SettlementComponent;
import com.liquidpixel.main.interfaces.IMarketLedger;
import com.liquidpixel.main.interfaces.services.ITradingService;

public class TradingService extends Service implements ITradingService {

    IMarketLedger marketLedger;

    public TradingService(IMarketLedger marketLedger) {
        this.marketLedger = marketLedger;
    }

    @Override
    public void registerItem(String itemName, float basePrice) {
        marketLedger.registerItem(itemName, basePrice);
    }

    @Override
    public float getCurrentPrice(String itemName) {
        return marketLedger.getCurrentPrice(itemName);
    }

    @Override
    public void recordTrade(String itemName, int quantity, float price) {
        marketLedger.recordTrade(itemName, quantity, price);
    }

    @Override
    public void updatePrice(String itemName) {
        marketLedger.updatePrice(itemName);
    }

    @Override
    public void addToGlobalSupply(String name, int harvestedAmount) {
        marketLedger.addToGlobalSupply(name, harvestedAmount);
    }

    public float getTradePrice(String itemName, int quantity) {
        return marketLedger.getTradePrice(itemName, quantity);
    }

    public boolean executeTrade(SettlementComponent buyer, SettlementComponent seller, String itemName, int quantity) {
        return true;
    }

    public float getDisplayPrice(String itemName) {
        return 0;
    }

}
