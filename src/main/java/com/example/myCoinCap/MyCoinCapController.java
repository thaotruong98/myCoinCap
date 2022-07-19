package com.example.myCoinCap;

import com.example.myCoinCap.models.Asset;
import com.example.myCoinCap.models.CurrentValueResponse;
import com.example.myCoinCap.models.HistoryResponse;
import com.example.myCoinCap.models.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("myCoinCap/v1/")
@AllArgsConstructor
public class MyCoinCapController {

    private final MyCoinCapService coinService;

    // Get full list of assets
    @GetMapping("/assets")
    public List<Asset> retrieveAssets(@RequestParam(required = false) MultiValueMap<String, String> filters) throws Exception {

        return coinService.getAssets(filters);
    }


    // Get single asset by id
    @GetMapping("/assets/{id}")
    public Asset retrieveAssets(@PathVariable(required = false) String id) throws Exception {
        return coinService.getSingleAsset(id);
    }


    // User create new wallet by passing in id and assets
    @PostMapping(value = "/wallet/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createWallet(@RequestBody Map<String, Object> obj) {
        return coinService.createWallet(obj);
    }


    //User edit wallet assets
    @PutMapping("/wallet/edit")
    public Wallet editWallet(@RequestBody Map<String, Object> obj) throws Exception {
        return coinService.editWallet(obj);
    }


    // Get current value of assets
    @GetMapping("/wallet/value/{id}")
    public CurrentValueResponse getCurrentValue(@PathVariable String id) throws Exception {

        try {
            return coinService.getCurrentValue(id);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID not found");
        }
    }


    // Get net historic gain/loss
    @GetMapping("/wallet/history/{id}/{time}/{amount}")
    public HistoryResponse getHistoricData(@PathVariable String id, @PathVariable String time, @PathVariable int amount) throws Exception {
        try {
            return coinService.getHistory(id, time, amount);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time param. Options are: month, day, hour");
        }
    }


}
