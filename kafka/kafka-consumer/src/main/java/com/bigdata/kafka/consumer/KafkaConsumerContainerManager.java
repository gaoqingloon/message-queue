package com.bigdata.kafka.consumer;

import com.bigdata.kafka.consumer.listener.KafkaMessageListenerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Slf4j
public class KafkaConsumerContainerManager implements ApplicationContextAware, KafkaConsumerManager {

    private static final String KAFKA_CONTAINER_ID_SUFFIX = "_kafka_container";

    private KafkaConsumerProperties properties;

    private Map<String, AbstractMessageListenerContainer<?, ?>> kafkaContainerMap = new ConcurrentHashMap<>();

    private KafkaMessageListenerFactory listenerFactory;

    private ApplicationContext applicationContext;

    /**
     * @param listenerFactory
     * @param properties
     */
    public KafkaConsumerContainerManager(KafkaConsumerProperties properties, KafkaMessageListenerFactory listenerFactory) {
        this.properties = properties;
        this.listenerFactory = listenerFactory;
    }

    @PostConstruct
    public void initKafkaConsumerContainer() {
        properties.getConsumers().forEach(consumer -> {
            try {
                add(consumer);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * @param consumer
     * @throws ClassNotFoundException
     */
    @Override
    public void add(KafkaConsumerProperties.KafkaConsumer consumer) throws ClassNotFoundException {
        AbstractMessageListenerContainer<Object, Object> kafkaContainer = kafkaContainer(consumer, consumerFactory(consumerOptions(consumer)));
        kafkaContainer.start();
        kafkaContainerMap.put(getContainerKey(consumer.getId()), kafkaContainer);
        log.info("started " + getContainerKey(consumer.getId()));
    }

    @Override
    public void add(KafkaConsumerProperties.KafkaConsumer consumer, MessageListener<?, ?> messageListener) {
        AbstractMessageListenerContainer<Object, Object> kafkaContainer = kafkaContainer(consumer, consumerFactory(consumerOptions(consumer)),
                messageListener);
        kafkaContainer.start();
        kafkaContainerMap.put(getContainerKey(consumer.getId()), kafkaContainer);
        log.info("started " + getContainerKey(consumer.getId()));
    }

    /**
     * @see KafkaConsumerManager#remove(java.lang.String)
     */
    @Override
    public void remove(String id) {
        AbstractMessageListenerContainer<?, ?> container = kafkaContainerMap.get(getContainerKey(id));
        container.stop();
        kafkaContainerMap.remove(getContainerKey(id));
    }

    @PreDestroy
    public void destroyKafkaConsumerContainer() {
        kafkaContainerMap.entrySet().forEach(entry -> {
            entry.getValue().stop();
        });
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<String, Object> consumerOptions(KafkaConsumerProperties.KafkaConsumer consumer) {
        Map<String, Object> map = new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.getServers());
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.isEnableAutoCommit());
        map.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumer.getSessionTimeout());
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumer.getKeyDeserializer());
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
        map.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset());
        return map;
    }

    private ConsumerFactory<Object, Object> consumerFactory(Map<String, Object> map) {
        return new DefaultKafkaConsumerFactory<>(map);
    }

    private AbstractMessageListenerContainer<Object, Object> kafkaContainer(KafkaConsumerProperties.KafkaConsumer consumer, ConsumerFactory<Object, Object> consumerFactory)
            throws ClassNotFoundException {
        return kafkaContainer(consumer, consumerFactory, messageListener(consumer));
    }

    /**
     * @param consumer
     * @param consumerFactory
     * @param messageListener
     * @return
     */
    private AbstractMessageListenerContainer<Object, Object> kafkaContainer(KafkaConsumerProperties.KafkaConsumer consumer, ConsumerFactory<Object, Object> consumerFactory,
                                                                            Object messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(consumer.getTopics().toArray(new String[]{}));
        containerProperties.setGroupId(consumer.getGroupId());
        containerProperties.setMessageListener(messageListener);
        ConcurrentMessageListenerContainer<Object, Object> kafkaContainer = new ConcurrentMessageListenerContainer<>(consumerFactory,
                containerProperties);
        kafkaContainer.setConcurrency(consumer.getConcurrency());
        return kafkaContainer;
    }

    private Object messageListener(KafkaConsumerProperties.KafkaConsumer consumer) throws ClassNotFoundException {
        if (!StringUtils.isEmpty(consumer.getMessageListener())) {
            return createBean(consumer);
        } else {
            return listenerFactory.create();
        }
    }

    /**
     * @param consumer
     * @return
     * @throws ClassNotFoundException
     */
    private Object createBean(KafkaConsumerProperties.KafkaConsumer consumer) throws ClassNotFoundException {
        Class<?> clz = Class.forName(consumer.getMessageListener());
        if (!GenericMessageListener.class.isAssignableFrom(clz)) {
            throw new ClassCastException(consumer.getMessageListener() + " is not a subclass of " + GenericMessageListener.class.getName());
        }
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(consumer.getMessageListener());
        defaultListableBeanFactory.registerBeanDefinition(consumer.getId() + "_kafkaMessageListener", builder.getRawBeanDefinition());
        return context.getBean(consumer.getId() + "_kafkaMessageListener");
    }

    /**
     * @param id
     * @return
     */
    private String getContainerKey(String id) {
        return id + KAFKA_CONTAINER_ID_SUFFIX;
    }
}
