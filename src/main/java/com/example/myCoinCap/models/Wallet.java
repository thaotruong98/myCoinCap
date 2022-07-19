package com.example.myCoinCap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    public String id;  // unique userID

    private HashMap<String, BigDecimal> assets;     // List of assets

    public Wallet(String id){
        this.id = id;
        this.assets = new HashMap<>();
    }


    public void deleteAsset(String assetId) {
        this.assets.remove(assetId);
    }

    public void editAsset(String assetId, BigDecimal amount) {
        this.assets.put(assetId, amount);
    }
}
