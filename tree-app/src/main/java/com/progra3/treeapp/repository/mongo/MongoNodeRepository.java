package com.progra3.treeapp.repository.mongo;

import com.progra3.treeapp.model.MongoNodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoNodeRepository extends MongoRepository<MongoNodeDocument, String> {

    List<MongoNodeDocument> findByParentId(String parentId);
}