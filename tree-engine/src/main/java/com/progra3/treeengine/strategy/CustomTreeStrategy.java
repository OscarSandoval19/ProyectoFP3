package com.progra3.treeengine.strategy;

import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeengine.service.TreeAlgorithmStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

public class CustomTreeStrategy implements TreeAlgorithmStrategy<NodeDTO> {

    private CustomNode root;
    private final CustomNodeIndex index = new CustomNodeIndex();

    private static class CustomNode {
        final NodeDTO data;
        final List<CustomNode> children = new ArrayList<>();
        CustomNode(NodeDTO data) { this.data = data; }
    }

    private static class CustomNodeIndex {
        private final List<CustomNode> entries = new ArrayList<>();
        void put(CustomNode node) { entries.add(node); }
        CustomNode get(String id) {
            for (CustomNode e : entries)
                if (e.data.id().equals(id)) return e;
            return null;
        }
        void remove(String id) { entries.removeIf(e -> e.data.id().equals(id)); }
        List<CustomNode> all() { return entries; }
        void clear() { entries.clear(); }
    }

    @Override
    public void createRoot(NodeDTO data) {
        CustomNode node = new CustomNode(data);
        this.root = node;
        index.put(node);
    }

    @Override
    public void insert(String parentId, NodeDTO data) {
        CustomNode parent = index.get(parentId);
        if (parent == null)
            throw new IllegalArgumentException("El nodo padre con ID " + parentId + " no existe.");
        CustomNode node = new CustomNode(data);
        parent.children.add(node);
        index.put(node);
    }

    @Override
    public void deleteNode(String id) {
        CustomNode node = index.get(id);
        if (node == null) return;
        if (node.data.parentId() != null) {
            CustomNode parent = index.get(node.data.parentId());
            if (parent != null) parent.children.remove(node);
        } else {
            root = null;
        }
        removeRecursive(node);
    }

    private void removeRecursive(CustomNode node) {
        index.remove(node.data.id());
        for (CustomNode child : node.children) removeRecursive(child);
    }

    @Override
    public NodeDTO findNode(String id) {
        CustomNode node = index.get(id);
        return node != null ? node.data : null;
    }

    @Override
    public List<NodeDTO> getTree() {
        if (root == null) return Collections.emptyList();
        return collectPreOrder(root);
    }

    private List<NodeDTO> collectPreOrder(CustomNode node) {
        List<NodeDTO> result = new ArrayList<>();
        if (node == null) return result;
        result.add(node.data);
        for (CustomNode child : node.children) result.addAll(collectPreOrder(child));
        return result;
    }

    @Override
    public List<NodeDTO> getSubtree(String nodeId) {
        CustomNode node = index.get(nodeId);
        if (node == null) return Collections.emptyList();
        return collectPreOrder(node);
    }

    @Override
    public List<NodeDTO> getPath(String nodeId) {
        CustomNode node = index.get(nodeId);
        if (node == null) return Collections.emptyList();
        Deque<NodeDTO> path = new ArrayDeque<>();
        CustomNode current = node;
        while (current != null) {
            path.addFirst(current.data);
            if (current.data.parentId() == null) break;
            current = index.get(current.data.parentId());
        }
        return new ArrayList<>(path);
    }

    @Override
    public List<NodeDTO> getDFS() {
        List<NodeDTO> result = new ArrayList<>();
        dfsHelper(root, result);
        return result;
    }

    private void dfsHelper(CustomNode current, List<NodeDTO> result) {
        if (current == null) return;
        result.add(current.data);
        for (CustomNode child : current.children) dfsHelper(child, result);
    }

    @Override
    public List<NodeDTO> getBFS() {
        List<NodeDTO> result = new ArrayList<>();
        if (root == null) return result;
        Deque<CustomNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            CustomNode current = queue.poll();
            result.add(current.data);
            queue.addAll(current.children);
        }
        return result;
    }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(CustomNode node) {
        if (node == null || node.children.isEmpty()) return 0;
        int maxHeight = 0;
        for (CustomNode child : node.children) {
            int h = calculateHeight(child);
            if (h > maxHeight) maxHeight = h;
        }
        return 1 + maxHeight;
    }

    @Override
    public int getLevel(String id) {
        CustomNode current = index.get(id);
        int level = 0;
        while (current != null && current.data.parentId() != null) {
            level++;
            current = index.get(current.data.parentId());
        }
        return level;
    }

    @Override
    public List<NodeDTO> getAncestors(String nodeId) {
        CustomNode node = index.get(nodeId);
        if (node == null || node.data.parentId() == null) return Collections.emptyList();
        Deque<NodeDTO> ancestors = new ArrayDeque<>();
        CustomNode current = index.get(node.data.parentId());
        while (current != null) {
            ancestors.addFirst(current.data);
            if (current.data.parentId() == null) break;
            current = index.get(current.data.parentId());
        }
        return new ArrayList<>(ancestors);
    }

    @Override
    public boolean hasCycles() {
        return detectCycle(root, new ArrayList<>());
    }

    private boolean detectCycle(CustomNode node, List<String> visited) {
        if (node == null) return false;
        for (String v : visited)
            if (v.equals(node.data.id())) return true;
        visited.add(node.data.id());
        for (CustomNode child : node.children)
            if (detectCycle(child, visited)) return true;
        visited.remove(visited.size() - 1);
        return false;
    }

    @Override
    public List<NodeDTO> getLeaves() {
        List<NodeDTO> leaves = new ArrayList<>();
        for (CustomNode n : index.all())
            if (n.children.isEmpty()) leaves.add(n.data);
        return leaves;
    }
}