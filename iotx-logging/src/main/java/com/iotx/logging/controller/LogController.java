package com.iotx.logging.controller;

/**
 * @Author Zhb
 * @create 2025/7/1 14:12
 */
@Service
public class LogCollector {
    @Autowired private ElasticsearchClient client;

    // 收集微服务日志
    // 常用
    public void collectServiceLog(ServiceLog log) {
        IndexRequest<ServiceLog> request = IndexRequest.of(i -> i
                .index("iot-service-logs")
                .document(log)
        );
        client.index(request);
    }

    // 收集设备行为日志
    // 常用
    public void collectDeviceBehavior(DeviceBehavior behavior) {
        BulkRequest.Builder br = new BulkRequest.Builder();
        br.operations(op -> op
                .index(idx -> idx
                        .index("iot-device-behavior")
                        .document(behavior)
                )
        );
        client.bulk(br.build());
    }
}
