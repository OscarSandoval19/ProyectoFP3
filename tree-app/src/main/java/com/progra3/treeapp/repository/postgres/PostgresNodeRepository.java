package com.progra3.treeapp.repository.postgres;

import com.progra3.treeapp.model.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresNodeRepository extends JpaRepository<NodeEntity, String> {
    // Hereda todos los métodos de persistencia (save, find, delete)
}
