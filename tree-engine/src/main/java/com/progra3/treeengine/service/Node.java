package com.progra3.treeengine.service;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private String name;
    private String type; 
    private String parentId;
    private List<Node> children;

    public Node(String id, String name, String type, String parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.children = new ArrayList<>();
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public List<Node> getChildren() { return children; }
    public String getParentId() { return parentId; }
    public String getType() { return type; }
}