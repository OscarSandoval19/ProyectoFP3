package com.progra3.treeengine.service;

import java.util.List;

public interface TreeAlgorithmStrategy<T> {

    void createRoot(T data);

    void insert(String parentId, T data);

    void deleteNode(String id);

    T findNode(String id);

    List<T> getTree();

    List<T> getDFS();

    List<T> getBFS();

    List<T> getLeaves();

    int getLevel(String id);

    int getHeight();

    boolean hasCycles();
}