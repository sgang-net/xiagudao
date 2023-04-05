package com.sun.xiagudao.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

@Slf4j
@DependsOn("gatewayConfig")
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

        try {
            String config = configService.getConfig(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, GatewayConfig.DEFAULT_TIMEOUT);
            log.info("confList: {}", JSON.toJSONString(config));

            List<RouteDefinition> routeDefinitionList = JSON.parseArray(config, RouteDefinition.class);
            if (!Collections.isEmpty(routeDefinitionList)) {
                routeDefinitionList.forEach(r -> {
                    log.info("conf: {}", JSON.toJSONString(r));
                    dynamicRouteService.addRouteDefinition(r);
                });
            }

            //添加监听
            configService.addListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String s) {
                    log.info("listener confList: {}", JSON.toJSONString(s));
                    List<RouteDefinition> routeDefinitionList = JSON.parseArray(s);
                    dynamicRouteService.updateRouteDefinition(routeDefinitionList);
                }
            });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            return NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
