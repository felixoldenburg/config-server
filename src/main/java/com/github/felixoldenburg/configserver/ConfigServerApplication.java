package com.github.felixoldenburg.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * TODO: Getting configuration is very slow (a few seconds)
 * TODO: Implement ConfigServerHealthIndicator
 */
@SpringBootApplication
public class ConfigServerApplication extends SpringBootServletInitializer
{
    private static final Class<ConfigServerApplication> APP_STARTER_CONFIG_CLASS = ConfigServerApplication.class;


    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
    {
        return builder.sources(APP_STARTER_CONFIG_CLASS);
    }

    public static void main(String[] args){

        SpringApplication.run(ConfigServerApplication.class, args);

    }
}
