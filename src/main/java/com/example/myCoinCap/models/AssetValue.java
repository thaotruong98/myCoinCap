package com.example.myCoinCap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetValue {
    String assetName;
    BigDecimal amount;  // how many units owned
    BigDecimal value;   // USD price
}
