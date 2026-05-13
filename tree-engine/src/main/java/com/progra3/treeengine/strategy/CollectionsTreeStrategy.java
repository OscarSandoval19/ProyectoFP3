package com.progra3.treeengine.strategy;

import com.progra3.treeengine.service.TreeAlgorithmStrategy;
import com.progra3.treeengine.dto.NodeDTO;

import java.util.*;

public class CollectionsTreeStrategy implements TreeAlgorithmStrategy<NodeDTO> {

    private final Map<String, NodeDTO> nodeMap = new HashMap<>();
    private final Map<String, List<String>> childrenMap = new HashMap<>();
    private String rootId = null;

    @Override
    public void createRoot(NodeDTO data) {
        this.rootId = data.id();
        nodeMap.put(data.id(), data);
        childrenMap.put(data.id(), new ArrayList<>());
    }

    @Override
    public void insert(String parentId, NodeDTO data) {
        if (!nodeMap.containsKey(parentId)) {
            throw new IllegalArgumentException("El nodo padre con ID " + parentId + " no existe.");
        }
        nodeMap.put(data.id(), data);
        childrenMap.putIfAbsent(data.id(), new ArrayList<>());
        childrenMap.get(parentId).add(data.id());
    }

    @Override
    public void deleteNode(String id) {
        if (id.equals(rootId)) {
            nodeMap.clear();
            childrenMap.clear();
            rootId = null;
            return;
        }
        NodeDTO node = nodeMap.get(id);
        if (node != null && node.parentId() != null) {
            childrenMap.get(node.parentId()).remove(id);
            removeSubtree(id);
        }
    }

    private void removeSubtree(String id) {
        List<String> children = childrenMap.remove(id);
        nodeMap.remove(id);
        if (children != null) {
            for (String childId : children) {
                removeSubtree(childId);
            }
        }
    }

    @Override
    public NodeDTO findNode(String id) {
        return nodeMap.get(id);
    }

    @Override
    public List<NodeDTO> getTree() {
        if (rootId == null) return Collections.emptyList();
        Map<String, List<NodeDTO>> hierarchyMap = new LinkedHashMap<>();
        for (String id : nodeMap.keySet()) {
            hierarchyMap.put(id, new ArrayList<>());
        }
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(rootId);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            for (String childId : childrenMap.getOrDefault(currentId, Collections.emptyList())) {
                hierarchyMap.get(currentId).add(nodeMap.get(childId));
                queue.offer(childId);
            }
        }
        return buildHierarchicalList(rootId, hierarchyMap);
    }

    private List<NodeDTO> buildHierarchicalList(String nodeId, Map<String, List<NodeDTO>> hierarchyMap) {
        List<NodeDTO> result = new ArrayList<>();
        NodeDTO current = nodeMap.get(nodeId);
        if (current == null) return result;
        result.add(current);
        for (NodeDTO child : hierarchyMap.getOrDefault(nodeId, Collections.emptyList())) {
            result.addAll(buildHierarchicalList(child.id(), hierarchyMap));
        }
        return result;
    }

    @Override
    public List<NodeDTO> getSubtree(String nodeId) {
        if (!nodeMap.containsKey(nodeId)) return Collections.emptyList();
        Map<String, List<NodeDTO>> hierarchyMap = new LinkedHashMap<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(nodeId);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            hierarchyMap.putIfAbsent(currentId, new ArrayList<>());
            for (String childId : childrenMap.getOrDefault(currentId, Collections.emptyList())) {
                hierarchyMap.get(currentId).add(nodeMap.get(childId));
                queue.offer(childId);
            }
        }
        return buildHierarchicalList(nodeId, hierarchyMap);
    }

    @Override
    public List<NodeDTO> getPath(String nodeId) {
        if (!nodeMap.containsKey(nodeId)) return Collections.emptyList();
        Deque<NodeDTO> path = new ArrayDeque<>();
        String currentId = nodeId;
        while (currentId != null) {
            NodeDTO node = nodeMap.get(currentId);
            if (node == null) break;
            path.addFirst(node);
            if (currentId.equals(rootId)) break;
            currentId = node.parentId();
        }
        return new ArrayList<>(path);
    }

    @Override
    public List<NodeDTO> getDFS() {
        List<NodeDTO> result = new ArrayList<>();
        if (rootId == null) return result;
        Deque<String> stack = new ArrayDeque<>();
        stack.push(rootId);
        while (!stack.isEmpty()) {
            String currentId = stack.pop();
            result.add(nodeMap.get(currentId));
            List<String> children = childrenMap.getOrDefault(currentId, Collections.emptyList());
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
        return result;
    }

    @Override
    public List<NodeDTO> getBFS() {
        List<NodeDTO> result = new ArrayList<>();
        if (rootId == null) return result;
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(rootId);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            result.add(nodeMap.get(currentId));
            for (String childId : childrenMap.getOrDefault(currentId, Collections.emptyList())) {
                queue.offer(childId);
            }
        }
        return result;
    }

    @Override
    public int getHeight() {
        if (rootId == null) return 0;
        return calculateHeight(rootId);
    }

    private int calculateHeight(String id) {
        List<String> children = childrenMap.getOrDefault(id, Collections.emptyList());
        if (children.isEmpty()) return 0;
        int maxHeight = 0;
        for (String childId : children) {
            maxHeight = Math.max(maxHeight, calculateHeight(childId));
        }
        return maxHeight + 1;
    }

    @Override
    public int getLevel(String id) {
        if (!nodeMap.containsKey(id)) return -1;
        int level = 0;
        String currentId = id;
        while (currentId != null && !currentId.equals(rootId)) {
            NodeDTO node = nodeMap.get(currentId);
            if (node == null || node.parentId() == null) break;
            currentId = node.parentId();
            level++;
        }
        return level;
    }

    @Override
    public List<NodeDTO> getAncestors(String nodeId) {
        if (!nodeMap.containsKey(nodeId)) return Collections.emptyList();
        Deque<NodeDTO> ancestors = new ArrayDeque<>();
        String currentId = nodeMap.get(nodeId).parentId();
        while (currentId != null) {
            NodeDTO node = nodeMap.get(currentId);
            if (node == null) break;
            ancestors.addFirst(node);
            if (currentId.equals(rootId)) break;
            currentId = node.parentId();
        }
        return new ArrayList<>(ancestors);
    }

    @Override
    public boolean hasCycles() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        return checkCycleDFS(rootId, visited, recursionStack);
    }

    private boolean checkCycleDFS(String current, Set<String> visited, Set<String> recursionStack) {
        if (current == null) return false;
        if (recursionStack.contains(current)) return true;
        if (visited.contains(current)) return false;
        visited.add(current);
        recursionStack.add(current);
        for (String child : childrenMap.getOrDefault(current, Collections.emptyList())) {
            if (checkCycleDFS(child, visited, recursionStack)) return true;
        }
        recursionStack.remove(current);
        return false;
    }

    @Override
    public List<NodeDTO> getLeaves() {
        List<NodeDTO> leaves = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : childrenMap.entrySet()) {
            if (entry.getValue().isEmpty() && nodeMap.containsKey(entry.getKey())) {
                leaves.add(nodeMap.get(entry.getKey()));
            }
        }
        return leaves;
    }
}
