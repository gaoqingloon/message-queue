package com.bigdata.distribute.demo.kafka;

import com.bigdata.distribute.Endpoint;
import com.bigdata.distribute.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 */
@Slf4j
@Service
public class PrintEndPoint implements Endpoint {

    @Override
    public void handle(Packet packet) {
        log.info(packet.getPayload().toString());
    }

}
