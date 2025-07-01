package com.iotx.mqtt;

import com.iotx.mqtt.model.DeviceMessage;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author Zhb
 * @create 2025/7/1 13:33
 */
@Service
public class MqttClientService {
    @Autowired
    private MqttConfig config;
    private IMqttClient mqttClient;

    @PostConstruct
    public void init() throws MqttException {
        mqttClient = new MqttClient(config.getBrokerUrl(), config.getClientId());
        mqttClient.connect(config.mqttConnectOptions());
        mqttClient.subscribe(config.getSubscribeTopics()[0], (topic, msg) -> {
            // 1. 解析设备消息
            DeviceMessage message = parsePayload(msg.getPayload());
            // 2. 转发至内部消息队列 (Kafka/RocketMQ)，可以使用rabbitmq实现
//            kafkaTemplate.send("iot-data-topic", message);
        });
    }

    private DeviceMessage parsePayload(byte[] payload) {
        return null;
    }

    // 下发指令到设备
    public void publishCommand(String deviceId, String command) throws MqttException {
        mqttClient.publish("command/" + deviceId, command.getBytes(), 1, false);
    }
}
