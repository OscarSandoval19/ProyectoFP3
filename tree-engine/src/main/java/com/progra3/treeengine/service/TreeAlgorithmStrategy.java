package com.progra3.treeengine.service;

import java.util.List;

public interface TreeAlgorithmStrategy<T> {

    void createRoot(T data);

    void insert(String parentId, T data);

    void deleteNode(String id);

    T findNode(String id);

    List<T> getTree();

    List<T> getSubtree(String nodeId);

    List<T> getPath(String nodeId);

    List<T> getDFS();

    List<T> getBFS();

    int getHeight();

    int getLevel(String id);

    List<T> getAncestors(String nodeId);

    boolean hasCycles();

    List<T> getLeaves();
}
