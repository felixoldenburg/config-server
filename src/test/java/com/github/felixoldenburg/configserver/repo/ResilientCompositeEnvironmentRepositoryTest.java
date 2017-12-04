package com.github.felixoldenburg.configserver.repo;

import com.github.felixoldenburg.configserver.repo.ResilientCompositeEnvironmentRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//    classes = ConfigServerApplication.class)
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = ResilientCompositeEnvironmentRepositoryTest.TestConfiguration.class)
public class ResilientCompositeEnvironmentRepositoryTest
{
    EnvironmentRepository repo1 = Mockito.mock(EnvironmentRepository.class);
    EnvironmentRepository repo2 = Mockito.mock(EnvironmentRepository.class);

    @Test
    public void whenGetCompositeConfiguration_givenOnlyOneEmptyEnvironment_thenDontFail() throws Exception
    {
        when(repo1.findOne(anyString(), anyString(), anyString())).thenReturn(new Environment("myservice", "live"));
        when(repo2.findOne(anyString(), anyString(), anyString())).thenReturn(null);
        //            Mockito.given(repository.findOne(anyString(), anyString(), anyString())).willReturn(new Environment("", ""));

        final ResilientCompositeEnvironmentRepository dut = new ResilientCompositeEnvironmentRepository(Lists.newArrayList(
            repo2,
            repo1));

        final Environment environment = dut.findOne("myservice", "live", null);

        Assert.assertNotNull(environment);
        Assert.assertEquals(0, environment.getPropertySources().size());

        Mockito.verify(repo1).findOne("myservice", "live", null);
        Mockito.verify(repo2).findOne("myservice", "live", null);
    }


    @Test
    public void whenGetCompositeConfiguration_givenOneEmptyEnvironmentThrowsExceptions_thenDontFail() throws Exception
    {
        when(repo1.findOne(anyString(), anyString(), anyString())).thenThrow(RuntimeException.class);
        when(repo2.findOne(anyString(), anyString(), anyString())).thenReturn(null);
        //            Mockito.given(repository.findOne(anyString(), anyString(), anyString())).willReturn(new Environment("", ""));

        final ResilientCompositeEnvironmentRepository dut = new ResilientCompositeEnvironmentRepository(Lists.newArrayList(
            repo1,
            repo2));

        final Environment environment = dut.findOne("myservice", "live", null);

        Assert.assertNotNull(environment);
        Assert.assertEquals(0, environment.getPropertySources().size());

        Mockito.verify(repo1).findOne("myservice", "live", null);
        Mockito.verify(repo2).findOne("myservice", "live", null);
    }

    @Test
    public void whenGetCompositeConfiguration_givenOnlyOneEnvironmentHasPropertrySource_thenDontFail() throws Exception
    {
        final Environment envWithPropSource = new Environment("mytservice", "live", null);
        envWithPropSource.add(new PropertySource("PropSource", Maps.newHashMap()));
        when(repo1.findOne(anyString(), anyString(), anyString())).thenReturn(envWithPropSource);
        when(repo2.findOne(anyString(), anyString(), anyString())).thenReturn(new Environment("myservice", "live"));
        //            Mockito.given(repository.findOne(anyString(), anyString(), anyString())).willReturn(new Environment("", ""));

        final ResilientCompositeEnvironmentRepository dut = new ResilientCompositeEnvironmentRepository(Lists.newArrayList(
            repo2,
            repo1));

        final Environment environment = dut.findOne("myservice", "live", null);

        Assert.assertNotNull(environment);
        Assert.assertEquals(1, environment.getPropertySources().size());

        Mockito.verify(repo1).findOne("myservice", "live", null);
        Mockito.verify(repo2).findOne("myservice", "live", null);
    }
}
