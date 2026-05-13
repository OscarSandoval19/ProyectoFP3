package com.progra3.treeengine.service;

import java.util.*;

public class InMemoryTreeRepository implements TreeAlgorithmStrategy<Node> {

    private Node root;
    private final Map<String, Node> nodeMap = new HashMap<>();

    @Override
    public void createRoot(Node data) {
        this.root = data;
        nodeMap.put(data.getId(), data);
    }

    @Override
    public void insert(String parentId, Node data) {
        Node parent = nodeMap.get(parentId);
        if (parent != null) {
            parent.getChildren().add(data);
            nodeMap.put(data.getId(), data);
        }
    }

    @Override
    public void deleteNode(String id) {
        Node node = nodeMap.get(id);
        if (node != null && node.getParentId() != null) {
            Node parent = nodeMap.get(node.getParentId());
            if (parent != null) parent.getChildren().remove(node);
            removeRecursive(node);
        }
    }

    private void removeRecursive(Node node) {
        nodeMap.remove(node.getId());
        for (Node child : node.getChildren()) {
            removeRecursive(child);
        }
    }

    @Override
    public Node findNode(String id) {
        return nodeMap.get(id);
    }

    @Override
    public List<Node> getTree() {
        if (root == null) return Collections.emptyList();
        return collectPreOrder(root);
    }

    private List<Node> collectPreOrder(Node node) {
        List<Node> result = new ArrayList<>();
        if (node == null) return result;
        result.add(node);
        for (Node child : node.getChildren()) {
            result.addAll(collectPreOrder(child));
        }
        return result;
    }

    @Override
    public List<Node> getSubtree(String nodeId) {
        Node node = nodeMap.get(nodeId);
        if (node == null) return Collections.emptyList();
        return collectPreOrder(node);
    }

    @Override
    public List<Node> getPath(String nodeId) {
        Node node = nodeMap.get(nodeId);
        if (node == null) return Collections.emptyList();
        Deque<Node> path = new ArrayDeque<>();
        Node current = node;
        while (current != null) {
            path.addFirst(current);
            if (current.getParentId() == null) break;
            current = nodeMap.get(current.getParentId());
        }
        return new ArrayList<>(path);
    }

    @Override
    public List<Node> getDFS() {
        List<Node> result = new ArrayList<>();
        dfsHelper(root, result);
        return result;
    }

    private void dfsHelper(Node current, List<Node> result) {
        if (current == null) return;
        result.add(current);
        for (Node child : current.getChildren()) {
            dfsHelper(child, result);
        }
    }

    @Override
    public List<Node> getBFS() {
        List<Node> result = new ArrayList<>();
        if (root == null) return result;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.add(current);
            queue.addAll(current.getChildren());
        }
        return result;
    }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(Node node) {
        if (node == null || node.getChildren().isEmpty()) return 0;
        int maxHeight = 0;
        for (Node child : node.getChildren()) {
            maxHeight = Math.max(maxHeight, calculateHeight(child));
        }
        return 1 + maxHeight;
    }

    @Override
    public int getLevel(String id) {
        int level = 0;
        Node current = nodeMap.get(id);
        while (current != null && current.getParentId() != null) {
            level++;
            current = nodeMap.get(current.getParentId());
        }
        return level;
    }

    @Override
    public List<Node> getAncestors(String nodeId) {
        Node node = nodeMap.get(nodeId);
        if (node == null || node.getParentId() == null) return Collections.emptyList();
        Deque<Node> ancestors = new ArrayDeque<>();
        Node current = nodeMap.get(node.getParentId());
        while (current != null) {
            ancestors.addFirst(current);
            if (current.getParentId() == null) break;
            current = nodeMap.get(current.getParentId());
        }
        return new ArrayList<>(ancestors);
    }

    @Override
    public boolean hasCycles() {
        Set<String> visited = new HashSet<>();
        return detectCycle(root, visited);
    }

    private boolean detectCycle(Node node, Set<String> visited) {
        if (node == null) return false;
        if (visited.contains(node.getId())) return true;
        visited.add(node.getId());
        for (Node child : node.getChildren()) {
            if (detectCycle(child, visited)) return true;
        }
        return false;
    }

    @Override
    public List<Node> getLeaves() {
        List<Node> leaves = new ArrayList<>();
        for (Node n : nodeMap.values()) {
            if (n.getChildren().isEmpty()) leaves.add(n);
        }
        return leaves;
    }
}
