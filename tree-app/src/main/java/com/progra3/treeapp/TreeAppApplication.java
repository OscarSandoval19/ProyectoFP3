package com.progra3.treeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class TreeAppApplication {

    
    @Configuration
    @Profile("mongo")
    @EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class, 
        HibernateJpaAutoConfiguration.class
    })
    public static class MongoEnvConfig {}

  
    @Configuration
    @Profile({"postgres", "memory"})
    @EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class, 
        MongoDataAutoConfiguration.class
    })
    public static class RelationalEnvConfig {}

    public static void main(String[] args) {
        SpringApplication.run(TreeAppApplication.class, args);
    }
}