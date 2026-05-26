package com.progra3.treeapp.storage;

import com.progra3.treeapp.model.MongoNodeDocument;
import com.progra3.treeapp.repository.mongo.MongoNodeRepository;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.storage", havingValue = "mongo")
public class MongoTreeStorageRepository implements TreeStorageRepository {

    private final MongoNodeRepository repo;

    public MongoTreeStorageRepository(MongoNodeRepository repo) {
        this.repo = repo;
    }

    private NodeDTO toDTO(MongoNodeDocument d) {
        return new NodeDTO(d.getId(), d.getName(), d.getType(), d.getParentId());
    }

    private MongoNodeDocument toDocument(NodeDTO d) {
        return new MongoNodeDocument(d.id(), d.name(), d.type(), d.parentId());
    }

    @Override
    public void save(NodeDTO node) {
        repo.save(toDocument(node));
    }

    @Override
    public NodeDTO findById(String id) {
        return repo.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public List<NodeDTO> findAll() {
        List<NodeDTO> result = new ArrayList<>();
        for (MongoNodeDocument d : repo.findAll()) {
            result.add(toDTO(d));
        }
        return result;
    }

    @Override
    public List<NodeDTO> findByParentId(String parentId) {
        List<NodeDTO> result = new ArrayList<>();
        for (MongoNodeDocument d : repo.findByParentId(parentId)) {
            result.add(toDTO(d));
        }
        return result;
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
}
