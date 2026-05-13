package com.progra3.treeapp.storage;

import com.progra3.treeapp.model.NodeEntity;
import com.progra3.treeapp.repository.postgres.PostgresNodeRepository;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.storage", havingValue = "postgres")
public class PostgresTreeStorageRepository implements TreeStorageRepository {

    private final PostgresNodeRepository postgresNodeRepository;

    public PostgresTreeStorageRepository(PostgresNodeRepository postgresNodeRepository) {
        this.postgresNodeRepository = postgresNodeRepository;
    }

    @Override
    public NodeDTO save(NodeDTO node) {
        NodeEntity entity = toEntity(node);
        NodeEntity saved = postgresNodeRepository.save(entity);
        return toDto(saved);
    }

    @Override
    public Optional<NodeDTO> findById(String id) {
        return postgresNodeRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<NodeDTO> findAll() {
        return postgresNodeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<NodeDTO> findByParentId(String parentId) {
        return postgresNodeRepository.findAll()
                .stream()
                .filter(node -> parentId == null
                        ? node.getParentId() == null
                        : parentId.equals(node.getParentId()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        postgresNodeRepository.deleteById(id);
    }

    private NodeEntity toEntity(NodeDTO node) {
        return new NodeEntity(
                node.id(),
                node.name(),
                node.type(),
                node.parentId()
        );
    }

    private NodeDTO toDto(NodeEntity entity) {
        return new NodeDTO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getParentId()
        );
    }
}