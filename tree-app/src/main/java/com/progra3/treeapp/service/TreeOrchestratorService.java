package com.progra3.treeapp.service;

import com.progra3.treeapp.model.MongoNodeDocument;
import com.progra3.treeapp.model.NodeEntity;
import com.progra3.treeapp.storage.TreeStorageRepository;
import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeapp.repository.mongo.MongoNodeRepository;
import com.progra3.treeapp.repository.postgres.PostgresNodeRepository;
import com.progra3.treeengine.service.TreeAlgorithmStrategy;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreeOrchestratorService {

    @Autowired(required = false)
    private PostgresNodeRepository PostgresNodeRepository;

    @Autowired(required = false)
    private MongoNodeRepository mongoRepository;

    @Autowired
    private final TreeAlgorithmStrategy<NodeDTO> strategy;
    private final TreeStorageRepository storage;

    public TreeOrchestratorService(TreeAlgorithmStrategy<NodeDTO> strategy,
                                   TreeStorageRepository storage) {
        this.strategy = strategy;
        this.storage = storage;
    }

    public NodeDTO createRoot(NodeDTO nodeDTO) {
     
        if (PostgresNodeRepository != null) {
            NodeEntity entity = new NodeEntity(nodeDTO.id(), nodeDTO.name(), nodeDTO.type(), nodeDTO.parentId());
            PostgresNodeRepository.save(entity);
        } else if (mongoRepository != null) {
            MongoNodeDocument doc = new MongoNodeDocument(nodeDTO.id(), nodeDTO.name(), nodeDTO.type(), nodeDTO.parentId());
            mongoRepository.save(doc);
        }

        
        if (nodeDTO.parentId() == null || nodeDTO.parentId().equals("null") || nodeDTO.parentId().trim().isEmpty()) {
            strategy.createRoot(nodeDTO);
        } else {
            strategy.insert(nodeDTO.parentId(), nodeDTO);
        }
        
        this.loadFromStorage();
        return nodeDTO;
    }

    @PostConstruct
    public void init() {
        loadFromStorage();
    }

    private void loadFromStorage() {
      
        if (mongoRepository != null) {
            List<MongoNodeDocument> allNodes = mongoRepository.findAll();
          
            List<NodeDTO> roots = allNodes.stream()
                .filter(n -> n.getParentId() == null 
                        || n.getParentId().trim().isEmpty() 
                        || n.getParentId().equalsIgnoreCase("null"))
                .map(n -> new NodeDTO(n.getId(), n.getName(), n.getType(), n.getParentId()))
                .toList();

            for (NodeDTO root : roots) {
                try {
                    strategy.createRoot(root);
                } catch (Exception e) {
                   
                }
                loadAllChildrenRecursivelyMongo(root.id(), allNodes);
            }
        } 
     
        else if (PostgresNodeRepository != null) {
            List<NodeEntity> allEntities = PostgresNodeRepository.findAll();
            System.out.println(">>> [POSTGRES] Total de filas leídas de la base de datos: " + allEntities.size());
            List<NodeDTO> roots = allEntities.stream()
                .filter(e -> e.getParentId() == null 
                        || e.getParentId().trim().isEmpty() 
                        || e.getParentId().equalsIgnoreCase("null"))
                .map(e -> new NodeDTO(e.getId(), e.getName(), e.getType(), e.getParentId()))
                .toList();

            for (NodeDTO root : roots) {
                try {
                    strategy.createRoot(root);
                } catch (Exception e) {
                    
                }
                loadAllChildrenRecursivelyPostgres(root.id(), allEntities);
            }
        }
    }

    private void loadAllChildrenRecursivelyMongo(String parentId, List<MongoNodeDocument> allNodes) {
        List<MongoNodeDocument> children = allNodes.stream()
            .filter(n -> parentId.equals(n.getParentId()))
            .toList();

        for (MongoNodeDocument child : children) {
            NodeDTO childDto = new NodeDTO(child.getId(), child.getName(), child.getType(), child.getParentId());
            try {
                if (strategy.findNode(child.getId()) == null) {
                    strategy.insert(parentId, childDto);
                }
            } catch (Exception e) {
               
            }
            loadAllChildrenRecursivelyMongo(child.getId(), allNodes);
        }
    }

   
    private void loadAllChildrenRecursivelyPostgres(String parentId, List<NodeEntity> allEntities) {
        List<NodeEntity> children = allEntities.stream()
            .filter(e -> parentId.equals(e.getParentId()))
            .toList();

        for (NodeEntity child : children) {
            NodeDTO childDto = new NodeDTO(child.getId(), child.getName(), child.getType(), child.getParentId());
            try {
                if (strategy.findNode(child.getId()) == null) {
                    strategy.insert(parentId, childDto);
                }
            } catch (Exception e) {
            
            }
            loadAllChildrenRecursivelyPostgres(child.getId(), allEntities);
        }
    }

    public NodeDTO addChild(String parentId, NodeDTO nodeDTO) {
        if (PostgresNodeRepository != null) {
            NodeEntity entity = new NodeEntity(nodeDTO.id(), nodeDTO.name(), nodeDTO.type(), parentId);
            PostgresNodeRepository.save(entity);
        } else if (mongoRepository != null) {
            MongoNodeDocument doc = new MongoNodeDocument(nodeDTO.id(), nodeDTO.name(), nodeDTO.type(), parentId);
            mongoRepository.save(doc);
        } else {
            throw new RuntimeException("No hay repositorio activo configurado.");
        }

        strategy.insert(parentId, nodeDTO);
        this.loadFromStorage();
        return nodeDTO;
    }

    public NodeDTO findNode(String id) {
        return strategy.findNode(id);
    }

    public void deleteNode(String id) {
        deleteChildrenRecursive(id);
        strategy.deleteNode(id);
        storage.deleteById(id);
    }

    private void deleteChildrenRecursive(String parentId) {
        for (NodeDTO child : storage.findByParentId(parentId)) {
            deleteChildrenRecursive(child.id());
            storage.deleteById(child.id());
        }
    }

    public List<NodeDTO> getTree() {
        loadFromStorage();
        return strategy.getTree();
    }

    public List<NodeDTO> getSubtree(String nodeId) {
        return strategy.getSubtree(nodeId);
    }

    public List<NodeDTO> getPath(String nodeId) {
        return strategy.getPath(nodeId);
    }

    public List<NodeDTO> getDFS() {
        return strategy.getDFS();
    }

    public List<NodeDTO> getBFS() {
        return strategy.getBFS();
    }

    public int getHeight() {
        return strategy.getHeight();
    }

    public int getLevel(String nodeId) {
        return strategy.getLevel(nodeId);
    }

    public List<NodeDTO> getAncestors(String nodeId) {
        return strategy.getAncestors(nodeId);
    }

    public boolean hasCycles() {
        return strategy.hasCycles();
    }

    public List<NodeDTO> getLeaves() {
        return strategy.getLeaves();
    }
}