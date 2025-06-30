package com.iotx.iotxauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @Author Zhb
 * @create 2025/6/30 17:01
 */
@Configuration
@EnableResourceServer // 此处是旧版
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


}
