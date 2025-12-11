package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.VirtualCoin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface VirtualCoinRepository extends ReactiveCrudRepository<VirtualCoin, String> {
}
