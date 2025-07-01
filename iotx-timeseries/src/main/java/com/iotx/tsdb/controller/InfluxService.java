package com.iotx.tsdb.controller;

import com.iotx.tsdb.model.DeviceData;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author Zhb
 * @create 2025/7/1 13:58
 */
@Service
public class InfluxService {
    @Autowired
    private InfluxDB influxDB;

    public void writeDeviceData(DeviceData data) {

//        据模型设计：
//        ● Measurement：device_metrics（设备指标集）
//        ● Tag：productKey, deviceName（高效过滤）
//        ● Field：temperature, humidity（可变数值）
        Point point = Point.measurement("device_metrics")
                .tag("productKey", data.getProductKey())
                .tag("deviceName", data.getDeviceName())
                .addField("temperature", data.getTemp())
                .addField("humidity", data.getHumidity())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();
        influxDB.write(point);
    }
}
