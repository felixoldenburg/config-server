package com.github.felixoldenburg.configserver.config;

import com.github.felixoldenburg.configserver.repo.MultipleJGitWithDefaultEnvironmentRepository;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class RepositoryConfiguration
{
    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private ConfigServerProperties server;

    @Autowired(required = false)
    private TransportConfigCallback transportConfigCallback;

    @Value("${spring.cloud.config.server.git.default-uri}")
    private String defaultGitUri;


    @Bean
    public MultipleJGitEnvironmentRepository defaultEnvironmentRepository()
    {
        MultipleJGitEnvironmentRepository repository = new MultipleJGitWithDefaultEnvironmentRepository(this.environment, defaultGitUri);
        repository.setTransportConfigCallback(this.transportConfigCallback);
        if (this.server.getDefaultLabel() != null)
        {
            repository.setDefaultLabel(this.server.getDefaultLabel());
        }
        return repository;
    }
}