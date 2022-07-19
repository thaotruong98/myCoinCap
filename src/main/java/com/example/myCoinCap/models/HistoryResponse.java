package com.example.myCoinCap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
    Model for our response when user looks up their wallet historical values
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    String id;
    BigDecimal totalNetGainLoss;
    String duration;
    List<PerAssetHistory> perAssetHistory;

    public HistoryResponse(String id){
        this.id = id;
        this.duration = "";
        totalNetGainLoss = BigDecimal.ZERO;
        perAssetHistory = new ArrayList<>();
    }

    public void addPerAssetHistory(PerAssetHistory pah) {
        this.perAssetHistory.add(pah);
    }
}
