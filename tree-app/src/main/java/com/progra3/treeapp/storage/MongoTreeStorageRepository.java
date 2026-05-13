package com.progra3.treeapp.storage;

import com.progra3.treeapp.model.MongoNodeDocument;
import com.progra3.treeapp.repository.mongo.MongoNodeRepository;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.storage", havingValue = "mongo")
public class MongoTreeStorageRepository implements TreeStorageRepository {

    private final MongoNodeRepository mongoNodeRepository;

    public MongoTreeStorageRepository(MongoNodeRepository mongoNodeRepository) {
        this.mongoNodeRepository = mongoNodeRepository;
    }

    @Override
    public NodeDTO save(NodeDTO node) {
        MongoNodeDocument document = toDocument(node);
        MongoNodeDocument saved = mongoNodeRepository.save(document);
        return toDto(saved);
    }

    @Override
    public Optional<NodeDTO> findById(String id) {
        return mongoNodeRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<NodeDTO> findAll() {
        return mongoNodeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<NodeDTO> findByParentId(String parentId) {
        return mongoNodeRepository.findByParentId(parentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoNodeRepository.deleteById(id);
    }

    private MongoNodeDocument toDocument(NodeDTO node) {
        return new MongoNodeDocument(
                node.id(),
                node.name(),
                node.type(),
                node.parentId()
        );
    }

    private NodeDTO toDto(MongoNodeDocument document) {
        return new NodeDTO(
                document.getId(),
                document.getName(),
                document.getType(),
                document.getParentId()
        );
    }
}