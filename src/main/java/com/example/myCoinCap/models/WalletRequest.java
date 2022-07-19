package com.example.myCoinCap.models;

import lombok.Data;

import java.util.HashMap;

@Data
public class WalletRequest {

    private int id;  // unique userID

    private HashMap<String, Float> assets;
}
