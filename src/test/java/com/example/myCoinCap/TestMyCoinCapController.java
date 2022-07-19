package com.example.myCoinCap;

import com.example.myCoinCap.models.Asset;
import com.example.myCoinCap.models.HistoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestMyCoinCapController {
    @Mock
    private MyCoinCapService coinCapService;

    @InjectMocks
    private MyCoinCapController coinCapController;

    @Test
    void testRetrieveAssets() throws Exception {
        lenient().when(coinCapService.getAssets(any())).thenReturn(new ArrayList<Asset>());
        MultiValueMap<String, String> filters = null;
        Object response = coinCapController.retrieveAssets(filters);
        assertNotNull(response);
    }

    @Test
    void testWallet() throws Exception {
        lenient().when(coinCapService.getHistory(anyString(), anyString(), anyInt())).thenReturn(new HistoryResponse());

        Object response = coinCapController.getHistoricData(anyString(), anyString(), anyInt());
        assertNotNull(response);
    }

    @Test
    void testWalletException() throws Exception {
        lenient().when(coinCapService.getHistory(anyString(), anyString(), anyInt())).thenReturn(new HistoryResponse());

        assertThrows(ResponseStatusException.class, () -> {coinCapController.getHistoricData(anyString(), "doop", anyInt());});

    }
}
