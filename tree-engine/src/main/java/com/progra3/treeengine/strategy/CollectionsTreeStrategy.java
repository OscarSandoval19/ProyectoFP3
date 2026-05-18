package com.progra3.treeengine.strategy;

import com.progra3.treeengine.service.TreeAlgorithmStrategy;
import com.progra3.treeengine.dto.NodeDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Deque;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.tree-strategy", havingValue = "collections")
public class CollectionsTreeStrategy implements TreeAlgorithmStrategy<NodeDTO> {

    private final List<NodeDTO> nodes = new ArrayList<>();
    private String rootId = null;

    @Override
    public void createRoot(NodeDTO data) {
        nodes.clear();
        this.rootId = data.id();
        nodes.add(data);
    }

    @Override
    public void insert(String parentId, NodeDTO data) {
        if (findNode(parentId) == null) {
            throw new IllegalArgumentException("El nodo padre con ID " + parentId + " no existe.");
        }
        nodes.add(data);
    }

    @Override
    public void deleteNode(String id) {
        if (id.equals(rootId)) {
            nodes.clear();
            rootId = null;
            return;
        }
        NodeDTO target = findNode(id);
        if (target != null) {
            removeSubtree(id);
        }
    }

    private void removeSubtree(String id) {
        List<NodeDTO> children = getChildrenOf(id);
        for (NodeDTO child : children) {
            removeSubtree(child.id());
        }
        nodes.removeIf(node -> node.id().equals(id));
    }

    @Override
    public NodeDTO findNode(String id) {
        for (NodeDTO node : nodes) {
            if (node.id().equals(id)) {
                return node;
            }
        }
        return null;
    }

    private List<NodeDTO> getChildrenOf(String parentId) {
        List<NodeDTO> children = new ArrayList<>();
        for (NodeDTO node : nodes) {
            if (parentId.equals(node.parentId())) {
                children.add(node);
            }
        }
        return children;
    }

    @Override
    public List<NodeDTO> getTree() {
        if (rootId == null) return Collections.emptyList();
        List<NodeDTO> result = new ArrayList<>();
        buildHierarchicalList(rootId, result);
        return result;
    }

    private void buildHierarchicalList(String nodeId, List<NodeDTO> result) {
        NodeDTO current = findNode(nodeId);
        if (current == null) return;
        result.add(current);
        List<NodeDTO> children = getChildrenOf(nodeId);
        for (NodeDTO child : children) {
            buildHierarchicalList(child.id(), result);
        }
    }

    @Override
    public List<NodeDTO> getSubtree(String nodeId) {
        if (findNode(nodeId) == null) return Collections.emptyList();
        List<NodeDTO> result = new ArrayList<>();
        buildHierarchicalList(nodeId, result);
        return result;
    }

    @Override
    public List<NodeDTO> getPath(String nodeId) {
        NodeDTO node = findNode(nodeId);
        if (node == null) return Collections.emptyList();
        Deque<NodeDTO> path = new ArrayDeque<>();
        NodeDTO current = node;
        while (current != null) {
            path.addFirst(current);
            if (current.id().equals(rootId)) break;
            current = findNode(current.parentId());
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
            result.add(findNode(currentId));
            List<NodeDTO> children = getChildrenOf(currentId);
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i).id());
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
            result.add(findNode(currentId));
            List<NodeDTO> children = getChildrenOf(currentId);
            for (NodeDTO child : children) {
                queue.offer(child.id());
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
        List<NodeDTO> children = getChildrenOf(id);
        if (children.isEmpty()) return 0;
        int maxHeight = 0;
        for (NodeDTO child : children) {
            maxHeight = Math.max(maxHeight, calculateHeight(child.id()));
        }
        return maxHeight + 1;
    }

    @Override
    public int getLevel(String id) {
        if (findNode(id) == null) return -1;
        int level = 0;
        String currentId = id;
        while (currentId != null && !currentId.equals(rootId)) {
            NodeDTO node = findNode(currentId);
            if (node == null || node.parentId() == null) break;
            currentId = node.parentId();
            level++;
        }
        return level;
    }

    @Override
    public List<NodeDTO> getAncestors(String nodeId) {
        NodeDTO target = findNode(nodeId);
        if (target == null) return Collections.emptyList();
        Deque<NodeDTO> ancestors = new ArrayDeque<>();
        String currentId = target.parentId();
        while (currentId != null) {
            NodeDTO node = findNode(currentId);
            if (node == null) break;
            ancestors.addFirst(node);
            if (currentId.equals(rootId)) break;
            currentId = node.parentId();
        }
        return new ArrayList<>(ancestors);
    }

    @Override
    public boolean hasCycles() {
        List<String> visited = new ArrayList<>();
        List<String> recursionStack = new ArrayList<>();
        return checkCycleDFS(rootId, visited, recursionStack);
    }

    private boolean checkCycleDFS(String current, List<String> visited, List<String> recursionStack) {
        if (current == null) return false;
        if (recursionStack.contains(current)) return true;
        if (visited.contains(current)) return false;
        
        visited.add(current);
        recursionStack.add(current);
        
        List<NodeDTO> children = getChildrenOf(current);
        for (NodeDTO child : children) {
            if (checkCycleDFS(child.id(), visited, recursionStack)) return true;
        }
        recursionStack.remove(current);
        return false;
    }

    @Override
    public List<NodeDTO> getLeaves() {
        List<NodeDTO> leaves = new ArrayList<>();
        for (NodeDTO node : nodes) {
            if (getChildrenOf(node.id()).isEmpty()) {
                leaves.add(node);
            }
        }
        return leaves;
    }
}
