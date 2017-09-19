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

package com.github.felixoldenburg.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.EnvironmentController;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A configuration controller serving at /config for non Spring Config Clients
 *
 * To provide plain json, yaml, or properties under
 *    /config/<name>/<profiles>[.json|.yaml|.yml|.properties]
 * this controller delegates to the EnvironmentController as these endpoints are originally reserved
 * for resolving Spring Environment objects.
 * The original environment endpoints can still be found at ${spring.cloud.config.server.prefix}.
 *
 *
 * @author f.oldenburg
 */
@RestController
@RequestMapping(method = RequestMethod.GET, path = "/config")
public class ConfigController
{
    EnvironmentController delegate;


    public ConfigController(EnvironmentRepository repository)
    {
        this(repository, new ObjectMapper());
    }


    @Autowired
    public ConfigController(
        EnvironmentRepository repository,
        ObjectMapper objectMapper)
    {
        this.delegate = new EnvironmentController(repository, objectMapper);
    }

    @RequestMapping("{name}/{profiles}.json")
    public ResponseEntity<String> jsonProperties(
        @PathVariable String name,
        @PathVariable String profiles,
        @RequestParam(defaultValue = "true") boolean resolvePlaceholders)
        throws Exception
    {
        return this.delegate.labelledJsonProperties(name, profiles, null, resolvePlaceholders);
    }


    @RequestMapping("/{name}/{profiles}.properties")
    public ResponseEntity<String> properties(
        @PathVariable String name,
        @PathVariable String profiles,
        @RequestParam(defaultValue = "true") boolean resolvePlaceholders)
        throws IOException
    {
        return this.delegate.labelledProperties(name, profiles, null, resolvePlaceholders);
    }


    @RequestMapping({"/{name}/{profiles}.yml", "/{name}/{profiles}.yaml"})
    public ResponseEntity<String> yaml(
        @PathVariable String name,
        @PathVariable String profiles,
        @RequestParam(defaultValue = "true") boolean resolvePlaceholders)
        throws Exception
    {
        return this.delegate.labelledYaml(name, profiles, null, resolvePlaceholders);
    }
}
