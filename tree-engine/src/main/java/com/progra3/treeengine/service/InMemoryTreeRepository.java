package com.progra3.treeengine.service;

import java.util.*;

public class InMemoryTreeRepository implements TreeAlgorithmStrategy<Node> {

    private Node root;
    private Map<String, Node> nodeMap = new HashMap<>(); 

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
            parent.getChildren().remove(node);
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
        return new ArrayList<>(nodeMap.values());
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
    public List<Node> getLeaves() {
        List<Node> leaves = new ArrayList<>();
        for (Node n : nodeMap.values()) {
            if (n.getChildren().isEmpty()) leaves.add(n);
        }
        return leaves;
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
    public boolean hasCycles() {

        return false;
    }
}