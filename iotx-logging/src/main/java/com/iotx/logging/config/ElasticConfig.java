package com.iotx.logging.config;

/**
 * @Author Zhb
 * @create 2025/7/1 14:12
 */
@Configuration
public class ElasticConfig {

    @Value("${elastic.hosts}")
    private String[] hosts;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                new HttpHost(hosts[0], 9200, "http")
        ).build();
    }

    @Bean
    public ElasticsearchTransport transport() {
        return new RestClientTransport(restClient(), new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient client() {
        return new ElasticsearchClient(transport());
    }
}
