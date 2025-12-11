package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.dto.BootCoinDto;
import com.nttdata.bootcamp.events.BootCoinCreatedEventKafka;
import com.nttdata.bootcamp.events.VirtualCoinCreatedEventKafka;
import com.nttdata.bootcamp.entity.VirtualCoin;
import com.nttdata.bootcamp.entity.enums.EventType;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate<String, EventKafka<?>> producer;  // Usamos KafkaTemplate

    @Value("${topic.virtualCoin.name}")
    private String topicVirtualCoin;

    @Value("${topic.bootCoin.name}")
    private String topicBootCoin;

    // Publicar VirtualCoin de manera asíncrona usando KafkaTemplate
    public void publish(VirtualCoin deposit) {
        VirtualCoinCreatedEventKafka created = new VirtualCoinCreatedEventKafka();
        created.setData(deposit);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        // Enviar evento de forma asíncrona (sin reactividad)
        producer.send(topicVirtualCoin, created);
    }

    // Publicar BootCoin de manera asíncrona usando KafkaTemplate
    public void publishBootCoin(BootCoinDto bootCoinDto) {
        BootCoinCreatedEventKafka created = new BootCoinCreatedEventKafka();
        created.setData(bootCoinDto);
        created.setId(UUID.randomUUID().toString());
        created.setType(EventType.CREATED);
        created.setDate(new Date());

        // Enviar evento de forma asíncrona (sin reactividad)
        producer.send(topicBootCoin, created);
    }
}
