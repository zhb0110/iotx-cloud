package com.iotx.logging.service;

/**
 * @Author Zhb
 * @create 2025/7/1 14:12
 */
public class DeviceSearchService {

    // 设备全文检索服务 (DeviceSearchService.java)
    public List<DeviceMeta> searchDevices(String keyword, String location) {
        Query geoQuery = GeoDistanceQuery.of(g -> g
                .distance("10km")
                .location(l -> l.latlon(ll -> ll.lat(39.9).lon(116.4)))
                .build()._toQuery();

        Query textQuery = MatchQuery.of(m -> m
                        .field("deviceName").query(keyword))
                .build()._toQuery();

        SearchResponse<DeviceMeta> response = client.search(s -> s
                        .index("iot-device-meta")
                        .query(q -> q.bool(b -> b.must(geoQuery, textQuery)))
                , DeviceMeta.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    // 时序数据+ES 混合查询方案
    public DeviceDetail getDeviceDetail(String deviceId) {
        // 1. 从ES获取设备元数据
        DeviceMeta meta = client.get(g -> g
                .index("iot-device-meta")
                .id(deviceId), DeviceMeta.class).source();

        // 2. 从InfluxDB获取实时数据
        String fluxQuery = """
        from(bucket: "iot_metrics")
        |> range(start: -1h)
        |> filter(fn: (r) => r.deviceId == "${deviceId}")
    """;
        List<FluxRecord> records = influxDB.query(fluxQuery);

        // 3. 组合结果
        return new DeviceDetail(meta, records);
    }
}
