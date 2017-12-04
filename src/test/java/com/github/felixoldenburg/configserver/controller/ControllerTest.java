package com.github.felixoldenburg.configserver.controller;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentController;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//    classes = ConfigServerApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
//@WebMvcTest(controllers = { EnvironmentController.class})
@SpringBootTest(classes = ControllerTest.ControllerConfiguration.class)
//@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest
{
    @Autowired
    MockMvc mvc;

    @Autowired
    private EnvironmentRepository repository;


    @Test
    public void whenGetSpringconfig_thenIsOk() throws Exception
    {
        this.mvc.perform(get("/springconfig/myservice/live")).
            andExpect(status().
                isOk()).andReturn();
    }


    @Test
    public void whenGetSpringconfig_thenReturnEnvironmentObject() throws Exception
    {
        Mockito.when(this.repository.findOne("myservice", "default", null)).thenReturn(new Environment("myservice", "default"));
        this.mvc.perform(get("/springconfig/myservice/default"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("myservice"))
            .andExpect(jsonPath("$.propertySources").isArray())
        ;

        Mockito.verify(this.repository).findOne("myservice", "default", null);
    }


    @Test
    public void whenGetPlainConfigForService_thenIsOk() throws Exception
    {
        Mockito.when(this.repository.findOne("myservice", "live", null)).thenReturn(new Environment("myservice", "live"));

        this.mvc.perform(get("/config/myservice/live.json")).
            andExpect(status().isOk()).andReturn();
        this.mvc.perform(get("/config/myservice/live.yaml")).
            andExpect(status().isOk()).andReturn();
        this.mvc.perform(get("/config/myservice/live.yml")).
            andExpect(status().isOk()).andReturn();
        this.mvc.perform(get("/config/myservice/live.properties")).
            andExpect(status().isOk()).andReturn();
    }

    @Test
    public void whenGetPlainConfigForService_thenReturnConfig() throws Exception
    {
        final Environment environment = new Environment("myservice", "live");
        environment.add(new PropertySource("JsonProps", Maps.newHashMap("anna", "bob")));
        Mockito.when(this.repository.findOne("myservice", "live", null))
            .thenReturn(environment);

        this.mvc.perform(get("/config/myservice/live.json"))
            .andExpect(jsonPath("$.anna").value("bob"));
    }


    @Configuration
    @EnableWebMvc
    @Import(PropertyPlaceholderAutoConfiguration.class)
    public static class ControllerConfiguration
    {

        @Bean
        public EnvironmentRepository environmentRepository()
        {
            EnvironmentRepository repository = Mockito.mock(EnvironmentRepository.class);
            return repository;
        }


        @Bean
        public ConfigController configController()
        {
            return new ConfigController(environmentRepository());
        }


        @Bean
        public EnvironmentController controller()
        {
            return new EnvironmentController(environmentRepository());
        }

    }
}
