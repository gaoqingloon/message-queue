package com.bigdata.distribute.demo.kafka;

import com.bigdata.distribute.Endpoint;
import com.bigdata.distribute.EndpointMatcher;
import com.bigdata.distribute.Packet;
import com.bigdata.distribute.annotation.EndPointInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 *
 */
@Service
@EndPointInfo(value = "test1", matcher = EndpointMatcher.FULL)
@Slf4j
public class KafkaEndpoint implements Endpoint {

    @Autowired(required = false)
    @Lazy
    private KafkaTemplate<Object, Object> kafkaTemplate;

    /**
     * @see com.bigdata.distribute.Endpoint#handle(com.bigdata.distribute.Packet)
     */
    @Override
    public void handle(Packet packet) {
        ListenableFuture<SendResult<Object, Object>> future = kafkaTemplate.send("test", packet.getPayload());
        future.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {

            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                log.info("success");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }

}
