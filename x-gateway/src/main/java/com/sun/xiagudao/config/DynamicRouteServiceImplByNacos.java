package com.sun.xiagudao.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@DependsOn("gatewayConf")
@Component
public class DynamicRouteServiceImplByNacos {

    private ConfigService configService;
    private final DynamicRouteServiceImpl dynamicRouteService;

    public DynamicRouteServiceImplByNacos(DynamicRouteServiceImpl dynamicRouteService) {
        this.dynamicRouteService = dynamicRouteService;
    }

    @PostConstruct
    public void init() {
        log.info("route config init...");
        configService = initConfigService();
        if (configService == null) {
            throw new IllegalArgumentException("route config init fail...");
        }
    }

    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConf.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConf.NACOS_NAMESPACE);
            return NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
