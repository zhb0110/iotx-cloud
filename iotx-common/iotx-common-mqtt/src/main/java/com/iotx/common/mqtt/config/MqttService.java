
package com.iotx.common.mqtt.config;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledExecutorService;

/**
 * MQTT服务类
 */
@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MqttProperties mqttProperties;

    private MqttClient client;
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() throws MqttException {
        connect();
        // 创建定时任务，检查连接状态并重新连接，但是不够优雅
        // 可选：每5秒检查一次连接状态（如果Broker未通知connectionLost）
//        scheduler = new ScheduledThreadPoolExecutor(1);
//        scheduler.scheduleAtFixedRate(this::checkConnection, 0, 5, TimeUnit.SECONDS);
    }


    private void connect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
            logger.info("Existing MQTT connection closed before re-connect.");
        }

        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(mqttProperties.getHostUrl(), mqttProperties.getClientId(), persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setConnectionTimeout(mqttProperties.getCompletionTimeout()); // 连接超时时间（秒）
        options.setKeepAliveInterval(mqttProperties.getKeepAliveTime()); // 心跳间隔（秒）
        options.setAutomaticReconnect(true); // 启用内建自动重连
        options.setCleanSession(false); // 不清除会话，保留订阅信息

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.error("MQTT connection lost: {}", cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                logger.info("Message arrived on topic [{}]: {}", topic, new String(message.getPayload()));
                // 解析topic中的ID
                String[] parts = topic.split("/");
                if (parts.length == 4 && parts[1].equals("2001") && parts[3].equals("up")) {
                    String id = parts[2];
                    logger.info("Received message for ID: {}, Message: {}", id, new String(message.getPayload()));
                } else {
                    logger.warn("Unexpected topic: {}", topic);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                logger.info("Message delivery complete. Token: {}", token.getMessageId());
            }
        });

        client.connect(options);
        logger.info("MQTT client connected to broker: {}", mqttProperties.getHostUrl());

        // 每次连接成功后重新订阅主题
//        for (String subTopic : mqttProperties.getSubTopic()) {
//            client.subscribe(subTopic, (topic, msg) -> {
//                // 局部回调高于全局setCallback
//                logger.info("Received during reconnect: {} - {}", topic, new String(msg.getPayload()));
//            });
//        }
        // 只订阅，不传回调，确保使用 setCallback 设置的全局回调
        client.subscribe(mqttProperties.getSubTopic());

    }


    public boolean publish(String topic, String payload) {
        if (client == null || !client.isConnected()) {
            logger.warn("MQTT client is not connected, message will not be published: {}", payload);
            return false;
        }

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(mqttProperties.getQosLevel());
            message.setRetained(mqttProperties.isRetained());

            client.publish(topic, message);
            logger.info("Published message to topic [{}]: {}", topic, payload);
            return true;
        } catch (MqttException e) {
            logger.error("Failed to publish MQTT message: {}", e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                logger.info("MQTT client disconnected from broker.");
            } catch (MqttException e) {
                logger.error("Error while disconnecting MQTT client: {}", e.getMessage());
            }
        }
    }

    private void checkConnection() {
        if (client == null || !client.isConnected()) {
            try {
                logger.warn("MQTT client detected as disconnected, attempting re-connect...");
                connect();
            } catch (MqttException e) {
                logger.error("MQTT re-connect failed: {}", e.getMessage());
            }
        }
    }


    @PreDestroy
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            logger.info("MQTT connection checker thread pool shutdown.");
        }

        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                logger.info("MQTT client manually disconnected before application shutdown.");
            } catch (MqttException ignored) {
            }
        }
    }
}