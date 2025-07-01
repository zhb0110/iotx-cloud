@Document(indexName = "iot-device-meta")
public class DeviceMeta {
    @Id
    private String deviceId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String deviceName;

    @Field(type = FieldType.Keyword)
    private String productType; // 传感器类型

    @Field(type = FieldType.GeoPoint)
    private GeoLocation location; // GPS坐标

    @Field(type = FieldType.Date)
    private Date lastOnlineTime;
}