package com.bigdata.mqtt.producer;

import com.bigdata.mqtt.producer.MqttProducerProperties.Producer;
import com.bigdata.mqtt.producer.MqttProducerProperties.SSLProperties;
import com.bigdata.mqtt.producer.utils.MqttProducerConstans;
import com.bigdata.mqtt.utils.SslUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 *
 */
@Slf4j
public class MqttProducerIntegrationManager implements MqttProducerManager {

    /**
     *
     */
    private static final String FLOW_ID_SUFFIX = "_producer_flow";

    private MqttProducerProperties properties;

    private IntegrationFlowContext flowContext;

    /**
     * @param properties
     * @param flowContext
     */
    public MqttProducerIntegrationManager(MqttProducerProperties properties, IntegrationFlowContext flowContext) {
        this.properties = properties;
        this.flowContext = flowContext;
    }

    @PostConstruct
    public void initIntegrationFlows() {
        properties.getProducers().forEach(producer -> {
            addProducer(producer, false);
        });
    }

    /**
     * @see MqttProducerManager#add(MqttProducerProperties.Producer)
     */
    @Override
    public MqttMessageTemplate add(Producer producer) {
        return addProducer(producer, true);
    }

    /**
     * @see MqttProducerManager#remove(java.lang.String)
     */
    @Override
    public void remove(String id) {
        flowContext.remove(id + FLOW_ID_SUFFIX);
    }

    private MqttMessageTemplate addProducer(Producer producer, boolean start) {
        MessageChannel messageChannel = mqttOutboundChannel();
        StandardIntegrationFlow flow = IntegrationFlows.from(messageChannel).handle(messageHandler(producer, mqttClientFactory(options(producer))))
                .get();
        MqttMessageTemplate messageProducer = messageProducer(messageChannel, producer);
        flowContext.registration(flow).id(producer.getId() + FLOW_ID_SUFFIX)
                .addBean(producer.getId() + MqttProducerConstans.MESSAGE_TEMPLATE_SUFFIX, messageProducer).register();
        log.info("register producer:" + producer.getId());
        return messageProducer;
    }

    private MqttMessageTemplate messageProducer(MessageChannel channel, Producer producer) {
        return new MqttMessageTemplate(channel, producer.getDefaultTopic());
    }

    private MessageHandler messageHandler(Producer producer, MqttPahoClientFactory clientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(producer.getClientId(), clientFactory);
        messageHandler.setAsync(producer.isAsync());
        return messageHandler;
    }

    private MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    private MqttPahoClientFactory mqttClientFactory(MqttConnectOptions options) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    private MqttConnectOptions options(Producer properties) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getUrl()});
        options.setKeepAliveInterval(properties.getKeepAlive());
        if (!StringUtils.isEmpty(properties.getPassword())) {
            options.setPassword(properties.getPassword().toCharArray());
        }
        options.setUserName(properties.getUsername());
        if (properties.isSslEnable()) {
            try {
                SSLProperties sslProperties = properties.getSsl();
                options.setSocketFactory(SslUtil.getSocketFactory(sslProperties.getCaFile(), sslProperties.getClientFile(),
                        sslProperties.getKeyFile(), sslProperties.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return options;
    }
}
