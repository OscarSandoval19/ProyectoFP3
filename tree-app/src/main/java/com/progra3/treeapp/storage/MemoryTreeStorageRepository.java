package com.progra3.treeapp.storage;

import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.storage", havingValue = "memory")
public class MemoryTreeStorageRepository implements TreeStorageRepository {

    private final List<NodeDTO> store = new ArrayList<>();

    @Override
    public void save(NodeDTO node) {
        store.removeIf(n -> n.id().equals(node.id()));
        store.add(node);
    }

    @Override
    public NodeDTO findById(String id) {
        for (NodeDTO n : store) {
            if (n.id().equals(id)) return n;
        }
        return null;
    }

    @Override
    public List<NodeDTO> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public List<NodeDTO> findByParentId(String parentId) {
        List<NodeDTO> result = new ArrayList<>();
        for (NodeDTO n : store) {
            if (parentId.equals(n.parentId())) result.add(n);
        }
        return result;
    }

    @Override
    public void deleteById(String id) {
        store.removeIf(n -> n.id().equals(id));
    }
}
