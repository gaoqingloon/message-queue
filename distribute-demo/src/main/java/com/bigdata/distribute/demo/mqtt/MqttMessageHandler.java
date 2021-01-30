package com.bigdata.distribute.demo.mqtt;

import com.bigdata.distribute.Distributer;
import com.bigdata.distribute.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 *
 */
public class MqttMessageHandler implements MessageHandler {

    @Autowired
    private Distributer distributer;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        distributer.submit(new Packet("test1", message.getPayload()));
    }

}
