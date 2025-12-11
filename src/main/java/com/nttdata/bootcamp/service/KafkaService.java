package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.dto.BootCoinDto;

public interface KafkaService {
    void publish(VirtualCoin customer);
    void publishBootCoin(BootCoinDto bootCoinDto);
}
