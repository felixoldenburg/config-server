package com.github.felixoldenburg.configserver.config;

import com.github.felixoldenburg.configserver.repo.ResilientCompositeEnvironmentRepository;
import java.util.List;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.config.ConfigServerAutoConfiguration;
import org.springframework.cloud.config.server.config.ConfigServerEncryptionConfiguration;
import org.springframework.cloud.config.server.config.ConfigServerMvcConfiguration;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.config.ResourceRepositoryConfiguration;
import org.springframework.cloud.config.server.config.TransportConfiguration;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathCompositeEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableAutoConfiguration(exclude = {ConfigServerAutoConfiguration.class})
@EnableConfigurationProperties(ConfigServerProperties.class)
@Import({
    EnvironmentRepositoryConfiguration.class,
    ResourceRepositoryConfiguration.class,
    ConfigServerEncryptionConfiguration.class,
    ConfigServerMvcConfiguration.class,
    TransportConfiguration.class})
public class ApplicationConfiguration
{
    @Bean
    @Primary
    public SearchPathCompositeEnvironmentRepository compositeEnvironmentRepository(List<EnvironmentRepository> environmentRepositories)
    {
        return new ResilientCompositeEnvironmentRepository(environmentRepositories);
    }
}
