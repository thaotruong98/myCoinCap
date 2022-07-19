package com.example.myCoinCap;

import com.example.myCoinCap.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.DataInput;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MyCoinCapService {
    private final RestTemplate restTemplate = new RestTemplate();
    private WalletRepository walletRepository;
    private MongoTemplate mongoTemplate;

    public MyCoinCapService(WalletRepository walletRepository, MongoTemplate mongoTemplate) {
        this.walletRepository = walletRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // Get list of all assets. Unless filtered
    public List<Asset> getAssets(MultiValueMap<String, String> filters) throws Exception {
        String uri = "https://api.coincap.io/v2/assets";

        // Check for filters or pagination, if exists, concat onto uri
        if (filters != null && !filters.isEmpty()) {
            uri = uri.concat("?");

            if (filters.get("search") != null) {
                uri = uri.concat("search=" + filters.get("search").get(0) + "&");
            }
            if (filters.get("limit") != null) {
                uri = uri.concat("limit=" + filters.get("limit").get(0) + "&");
            }
            if (filters.get("ids") != null) {
                uri = uri.concat("ids=" + filters.get("ids").get(0) + "&");
            }
            if (filters.get("offset") != null) {
                uri = uri.concat("offset=" + filters.get("offset").get(0));
            }

        }

        // Read Response from coincap
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();
        ListAssets assets = mapper.readValue(response.getBody(), ListAssets.class);     // Map response into a List of Assets
        return assets.getData();    // Return list of Assets
    }

    // Get singular asset from asset id
    public Asset getSingleAsset(String id) throws Exception {
        String uri = "https://api.coincap.io/v2/assets/" + id;

        // Read response from Coincap
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        // Because we only get one asset back, must parse past "data" object
        JSONObject obj = new JSONObject(response.getBody());
        JSONObject map = obj.getJSONObject("data");
        ObjectMapper mapper = new ObjectMapper();
        Asset asset = mapper.readValue(map.toString(), Asset.class);        // Map object as an Asset
        return asset;       // Return singular asset
    }


    // Create a new user wallet based on ID
    public String createWallet(Map<String, Object> obj) {
        if (obj != null) {
            String id = (String) obj.get("id");

            // If id already exists in mongo return already exists
            if (id != null && !id.isEmpty()) {

                if (walletRepository.existsById(id)) {
                    return "A Wallet with this ID already exists: " + id + ". Edit account with the edit endpoint";
                }

                // If wallet id doesn't exist yet, create one!
                Wallet wallet = new Wallet(id, (HashMap<String, BigDecimal>) obj.get("assets"));
                this.walletRepository.insert(wallet);       // Insert into Mongo
                return "Wallet created with ID: " + wallet.getId();
            }
            return "Id field was empty. Please provide an Id in request body";
        }
        return "Wallet or Id was null";
    }

    // User Edits assets in wallet
    public Wallet editWallet(Map<String, Object> obj) throws Exception {
        if (obj != null) {
            String id = (String) obj.get("id");

            if (id != null && !id.isEmpty()) {

                // If id doesnt exist in Mongo yet, throw exception and prompt user to create one
                if (walletRepository.existsById(id)) {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, BigDecimal> request = (HashMap<String, BigDecimal>) obj.get("assets");  // User's updated Asset list

                    Wallet currentWallet = this.walletRepository.findById(id).orElse(new Wallet());     // Fetch Wallet from Mongo

                    HashMap<String, BigDecimal> assets = currentWallet.getAssets();        // User's old Asset List

                    assets.putAll(request);     // Merge Asset lists, so that the updated ones populate in the old one

                    currentWallet.setAssets(assets);    // Update wallet with new merged Assets List
                    return this.walletRepository.save(currentWallet);   // Save wallet in Mongo
                }
                else {
                    throw new RuntimeException("Wallet Id doesn't exist. Please create one");
                }
            }
        }
        return null;
    }


    // Get User's current USD value of wallet
    public CurrentValueResponse getCurrentValue(String id) {
        if (id == null && id.isEmpty()){
            throw new RuntimeException("Did not provide id");
        }
        // If Id exists, otherwise throw exception and prompt to create one
        if (walletRepository.existsById(id)) {
            CurrentValueResponse response = new CurrentValueResponse(id);       // Response Object
            AtomicReference<Double> totalValue = new AtomicReference<>((double) 0);     // Total value tracker

            Wallet currentWallet = this.walletRepository.findById(id).orElse(new Wallet());    // Grab wallet from Mongo
            HashMap<String, BigDecimal> assets = currentWallet.getAssets();         // Grab Assets from wallet

            // Iterate through user's Assets, multiply how many assets owned with value of asset, and add to total count
            assets.forEach((k, v) -> {
                try {
                    Asset tempAsset = getSingleAsset(k);        // Asset in list

                    // Fetch current price
                    AssetValue tempValue = new AssetValue(k, v, new BigDecimal(tempAsset.getPriceUsd()));
                    response.addValues(tempValue);  // Add each asset value to response

                    // Multiply price by amount owned
                    BigDecimal mult = tempValue.getValue().multiply(v);
                    totalValue.getAndUpdate(value -> value + mult.doubleValue());  // Add to total counter
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            response.setTotal("$" + totalValue.get().toString() + " USD");      // Total Value
            return response;
        }
        throw new RuntimeException("Wallet Id doesn't exist. Please create one");

    }

    public HistoryResponse getHistory(String id, String time, int amount) throws RuntimeException{
        if (id == null && id.isEmpty()){
            throw new RuntimeException("Did not provide id");
        }
        // If Id exists, otherwise throw exception and prompt to create one
        if (walletRepository.existsById(id)) {
            HistoryResponse response = new HistoryResponse(id);     // Response Object
            AtomicReference<Double> currentSum = new AtomicReference<>((double) 0);     // Total Counter for current price
            AtomicReference<Double> historicSum = new AtomicReference<>((double) 0);    // Total Counter for historic price

            Wallet currentWallet = this.walletRepository.findById(id).orElse(new Wallet()); // Grad wallet from Mongo
            HashMap<String, BigDecimal> assets = currentWallet.getAssets();     // Grad asset list from Wallet

            // Iterate through user's assets
            assets.forEach((k, v) -> {
                try {
                    Asset tempAsset = getSingleAsset(k);        // each asset
                    BigDecimal price = new BigDecimal(tempAsset.getPriceUsd());     // Current USD value
                    currentSum.getAndUpdate(value -> value + (price.multiply(v)).doubleValue());    // Add muliplied value to total counter

                    PerAssetHistory pAH = getPerAssetHistory(k, time, amount);     // Historic value of asset
                    historicSum.getAndUpdate(value -> value + (pAH.getNetGainLoss().multiply(v)).doubleValue());    // Add muliplied value to total counter

                    BigDecimal netGainLoss = (price.multiply(v)).subtract(pAH.getNetGainLoss().multiply(v));  // Difference between current and historic value multiplied by amount owned

                    PerAssetHistory newPAH = new PerAssetHistory(k, netGainLoss, pAH.getSinceTime());  // New object to store net values per asset

                    response.addPerAssetHistory(newPAH);    // Add each asset's history to response

                } catch (Exception e) {
                    throw new RuntimeException();
                }

            });

            response.setTotalNetGainLoss(BigDecimal.valueOf(currentSum.get() - historicSum.get()));     // Set total overall net gain / loss
            response.setDuration("In the past " + amount + " " + time + "s.");
            return response;
        }
        throw new RuntimeException("Wallet Id doesn't exist. Please create one");
    }


    // Helper method for grabbing Historic value of an asset
    public PerAssetHistory getPerAssetHistory(String id, String time, int amount) throws JsonProcessingException {
        String uri = String.format("https://api.coincap.io/v2/assets/%s/history?interval=", id);

        // check time intervals
        if(time.equals("minute")) {
            uri = uri.concat("m1");
        }
        else if(time.equals("day")) {
            uri = uri.concat("d1");
        }
        else if(time.equals("hour")) {
            uri = uri.concat("h1");
        }
        else {
            throw new RuntimeException();
        }

        // Read in JSON response
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        PerAssetHistory pAH = new PerAssetHistory(id);

        // Map response into list of PriceInTime objects
        ObjectMapper mapper = new ObjectMapper();
        ListPriceInTime history = mapper.readValue(response.getBody(), ListPriceInTime.class);

        // Data is sent back with most recent history at the end of list, thus we grab (offset by amount) from end of list
        int index = history.getData().size() - amount;

        // Set fields in PerAssetHistory object and return
        String price = history.getData().get(index).getPriceUsd();
        pAH.setNetGainLoss(new BigDecimal(price));

        pAH.setSinceTime(history.getData().get(index).getTime());

        return pAH;
    }
}
