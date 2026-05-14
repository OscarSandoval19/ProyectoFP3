package com.progra3.treeapp.repository.mongo;

import com.progra3.treeapp.model.MongoNodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MongoNodeRepository extends MongoRepository<MongoNodeDocument, String> {
    List<MongoNodeDocument> findByParentId(String parentId);
}
