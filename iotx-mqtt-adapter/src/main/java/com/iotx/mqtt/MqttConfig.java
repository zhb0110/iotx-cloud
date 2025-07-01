package com.iotx.mqtt;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author Zhb
 * @create 2025/7/1 13:33
 */
@Configuration
@ConfigurationProperties(prefix = "iotx.mqtt")
@Data
public class MqttConfig {
    private String brokerUrl;  // tcp://broker.emqx.io:1883
    private String clientId;   // iotx-server-${random.uuid}
    private String username;
    private String password;
    private String[] subscribeTopics; // 如 "device/+/status"

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true); // 断线重连
        return options;
    }


}
