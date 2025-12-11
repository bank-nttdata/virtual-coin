package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.dto.BootCoinDto;
import com.nttdata.bootcamp.entity.dto.UpdateVirtualCoinDto;
import com.nttdata.bootcamp.entity.dto.VirtualCoinTransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.VirtualCoinService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import com.nttdata.bootcamp.entity.dto.VirtualCoinDto;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/virtual-coin")
public class VirtualCoinController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualCoinController.class);

    @Autowired
    private VirtualCoinService virtualCoinService;

    // All Virtual-coin Registered
    @GetMapping("/findAllVirtualCoin")
    public Flux<VirtualCoin> findAllVirtualCoin() {
        return virtualCoinService.findAllVirtualCoin()
                .doOnSubscribe(s -> LOGGER.info("Fetching all virtual coins"))
                .doOnNext(v -> LOGGER.info("Virtual coin: {}", v));
    }

    // Virtual-coin registered by customer
    @GetMapping("/findVirtualCoinByCellNumber/{cellNumber}")
    public Mono<VirtualCoin> findVirtualCoinByCellNumber(@PathVariable("cellNumber") String cellNumber) {
        return virtualCoinService.findVirtualCoinByCellNumber(cellNumber)
                .doOnSubscribe(s -> LOGGER.info("Fetching virtual coin by cell number: {}", cellNumber))
                .doOnSuccess(v -> LOGGER.info("Found virtual coin: {}", v));
    }

    // Virtual coin transactions by customer cell number
    @GetMapping("/findTransactionsByCellNumber/{cellNumber}")
    public Flux<VirtualCoin> findTransactionsByCellNumber(@PathVariable("cellNumber") String cellNumber) {
        return virtualCoinService.findVirtualCoinTransactionByCellNumber(cellNumber)
                .doOnSubscribe(s -> LOGGER.info("Fetching transactions by cell number: {}", cellNumber))
                .doOnNext(v -> LOGGER.info("Transaction: {}", v));
    }

    // Save Virtual-coin
    @PostMapping(value = "/saveVirtualCoin")
    public Mono<VirtualCoin> saveVirtualCoin(@RequestBody VirtualCoinDto virtualCoinDto) {
        VirtualCoin virtualCoin = new VirtualCoin();
        virtualCoin.setDni(virtualCoinDto.getDni());
        virtualCoin.setEmail(virtualCoinDto.getEmail());
        virtualCoin.setCellNumber(virtualCoinDto.getCellNumber());
        virtualCoin.setIMEI(virtualCoinDto.getIMEI());
        virtualCoin.setNumberAccount("");
        virtualCoin.setMount(0.00);
        virtualCoin.setFlagDebitCard(false);
        virtualCoin.setNumberDebitCard("");
        virtualCoin.setTypeOperation("REGISTER");
        virtualCoin.setCreationDate(new Date());
        virtualCoin.setModificationDate(new Date());

        // Save the virtual coin reactively
        return virtualCoinService.saveVirtualCoin(virtualCoin)
                .doOnSubscribe(s -> LOGGER.info("Saving virtual coin"))
                .doOnSuccess(v -> LOGGER.info("Saved virtual coin: {}", v));
    }

    // Save the send of a payment
    @PostMapping(value = "/sendPayment")
    public Mono<VirtualCoin> sendPayment(@RequestBody VirtualCoinTransactionDto transactionDto) {
        return virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber())
                .flatMap(virtualCoin -> {
                    virtualCoin.setMount(transactionDto.getMount() * -1);
                    virtualCoin.setTypeOperation("SEND");
                    virtualCoin.setModificationDate(new Date());
                    return virtualCoinService.saveTransactionVirtualCoin(virtualCoin)
                            .doOnSubscribe(s -> LOGGER.info("Sending payment"))
                            .doOnSuccess(v -> LOGGER.info("Payment sent: {}", v));
                });
    }

    // Save get of a paid
    @PostMapping(value = "/getPaid")
    public Mono<VirtualCoin> getPaid(@RequestBody VirtualCoinTransactionDto transactionDto) {
        return virtualCoinService.findVirtualCoinByCellNumber(transactionDto.getCellNumber())
                .flatMap(virtualCoin -> {
                    virtualCoin.setMount(transactionDto.getMount());
                    virtualCoin.setTypeOperation("GET");
                    virtualCoin.setModificationDate(new Date());
                    return virtualCoinService.saveTransactionVirtualCoin(virtualCoin)
                            .doOnSubscribe(s -> LOGGER.info("Getting paid"))
                            .doOnSuccess(v -> LOGGER.info("Payment received: {}", v));
                });
    }

    // Update debit card of the Virtual-coin
    @PutMapping("/updateDebiCardVirtualCoin")
    public Mono<VirtualCoin> updateDebiCardVirtualCoin(@RequestBody UpdateVirtualCoinDto virtualCoinDto) {
        VirtualCoin dataVirtualCoin = new VirtualCoin();
        dataVirtualCoin.setCellNumber(virtualCoinDto.getCellNumber());
        dataVirtualCoin.setFlagDebitCard(true);
        dataVirtualCoin.setNumberDebitCard(virtualCoinDto.getDebitCard());
        dataVirtualCoin.setNumberAccount(virtualCoinDto.getNumberAccount());
        dataVirtualCoin.setModificationDate(new Date());

        return virtualCoinService.updateDebiCardVirtualCoin(dataVirtualCoin)
                .doOnSubscribe(s -> LOGGER.info("Updating debit card for virtual coin"))
                .doOnSuccess(v -> LOGGER.info("Debit card updated: {}", v));
    }

    @PostMapping(value = "/saveBootCoin")
    public Mono<VirtualCoin> saveTransactionBootCoin(@RequestBody BootCoinDto bootCoinDto) {
        return virtualCoinService.saveBootCoin(bootCoinDto)
                .doOnSubscribe(s -> LOGGER.info("Saving BootCoin"))
                .doOnSuccess(v -> LOGGER.info("BootCoin saved: {}", v));
    }
}
