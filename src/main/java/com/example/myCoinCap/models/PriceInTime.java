package com.example.myCoinCap.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;


/*
    Model for Reading in Historical Prices from CoinCap
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInTime {
    String priceUsd;
    BigDecimal time;
}
