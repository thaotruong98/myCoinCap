package com.example.myCoinCap;

import com.example.myCoinCap.models.Asset;
import com.example.myCoinCap.models.PerAssetHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class TestMyCoinCapService {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private MyCoinCapService coinCapService;

    @BeforeEach
    void setUp() {
        coinCapService = new MyCoinCapService(walletRepository, mongoTemplate);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerAssetHistory() throws Exception {

        PerAssetHistory response = coinCapService.getPerAssetHistory("bitcoin", "day", 2);
        assertNotNull(response);
        assertEquals(response.getAssetName(), "bitcoin");
        assertNotNull(response.getNetGainLoss());
        assertNotNull(response.getSinceTime());
    }

    @Test
    void testCreate() throws Exception {

        HashMap<String, BigDecimal> assets = new HashMap<>();
        assets.put("bitcoin", BigDecimal.ONE);

        Map<String, Object> obj = new HashMap<>();
        obj.put("id", "9748957239874987529");
        obj.put("assets", assets);

        String response = coinCapService.createWallet(obj);
        assertNotNull(response);
        assertEquals(response, "Wallet created with ID: 9748957239874987529");

    }

    // TODO Add More test coverage
}
