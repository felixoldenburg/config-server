package com.github.felixoldenburg.configserver.repo;

import java.io.File;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * A {@code MultipleJGitEnvironmentRepository} which aggregates the standard resolved environment with a default repository,
 * which can be used to store global configuration.
 * The Application specific {@link Environment} takes higher precedence, overwriting any global configuration with the same key.
 */
public class MultipleJGitWithDefaultEnvironmentRepository extends MultipleJGitEnvironmentRepository
{
    private JGitEnvironmentRepository globalRepository;
    private final String defaultRepoUri;


    public MultipleJGitWithDefaultEnvironmentRepository(ConfigurableEnvironment environment, String defaultRepoUri)
    {
        super(environment);
        this.defaultRepoUri = defaultRepoUri;
    }


    /**
     * Initializes an additional default repository
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        super.afterPropertiesSet();

        this.globalRepository = createDefaultRepository(defaultRepoUri);
    }


    // Copied from MultipleJGitEnvironmentRepository as it was private
    private JGitEnvironmentRepository createDefaultRepository(String uri)
    {
        JGitEnvironmentRepository repository = new JGitEnvironmentRepository(null);
        File basedir = repository.getBasedir();
        BeanUtils.copyProperties(this, repository);
        repository.setUri(uri);
        repository.setBasedir(
            new File(this.getBasedir().getParentFile(), basedir.getName()));
        return repository;
    }


    /**
     * Returns an aggregated Environment with the property sources from the (name) specific repo
     * and a globally shared repository
     *
     * @param application
     * @param profile
     * @param label
     * @return Aggregated Environment
     */
    @Override
    public Environment findOne(String application, String profile, String label)
    {
        final Environment globalEnvironment = this.globalRepository.findOne(application, profile, getDefaultLabel());
        final Environment aggregatedEnvironment = super.findOne(application, profile, label);
        aggregatedEnvironment.addAll(globalEnvironment.getPropertySources());
        return aggregatedEnvironment;
    }
}
