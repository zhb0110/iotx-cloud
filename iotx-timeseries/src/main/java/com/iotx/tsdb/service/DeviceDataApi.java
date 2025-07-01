package com.iotx.tsdb.service;

import com.iotx.tsdb.model.DeviceData;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Zhb
 * @create 2025/7/1 13:58
 */
@RestController
@RequestMapping("/tsdb")
public class DeviceDataApi {

    private InfluxDB influxDB;

    @GetMapping("/query")
    public List<DeviceData> query(
            @RequestParam String deviceName,
            @RequestParam long start,
            @RequestParam long end) {

        String query = "SELECT * FROM device_metrics WHERE "
                + "deviceName='%s' AND time >= %dms AND time <= %dms";
        String database = "iotx";

//        查询优化：
//        ● 时间范围必传（避免全表扫描）
//        ● 按设备名分片查询
        QueryResult result = influxDB.query(new Query(
                String.format(query, deviceName, start, end), database
        ));
        return parseResult(result);
    }

    private List<DeviceData> parseResult(QueryResult result) {
        return null;
    }
}
