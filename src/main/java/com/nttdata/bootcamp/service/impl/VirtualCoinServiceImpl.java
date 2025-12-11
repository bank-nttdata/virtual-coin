package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.dto.BootCoinDto;
import com.nttdata.bootcamp.repository.VirtualCoinRepository;
import com.nttdata.bootcamp.service.KafkaService;
import com.nttdata.bootcamp.service.VirtualCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class VirtualCoinServiceImpl implements VirtualCoinService {

    @Autowired
    private VirtualCoinRepository virtualCoinRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public Flux<VirtualCoin> findAllVirtualCoin() {
        // Se devuelve el flujo completo de todos los VirtualCoins sin usar .block()
        return virtualCoinRepository.findAll();
    }

    @Override
    public Mono<VirtualCoin> findVirtualCoinByCellNumber(String cellNumber) {
        // Se busca el VirtualCoin por cellNumber, de manera reactiva
        return virtualCoinRepository.findAll()
                .filter(x -> x.getCellNumber().equals(cellNumber) && x.getTypeOperation().equals("REGISTER"))
                .next();  // next() es reactivo y obtiene el primer elemento
    }

    @Override
    public Flux<VirtualCoin> findVirtualCoinTransactionByCellNumber(String cellNumber) {
        // Se filtra de manera reactiva para obtener las transacciones
        return virtualCoinRepository.findAll()
                .filter(x -> x.getCellNumber().equals(cellNumber) && !x.getTypeOperation().equals("REGISTER"));
    }

    @Override
    public Mono<VirtualCoin> saveVirtualCoin(VirtualCoin dataVirtualCoin) {
        // Se guarda el VirtualCoin de manera reactiva
        return virtualCoinRepository.save(dataVirtualCoin);
    }

    @Override
    public Mono<VirtualCoin> saveTransactionVirtualCoin(VirtualCoin dataVirtualCoin) {
        // Guarda la transacción de manera reactiva
        return saveTopic(dataVirtualCoin);
    }

    // Método reactivo para manejar la lógica de "guardar" y enviar el evento a Kafka
    public Mono<VirtualCoin> saveTopic(VirtualCoin dataVirtual) {
        // Guardamos el VirtualCoin de manera reactiva y luego publicamos el evento de Kafka
        return virtualCoinRepository.save(dataVirtual)
                .doOnSuccess(savedCoin -> {
                    // Publicar el evento de Kafka de forma reactiva
                    kafkaService.publish(savedCoin);
                });
    }

    @Override
    public Mono<VirtualCoin> updateDebiCardVirtualCoin(VirtualCoin dataVirtualCoin) {
        // Buscar el VirtualCoin por cellNumber
        return findVirtualCoinByCellNumber(dataVirtualCoin.getCellNumber())
                .flatMap(existingCoin -> {
                    existingCoin.setFlagDebitCard(dataVirtualCoin.getFlagDebitCard());
                    existingCoin.setNumberDebitCard(dataVirtualCoin.getNumberDebitCard());
                    existingCoin.setModificationDate(dataVirtualCoin.getModificationDate());
                    existingCoin.setNumberAccount(dataVirtualCoin.getNumberAccount());
                    // Actualizar el VirtualCoin de manera reactiva
                    return virtualCoinRepository.save(existingCoin);
                })
                .switchIfEmpty(Mono.error(new Exception("VirtualCoin not found for CellNumber: " + dataVirtualCoin.getCellNumber())));
    }

    @Override
    public Mono<VirtualCoin> saveBootCoin(BootCoinDto bootCoinDto) {
        // Buscar el VirtualCoin por cellNumber y proceder de manera reactiva
        return findVirtualCoinByCellNumber(bootCoinDto.getCellNumberSend())
                .flatMap(existingCoin -> {
                    // Publicar el evento de BootCoin de manera reactiva
                    kafkaService.publishBootCoin(bootCoinDto);
                    return Mono.just(existingCoin);  // Retornar el VirtualCoin existente
                })
                .switchIfEmpty(Mono.error(new Exception("The virtual coin CellNumber: " + bootCoinDto.getCellNumberSend() + " does not exist")));
    }
}
