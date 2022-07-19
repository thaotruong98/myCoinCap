package com.example.myCoinCap.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset {
    private String id;  // unique productId
    private String name;  // Name of product
    private String symbol;
    private String rank;
    private String supply;
    private String maxSupply;
    private String marketCapUsd;
    private String volumeUsd24Hr;
    private String priceUsd;    // current value in USD
    private String changePercent24Hr;
    private String vwap24Hr;
    private String explorer;

}
