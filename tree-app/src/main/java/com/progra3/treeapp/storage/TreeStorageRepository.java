package com.progra3.treeapp.storage;

import com.progra3.treeengine.dto.NodeDTO;
import java.util.List;

public interface TreeStorageRepository {
    void save(NodeDTO node);
    NodeDTO findById(String id);
    List<NodeDTO> findAll();
    List<NodeDTO> findByParentId(String parentId);
    void deleteById(String id);
}
