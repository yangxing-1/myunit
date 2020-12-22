package com.example.demozuul.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demozuul.route.CustomRouteLocator;
import com.netflix.zuul.http.ZuulServlet;

@Configuration
@EnableConfigurationProperties({ ZuulProperties.class })
@ConditionalOnClass(ZuulServlet.class)
//@Import(ServerPropertiesAutoConfiguration.class)
public class CustomZuulConfig {

    @Autowired
    public ZuulProperties zuulProperties;
    @Autowired
    public ServerProperties server;
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Bean
    public CustomRouteLocator routeLocator() {
        CustomRouteLocator routeLocator = new CustomRouteLocator(this.server.getServlet().getContextPath(), this.zuulProperties);
        routeLocator.setJdbcTemplate(jdbcTemplate);
        return routeLocator;
    }
}
