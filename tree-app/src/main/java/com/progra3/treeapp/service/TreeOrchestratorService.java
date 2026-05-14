package com.progra3.treeapp.service;

import com.progra3.treeapp.storage.TreeStorageRepository;
import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeengine.service.TreeAlgorithmStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TreeOrchestratorService {

    private final TreeAlgorithmStrategy<NodeDTO> strategy;
    private final TreeStorageRepository storage;

    public TreeOrchestratorService(TreeAlgorithmStrategy<NodeDTO> strategy,
                                   TreeStorageRepository storage) {
        this.strategy = strategy;
        this.storage = storage;
        loadFromStorage();
    }

    private void loadFromStorage() {
        List<NodeDTO> all = storage.findAll();
        NodeDTO root = null;
        for (NodeDTO n : all) {
            if (n.parentId() == null) { root = n; break; }
        }
        if (root == null) return;
        strategy.createRoot(root);
        loadChildren(root.id(), all);
    }

    private void loadChildren(String parentId, List<NodeDTO> all) {
        for (NodeDTO n : all) {
            if (parentId.equals(n.parentId())) {
                strategy.insert(parentId, n);
                loadChildren(n.id(), all);
            }
        }
    }

    public NodeDTO createRoot(String name, String type) {
        NodeDTO node = new NodeDTO(UUID.randomUUID().toString(), name, type, null);
        strategy.createRoot(node);
        storage.save(node);
        return node;
    }

    public NodeDTO addChild(String parentId, String name, String type) {
        NodeDTO node = new NodeDTO(UUID.randomUUID().toString(), name, type, parentId);
        strategy.insert(parentId, node);
        storage.save(node);
        return node;
    }

    public NodeDTO findNode(String id) {
        return strategy.findNode(id);
    }

    public void deleteNode(String id) {
        deleteChildrenRecursive(id);
        strategy.deleteNode(id);
        storage.deleteById(id);
    }

    private void deleteChildrenRecursive(String parentId) {
        for (NodeDTO child : storage.findByParentId(parentId)) {
            deleteChildrenRecursive(child.id());
            storage.deleteById(child.id());
        }
    }

    public List<NodeDTO> getTree() {
        return strategy.getTree();
    }

    public List<NodeDTO> getSubtree(String nodeId) {
        return strategy.getSubtree(nodeId);
    }

    public List<NodeDTO> getPath(String nodeId) {
        return strategy.getPath(nodeId);
    }

    public List<NodeDTO> getDFS() {
        return strategy.getDFS();
    }

    public List<NodeDTO> getBFS() {
        return strategy.getBFS();
    }

    public int getHeight() {
        return strategy.getHeight();
    }

    public int getLevel(String nodeId) {
        return strategy.getLevel(nodeId);
    }

    public List<NodeDTO> getAncestors(String nodeId) {
        return strategy.getAncestors(nodeId);
    }

    public boolean hasCycles() {
        return strategy.hasCycles();
    }

    public List<NodeDTO> getLeaves() {
        return strategy.getLeaves();
    }
}
