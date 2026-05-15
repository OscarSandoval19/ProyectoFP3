package com.progra3.treeapp.storage;

import com.progra3.treeapp.model.NodeEntity;
import com.progra3.treeapp.repository.postgres.PostgresNodeRepository;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.storage", havingValue = "postgres")
public class PostgresTreeStorageRepository implements TreeStorageRepository {

    private final PostgresNodeRepository repo;

    public PostgresTreeStorageRepository(PostgresNodeRepository repo) {
        this.repo = repo;
    }

    private NodeDTO toDTO(NodeEntity e) {
        return new NodeDTO(e.getId(), e.getName(), e.getType(), e.getParentId());
    }

    private NodeEntity toEntity(NodeDTO d) {
        return new NodeEntity(d.id(), d.name(), d.type(), d.parentId());
    }

    @Override
    public void save(NodeDTO node) {
        repo.save(toEntity(node));
    }

    @Repository
    public interface PostgresNodeRepository extends JpaRepository<NodeEntity, String> {

        List<NodeEntity> findByParentId(String parentId);
    }
    @Override
    public NodeDTO findById(String id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public List<NodeDTO> findAll() {
        List<NodeDTO> result = new ArrayList<>();
        for (NodeEntity e : repo.findAll()) {
            result.add(toDTO(e));
        }
        return result;
    }

    @Override
    public List<NodeDTO> findByParentId(String parentId) {
        List<NodeDTO> result = new ArrayList<>();
        for (NodeEntity e : repo.findByParentId(parentId)) {
            result.add(toDTO(e));
        }
        return result;
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
}
