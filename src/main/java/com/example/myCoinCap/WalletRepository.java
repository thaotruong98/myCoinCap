package com.example.myCoinCap;

import com.example.myCoinCap.models.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletRepository extends MongoRepository<Wallet, String> {
}
