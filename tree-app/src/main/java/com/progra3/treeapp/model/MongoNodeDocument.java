package com.progra3.treeapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nodes")
public class MongoNodeDocument {

    @Id
    private String id;
    private String name;
    private String type;
    private String parentId;

    public MongoNodeDocument() {}

    public MongoNodeDocument(String id, String name, String type, String parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
