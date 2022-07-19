package com.example.myCoinCap.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListPriceInTime {
    private List<PriceInTime> data;

    public ListPriceInTime() {
        data = new ArrayList<>();
    }
}
