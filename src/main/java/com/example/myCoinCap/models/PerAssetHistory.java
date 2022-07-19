package com.example.myCoinCap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerAssetHistory {
    String assetName;       // Name of asset  ex. Bitcoin
    BigDecimal netGainLoss;  // net gained/loss
    BigDecimal sinceTime;   // since UNIX time

    public PerAssetHistory(String name){
        this.assetName = name;
        this.netGainLoss = BigDecimal.ZERO;
        this.sinceTime = BigDecimal.ZERO;
    }
}
