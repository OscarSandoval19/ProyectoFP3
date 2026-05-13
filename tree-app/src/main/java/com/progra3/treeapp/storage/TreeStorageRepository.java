package com.progra3.treeapp.storage;

import com.progra3.treeengine.dto.NodeDTO;

import java.util.List;
import java.util.Optional;

public interface TreeStorageRepository {

    NodeDTO save(NodeDTO node);

    Optional<NodeDTO> findById(String id);

    List<NodeDTO> findAll();

    List<NodeDTO> findByParentId(String parentId);

    void deleteById(String id);
}