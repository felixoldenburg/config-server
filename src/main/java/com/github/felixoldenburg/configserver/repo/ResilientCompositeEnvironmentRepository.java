/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.felixoldenburg.configserver.repo;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

/**
 * A resilient version of the {@link CompositeEnvironmentRepository} which doesn't fail if on its {@link EnvironmentRepository}
 * can't provide any configuration or fails.
 * Useful to set up a back up {@link EnvironmentRepository}: If the first one doesn't has any configuration, pick it up from the next one and so on.
 */
public class ResilientCompositeEnvironmentRepository extends CompositeEnvironmentRepository
{
    private final static Logger LOG = LoggerFactory.getLogger(ResilientCompositeEnvironmentRepository.class);


    /**
     * Creates a new {@link CompositeEnvironmentRepository}.
     *
     * @param environmentRepositories The list of {@link EnvironmentRepository}s to create the composite from.
     */
    public ResilientCompositeEnvironmentRepository(List<EnvironmentRepository> environmentRepositories)
    {
        super(environmentRepositories);
    }


    @Override
    public Environment findOne(String application, String profile, String label)
    {
        Environment env = new Environment(application, new String[] {profile}, label, null, null);
        if (environmentRepositories.size() == 1)
        {
            Environment envRepo = environmentRepositories.get(0).findOne(application, profile, label);
            env.addAll(envRepo.getPropertySources());
            env.setVersion(envRepo.getVersion());
            env.setState(envRepo.getState());
        }
        else
        {
            for (EnvironmentRepository repo : environmentRepositories)
            {
                try
                {
                    final Environment environment = repo.findOne(application, profile, label);

                    // Its ok if an EnvironmentRepository doesn't have an environment
                    if (environment != null)
                    {
                        env.addAll(environment.getPropertySources());
                    }
                }
                catch (Throwable t)
                {
                    LOG.error("Error when getting config from " + repo.toString(), t);
                    // Swallow exception and proceed with next EnvironmentRepository
                }
            }
        }
        return env;
    }
}
