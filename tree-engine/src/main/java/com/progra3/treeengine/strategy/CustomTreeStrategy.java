package com.progra3.treeengine.strategy;

import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeengine.service.TreeAlgorithmStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@Component
@ConditionalOnProperty(name = "app.tree-strategy", havingValue = "custom")
public class CustomTreeStrategy implements TreeAlgorithmStrategy<NodeDTO> {

    private CustomNode root;
    private final CustomNodeIndex index = new CustomNodeIndex();

    
    private static class CustomNode {
        final NodeDTO data;
        CustomNode firstChild;   
        CustomNode nextSibling;  

        CustomNode(NodeDTO data) { 
            this.data = data; 
            this.firstChild = null;
            this.nextSibling = null;
        }
    }

  
    private static class CustomNodeIndex {
        private static class IndexEntry {
            CustomNode node;
            IndexEntry next;
            IndexEntry(CustomNode node, IndexEntry next) {
                this.node = node;
                this.next = next;
            }
        }

        private IndexEntry head = null;

        void put(CustomNode node) {
            head = new IndexEntry(node, head);
        }

        CustomNode get(String id) {
            IndexEntry current = head;
            while (current != null) {
                if (current.node.data.id().equals(id)) {
                    return current.node;
                }
                current = current.next;
            }
            return null;
        }

        void remove(String id) {
            if (head == null) return;
            if (head.node.data.id().equals(id)) {
                head = head.next;
                return;
            }
            IndexEntry current = head;
            while (current.next != null) {
                if (current.next.node.data.id().equals(id)) {
                    current.next = current.next.next;
                    return;
                }
                current = current.next;
            }
        }

        void clear() {
            head = null;
        }

        List<CustomNode> all() {
            List<CustomNode> list = new ArrayList<>();
            IndexEntry current = head;
            while (current != null) {
                list.add(current.node);
                current = current.next;
            }
            return list;
        }
    }

    @Override
    public void createRoot(NodeDTO data) {
        index.clear();
        this.root = new CustomNode(data);
        index.put(root);
    }

    @Override
    public void insert(String parentId, NodeDTO data) {
        CustomNode parent = index.get(parentId);
        if (parent == null) {
            throw new IllegalArgumentException("El nodo padre con ID " + parentId + " no existe.");
        }
        CustomNode newNode = new CustomNode(data);
        index.put(newNode);

        if (parent.firstChild == null) {
            parent.firstChild = newNode;
        } else {
            CustomNode current = parent.firstChild;
            while (current.nextSibling != null) {
                current = current.nextSibling;
            }
            current.nextSibling = newNode;
        }
    }

    @Override
    public void deleteNode(String id) {
        if (root != null && root.data.id().equals(id)) {
            root = null;
            index.clear();
            return;
        }

        CustomNode target = index.get(id);
        if (target == null) return;

       
        CustomNode parent = index.get(target.data.parentId());
        if (parent != null) {
            if (parent.firstChild == target) {
                parent.firstChild = target.nextSibling;
            } else {
                CustomNode current = parent.firstChild;
                while (current != null && current.nextSibling != target) {
                    current = current.nextSibling;
                }
                if (current != null) {
                    current.nextSibling = target.nextSibling;
                }
            }
        }
        removeRecursive(target);
    }

    private void removeRecursive(CustomNode node) {
        if (node == null) return;
        index.remove(node.data.id());
        
        CustomNode child = node.firstChild;
        while (child != null) {
            CustomNode next = child.nextSibling;
            removeRecursive(child);
            child = next;
        }
    }

    @Override
    public NodeDTO findNode(String id) {
        CustomNode node = index.get(id);
        return node != null ? node.data : null;
    }

    @Override
    public List<NodeDTO> getTree() {
        List<NodeDTO> result = new ArrayList<>();
        buildFlatList(root, result);
        return result;
    }

    private void buildFlatList(CustomNode node, List<NodeDTO> result) {
        if (node == null) return;
        result.add(node.data);
        CustomNode child = node.firstChild;
        while (child != null) {
            buildFlatList(child, result);
            child = child.nextSibling;
        }
    }

    @Override
    public List<NodeDTO> getSubtree(String nodeId) {
        List<NodeDTO> result = new ArrayList<>();
        CustomNode startNode = index.get(nodeId);
        buildFlatList(startNode, result);
        return result;
    }

    @Override
    public List<NodeDTO> getPath(String nodeId) {
        CustomNode node = index.get(nodeId);
        if (node == null) return Collections.emptyList();
        Deque<NodeDTO> path = new ArrayDeque<>();
        CustomNode current = node;
        while (current != null) {
            path.addFirst(current.data);
            current = index.get(current.data.parentId());
        }
        return new ArrayList<>(path);
    }

    @Override
    public List<NodeDTO> getDFS() {
        List<NodeDTO> result = new ArrayList<>();
        dfsTraversal(root, result);
        return result;
    }

    private void dfsTraversal(CustomNode node, List<NodeDTO> result) {
        if (node == null) return;
        result.add(node.data);
        CustomNode child = node.firstChild;
        while (child != null) {
            dfsTraversal(child, result);
            child = child.nextSibling;
        }
    }

    @Override
    public List<NodeDTO> getBFS() {
        if (root == null) return Collections.emptyList();
        List<NodeDTO> result = new ArrayList<>();
        Queue<CustomNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            CustomNode current = queue.poll();
            result.add(current.data);
            CustomNode child = current.firstChild;
            while (child != null) {
                queue.add(child);
                child = child.nextSibling;
            }
        }
        return result;
    }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(CustomNode node) {
        if (node == null) return 0;
        int maxHeight = 0;
        CustomNode child = node.firstChild;
        while (child != null) {
            maxHeight = Math.max(maxHeight, calculateHeight(child));
            child = child.nextSibling;
        }
        return 1 + maxHeight;
    }

    @Override
    public int getLevel(String id) {
        CustomNode node = index.get(id);
        if (node == null) return 0;
        int level = 0;
        CustomNode current = node;
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
        for (String v : visited) {
            if (v.equals(node.data.id())) return true;
        }
        visited.add(node.data.id());
        CustomNode child = node.firstChild;
        while (child != null) {
            if (detectCycle(child, visited)) return true;
            child = child.nextSibling;
        }
        visited.remove(visited.size() - 1);
        return false;
    }

    @Override
    public List<NodeDTO> getLeaves() {
        List<NodeDTO> leaves = new ArrayList<>();
        for (CustomNode node : index.all()) {
            if (node.firstChild == null) {
                leaves.add(node.data);
            }
        }
        return leaves;
    }
}