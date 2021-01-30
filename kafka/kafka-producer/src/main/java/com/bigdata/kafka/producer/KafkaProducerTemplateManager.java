package com.bigdata.kafka.producer;

import com.bigdata.kafka.producer.KafkaProducerProperties.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class KafkaProducerTemplateManager implements ApplicationContextAware, KafkaProducerManager {

    private static final String KAFKA_TEMPLATE_SUFFIX = "_KafkaTemplate";

    private KafkaProducerProperties properties;

    private ApplicationContext applicationContext;

    /**
     * @param properties
     */
    public KafkaProducerTemplateManager(KafkaProducerProperties properties) {
        this.properties = properties;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initKafkaTemplate() {
        properties.getProducers().forEach(producer -> {
            add(producer);
        });
    }

    /**
     * @param producer
     */
    @Override
    public KafkaTemplate<Object, Object> add(KafkaProducer producer) {
        Map<String, Object> map = producerOptions(producer);
        ProducerFactory<Object, Object> producerFactory = new DefaultKafkaProducerFactory<>(map);
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        if (!StringUtils.isEmpty(producer.getDefaultTopic())) {
            kafkaTemplate.setDefaultTopic(producer.getDefaultTopic());
        }
        registerBean(getBeanName(producer.getId()), kafkaTemplate);
        log.info("started " + getBeanName(producer.getId()));
        return kafkaTemplate;
    }

    /**
     * @param producer
     */
    private Map<String, Object> producerOptions(KafkaProducer producer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer.getServers());
        props.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLinger());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer.getValueSerializer());
        return props;
    }

    /**
     * @param beanName
     * @param kafkaTemplate
     */
    private void registerBean(String beanName, KafkaTemplate<Object, Object> kafkaTemplate) {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(KafkaTemplate.class, () -> kafkaTemplate);
        defaultListableBeanFactory.registerBeanDefinition(beanName, builder.getRawBeanDefinition());
    }

    @Override
    public void remove(String id) {
        removeBean(getBeanName(id));
    }

    /**
     * @param beanName
     */
    private void removeBean(String beanName) {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        defaultListableBeanFactory.removeBeanDefinition(beanName);
    }


    /**
     * @param id
     * @return
     */
    private String getBeanName(String id) {
        return id + KAFKA_TEMPLATE_SUFFIX;
    }
}
