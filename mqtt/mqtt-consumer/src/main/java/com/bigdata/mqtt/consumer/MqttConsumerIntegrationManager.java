package com.bigdata.mqtt.consumer;

import com.bigdata.mqtt.consumer.handler.MqttMessageHandlerFactory;
import com.bigdata.mqtt.utils.SslUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 *
 */
@Slf4j
public class MqttConsumerIntegrationManager implements MqttConsumerManager, ApplicationContextAware {

    /**
     *
     */
    private static final String CONSUMER_ADAPTER_SUFFIX = "_consumer_adpater";

    /**
     *
     */
    private static final String FLOW_ID_SUFFIX = "_consumer";

    private MqttConsumerProperties mqttProperties;

    private IntegrationFlowContext flowContext;

    private MqttMessageHandlerFactory messageHandlerFactory;

    private ApplicationContext applicationContext;

    /**
     * @param mqttProperties
     * @param flowContext
     */
    public MqttConsumerIntegrationManager(MqttConsumerProperties mqttProperties, IntegrationFlowContext flowContext) {
        this.flowContext = flowContext;
        this.mqttProperties = mqttProperties;
    }

    /**
     * @param messageHandlerFactory
     */
    public void setHandlerFactory(MqttMessageHandlerFactory messageHandlerFactory) {
        this.messageHandlerFactory = messageHandlerFactory;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initIntegrationFlows() {
        mqttProperties.getConsumers().forEach(consumer -> {
            try {
                add(consumer, messageHandler(consumer), false);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * @throws ClassNotFoundException
     * @see MqttConsumerManager#add(MqttConsumerProperties.Consumer)
     */
    @Override
    public void add(MqttConsumerProperties.Consumer consumer) throws ClassNotFoundException {
        MessageHandler messageHandler = messageHandler(consumer);
        add(consumer, messageHandler, true);
    }

    /**
     * @see MqttConsumerManager#add(MqttConsumerProperties.Consumer,
     * org.springframework.messaging.MessageHandler)
     */
    @Override
    public void add(MqttConsumerProperties.Consumer consumer, MessageHandler handler) {
        add(consumer, handler, true);
    }

    /**
     * @see MqttConsumerManager#remove(java.lang.String)
     */
    @Override
    public void remove(String id) {
        flowContext.remove(id + FLOW_ID_SUFFIX);
        removeBean(getHandlerBeanName(id));
    }

    private void add(MqttConsumerProperties.Consumer consumer, MessageHandler handler, boolean start) {
        MessageChannel channel = mqttInputChannel();
        StandardIntegrationFlow flow = IntegrationFlows.from(channel).handle(handler).get();
        MqttPahoMessageDrivenChannelAdapter adapter = channelAdapter(clientFactory(options(consumer)), consumer, channel);
        flowContext.registration(flow).id(consumer.getId() + FLOW_ID_SUFFIX).addBean(consumer.getId() + CONSUMER_ADAPTER_SUFFIX, adapter).register();
        log.info("register " + consumer.getId());
        if (start) {
            adapter.start();
        }
    }

    /**
     * @param clientFactory
     * @param consumer
     * @param messageChannel
     * @return
     */
    private MqttPahoMessageDrivenChannelAdapter channelAdapter(MqttPahoClientFactory clientFactory, MqttConsumerProperties.Consumer consumer,
                                                               MessageChannel messageChannel) {
        String[] topics = new String[consumer.getTopics().size()];
        int[] qos = new int[consumer.getTopics().size()];
        for (int i = 0; i < consumer.getTopics().size(); i++) {
            topics[i] = consumer.getTopics().get(i).getTopic();
            qos[i] = consumer.getTopics().get(i).getQos();
        }
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(consumer.getClientId(), clientFactory, topics);
        adapter.setCompletionTimeout(consumer.getCompletionTimeout());
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        if (consumer.isPayloadAsBytes()) {
            converter.setPayloadAsBytes(consumer.isPayloadAsBytes());
        }
        adapter.setConverter(converter);
        adapter.setQos(qos);
        adapter.setOutputChannel(messageChannel);
        return adapter;
    }

    private MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * @param options
     */
    private MqttPahoClientFactory clientFactory(MqttConnectOptions options) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    private MqttConnectOptions options(MqttConsumerProperties.Consumer properties) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getUrl()});
        options.setKeepAliveInterval(properties.getKeepAlive());
        if (!StringUtils.isEmpty(properties.getPassword())) {
            options.setPassword(properties.getPassword().toCharArray());
        }
        options.setUserName(properties.getUsername());
        if (properties.isSslEnable()) {
            try {
                MqttConsumerProperties.SSLProperties sslProperties = properties.getSsl();
                options.setSocketFactory(SslUtil.getSocketFactory(sslProperties.getCaFile(), sslProperties.getClientFile(),
                        sslProperties.getKeyFile(), sslProperties.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // TODO will message
        return options;
    }

    /**
     * @param consumer
     * @return
     * @throws ClassNotFoundException
     */
    private MessageHandler messageHandler(MqttConsumerProperties.Consumer consumer) throws ClassNotFoundException {
        if (!StringUtils.isEmpty(consumer.getMessageHandler())) {
            return createBean(consumer);
        } else {
            return messageHandlerFactory.create();
        }
    }

    /**
     * @param consumer
     * @return
     * @throws ClassNotFoundException
     */
    private MessageHandler createBean(MqttConsumerProperties.Consumer consumer) throws ClassNotFoundException {
        Class<?> clz = Class.forName(consumer.getMessageHandler());
        if (!MessageHandler.class.isAssignableFrom(clz)) {
            throw new ClassCastException(consumer.getMessageHandler() + " is not a subclass of " + MessageHandler.class.getName());
        }
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
        beanFactory.registerBeanDefinition(getHandlerBeanName(consumer.getId()), builder.getRawBeanDefinition());
        return context.getBean(getHandlerBeanName(consumer.getId()), MessageHandler.class);
    }

    /**
     * @param beanName
     */
    private void removeBean(String beanName) {
        if (applicationContext.containsBeanDefinition(beanName)) {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
            beanFactory.removeBeanDefinition(beanName);
        }
    }

    /**
     * @param id
     * @return
     */
    private String getHandlerBeanName(String id) {
        return id + "_MqttMessageHandler";
    }
}
