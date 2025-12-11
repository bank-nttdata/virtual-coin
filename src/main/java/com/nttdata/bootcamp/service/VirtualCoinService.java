package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.dto.BootCoinDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface VirtualCoinService {
    Flux<VirtualCoin> findAllVirtualCoin();
    Mono<VirtualCoin> findVirtualCoinByCellNumber(String dni);

    Flux<VirtualCoin>  findVirtualCoinTransactionByCellNumber(String dni);
    Mono<VirtualCoin> saveVirtualCoin(VirtualCoin dataVirtualCoin);
    Mono<VirtualCoin> saveTransactionVirtualCoin(VirtualCoin dataVirtualCoin);
    Mono<VirtualCoin> updateDebiCardVirtualCoin(VirtualCoin dataVirtualCoin);
    Mono<VirtualCoin> saveBootCoin(BootCoinDto bootCoinDto);
}
