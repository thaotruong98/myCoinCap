package com.example.myCoinCap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentValueResponse {
    String id;
    String total;
    List<AssetValue> values;

    public CurrentValueResponse(String id){
        this.id = id;
        total = "";
        values = new ArrayList<>();
    }

    public void addValues(AssetValue value) {
        this.values.add(value);
    }
}
