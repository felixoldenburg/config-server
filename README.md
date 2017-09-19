# Config Server

A Spring Cloud Config based configuration server for arbitrary configuration and Spring Environments alike.

## Config Sources

The config server reads its configuration from (in this order)
* Git
* ZooKeeper

On how to store yaml, json, or property (or any other file) files see the excellent Spring Cloud Config documentation 
https://github
.com/spring-cloud/spring-cloud-config

## API
#### Spring Environment
For Spring Cloud Config Clients configuration can be found at

    /springconfig/{application}/{profile}[/{label}]
    /springconfig/{application}-{profile}.yml
    /springconfig/{label}/{application}-{profile}.yml
    /springconfig/{application}-{profile}.properties
    /springconfig/{label}/{application}-{profile}.properties

### JSON, YAML and Properties

If there's configuration in any of the source for a service, it can be retrieved at

    /config/<name>/<profiles>[.json|.yaml|.yml|.properties]