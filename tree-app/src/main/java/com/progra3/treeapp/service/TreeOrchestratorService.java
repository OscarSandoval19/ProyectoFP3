package com.progra3.treeapp.service;

import com.progra3.treeapp.storage.TreeStorageRepository;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TreeOrchestratorService {

    private final TreeStorageRepository treeStorageRepository;

    public TreeOrchestratorService(TreeStorageRepository treeStorageRepository) {
        this.treeStorageRepository = treeStorageRepository;
    }

    public List<NodeDTO> findAll() {
        return treeStorageRepository.findAll();
    }

    public Optional<NodeDTO> findById(String id) {
        return treeStorageRepository.findById(id);
    }

    public NodeDTO createNode(String name, String type, String parentId) {
        String id = UUID.randomUUID().toString();
        NodeDTO node = new NodeDTO(id, name, type, parentId);
        return treeStorageRepository.save(node);
    }

    public NodeDTO createRoot(String name, String type) {
        String id = UUID.randomUUID().toString();
        NodeDTO node = new NodeDTO(id, name, type, null);
        return treeStorageRepository.save(node);
    }

    public void deleteNode(String id) {
        deleteChildren(id);
        treeStorageRepository.deleteById(id);
    }

    private void deleteChildren(String parentId) {
        List<NodeDTO> children = treeStorageRepository.findByParentId(parentId);

        for (NodeDTO child : children) {
            deleteChildren(child.id());
            treeStorageRepository.deleteById(child.id());
        }
    }
}