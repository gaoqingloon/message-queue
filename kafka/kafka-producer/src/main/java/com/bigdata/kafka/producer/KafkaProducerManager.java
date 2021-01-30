package com.bigdata.kafka.producer;

import com.bigdata.kafka.producer.KafkaProducerProperties.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;

/**
 *
 */
public interface KafkaProducerManager {

    /**
     * @param producer
     * @return
     */
    KafkaTemplate<Object, Object> add(KafkaProducer producer);

    /**
     * @param id
     */
    void remove(String id);

}
