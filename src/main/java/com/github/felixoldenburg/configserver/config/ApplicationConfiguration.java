package com.github.felixoldenburg.configserver.config;

import com.github.felixoldenburg.ZooKeeperEnvironmentRepository;
import com.github.felixoldenburg.configserver.repo.ResilientCompositeEnvironmentRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration
{
    @Autowired
    public ZooKeeperEnvironmentRepository zooKeeperEnvironmentRepository;

    @Autowired
    public MultipleJGitEnvironmentRepository multipleJGitEnvironmentRepository;


    @Bean
    public CompositeEnvironmentRepository compositeEnvironmentRepository()
    {
        return new ResilientCompositeEnvironmentRepository(Lists.newArrayList(multipleJGitEnvironmentRepository, zooKeeperEnvironmentRepository));
    }
}
