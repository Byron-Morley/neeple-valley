package com.liquidpixel.main.managers.market;

import com.badlogic.gdx.utils.TimeUtils;
import com.liquidpixel.main.factories.ModelFactory;
import com.liquidpixel.main.interfaces.IMarketLedger;
import com.liquidpixel.main.interfaces.services.ITradingService;
import com.liquidpixel.main.model.item.Item;
import com.liquidpixel.main.services.TradingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MarketLedger implements IMarketLedger {
    private static final float PRICE_HISTORY_INTERVAL = 300f; // 5 minutes
    private final Map<String, Float> basePrices = new HashMap<>();
    private final Map<String, List<TradeRecord>> tradeHistory = new HashMap<>();
    private final Map<String, Float> currentPrices = new HashMap<>();
    private final Map<String, Integer> globalSupply = new HashMap<>();
    ITradingService tradingService;

    public MarketLedger() {
        tradingService = new TradingService(this);
//        registerResources();
    }

    private void registerResources() {
        Map<String, Item> models = ModelFactory.getItemsModel();
        for (String itemName : models.keySet()) {
            Item item = models.get(itemName);
        }
    }

    public static class TradeRecord {
        public final float price;
        public final int quantity;
        public final float timestamp;

        public TradeRecord(float price, int quantity) {
            this.price = price;
            this.quantity = quantity;
            this.timestamp = TimeUtils.millis() / 1000f;
        }
    }

    public void registerItem(String itemName, float basePrice) {
        basePrices.put(itemName, basePrice);
        currentPrices.put(itemName, basePrice);
        tradeHistory.put(itemName, new ArrayList<>());
        globalSupply.put(itemName, 0);
    }

    public float getCurrentPrice(String itemName) {
        return currentPrices.getOrDefault(itemName, 0f);
    }

    public void recordTrade(String itemName, int quantity, float price) {
        tradeHistory.get(itemName).add(new TradeRecord(price, quantity));
        updatePrice(itemName);
    }

    public void addToGlobalSupply(String itemName, int amount) {
        globalSupply.merge(itemName, amount, Integer::sum);
        updatePrice(itemName);
    }

    public void removeFromGlobalSupply(String itemName, int amount) {
        globalSupply.merge(itemName, -amount, Integer::sum);
        updatePrice(itemName);
    }

    public int getGlobalSupply(String itemName) {
        return globalSupply.getOrDefault(itemName, 0);
    }

    public void updatePrice(String itemName) {
        float basePrice = basePrices.get(itemName);
        int totalSupply = globalSupply.get(itemName);
        List<TradeRecord> recentTrades = getRecentTrades(itemName);

        float supplyMultiplier = calculateSupplyEffect(totalSupply);
        float demandMultiplier = calculateDemandEffect(recentTrades);

        float newPrice = basePrice * supplyMultiplier * demandMultiplier;
        newPrice = Math.max(basePrice * 0.5f, Math.min(basePrice * 2.0f, newPrice));

        currentPrices.put(itemName, newPrice);
    }

    public float getTradePrice(String itemName, int quantity) {
        float baseTradePrice = getCurrentPrice(itemName);
        float volumeImpact = 1.0f + (Math.abs(quantity) / 1000f); // Large orders impact price more
        float tradingFee = 0.01f; // 1% trading fee

        float finalPrice = baseTradePrice * volumeImpact * (1 + tradingFee);
        return Math.max(basePrices.get(itemName) * 0.5f, Math.min(basePrices.get(itemName) * 2.0f, finalPrice));
    }


    private List<TradeRecord> getRecentTrades(String itemName) {
        float currentTime = TimeUtils.millis() / 1000f;
        return tradeHistory.get(itemName).stream()
            .filter(record -> currentTime - record.timestamp < PRICE_HISTORY_INTERVAL)
            .collect(Collectors.toList());
    }

    private float calculateSupplyEffect(int totalSupply) {
        return Math.max(0.5f, 2.0f - (totalSupply / 1000f));
    }

    private float calculateDemandEffect(List<TradeRecord> records) {
        return Math.min(2.0f, 1.0f + (records.size() / 10f));
    }

    public ITradingService getTradingService() {
        return tradingService;
    }
}
