package com.iotx.tsdb.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author Zhb
 * 连接配置
 * @create 2025/7/1 13:58
 */
@Configuration
@ConfigurationProperties(prefix = "influx")
public class InfluxDbConfig {
    private String url;      // http://localhost:8086
    private String user;     // root
    private String password; // root
    private String database; // iot_metrics

    @Bean
    public InfluxDB influxDB() {

        // 注意批处理enableBatch能优化降低写入压力（每1000点或200ms刷盘）
        return InfluxDBFactory.connect(url, user, password)
                .setDatabase(database)
                .enableBatch(1000, 200, TimeUnit.MILLISECONDS); // 批处理优化
    }
}
