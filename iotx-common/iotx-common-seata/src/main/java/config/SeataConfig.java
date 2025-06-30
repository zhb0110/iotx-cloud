package config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Zhb
 * @create 2025/6/30 17:45
 */
@Configuration
public class SeataConfig {
    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        return new GlobalTransactionScanner("iotx-system", "my_test_tx_group");
    }
}
