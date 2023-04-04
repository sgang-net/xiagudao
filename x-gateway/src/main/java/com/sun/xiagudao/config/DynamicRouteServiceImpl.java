package com.sun.xiagudao.config;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    private final RouteDefinitionWriter routeDefinitionWriter;

    private final RouteDefinitionLocator routeDefinitionLocator;

    private ApplicationEventPublisher publisher;

    public DynamicRouteServiceImpl(RouteDefinitionWriter writer, RouteDefinitionLocator locator) {
        this.routeDefinitionWriter = writer;
        this.routeDefinitionLocator = locator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void updateRouteDefinition(List<RouteDefinition> routeDefinitionList) {
        List<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!Collections.isEmpty(routeDefinitions)) {
            routeDefinitions.forEach(routeDefinition -> deleteRouteDefinition(routeDefinition.getId()));
        }

        routeDefinitionList.forEach(routeDefinition -> addRouteDefinition(routeDefinition));
    }

    public void addRouteDefinition(RouteDefinition routeDefinition) {
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    public void deleteRouteDefinition(String id) {
        routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
