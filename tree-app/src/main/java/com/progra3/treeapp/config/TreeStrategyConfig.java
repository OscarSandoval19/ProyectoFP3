package com.progra3.treeapp.config;

import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeengine.service.TreeAlgorithmStrategy;
import com.progra3.treeengine.service.InMemoryTreeRepository;
import com.progra3.treeengine.strategy.CollectionsTreeStrategy;
import com.progra3.treeengine.strategy.CustomTreeStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TreeStrategyConfig {

    @Bean
    @ConditionalOnProperty(name = "app.tree-strategy", havingValue = "collections", matchIfMissing = true)
    public TreeAlgorithmStrategy<NodeDTO> collectionsStrategy() {
        return new CollectionsTreeStrategy();
    }

    @Bean
    @ConditionalOnProperty(name = "app.tree-strategy", havingValue = "custom")
    public TreeAlgorithmStrategy<NodeDTO> customStrategy() {
        return new CustomTreeStrategy();
    }
}
