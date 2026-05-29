package com.progra3.treeapp.repository.postgres;

import com.progra3.treeapp.model.NodeEntity;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("postgres")
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
	    name = "app.storage", havingValue = "postgres"
	)
public interface PostgresNodeRepository extends JpaRepository<NodeEntity, String> {
	 List<NodeEntity> findByParentId(String parentId);
    // Hereda todos los métodos de persistencia (save, find, delete)
}