package com.nttdata.bootcamp.events;

import com.nttdata.bootcamp.entity.VirtualCoin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VirtualCoinCreatedEventKafka extends EventKafka<VirtualCoin> {

}
