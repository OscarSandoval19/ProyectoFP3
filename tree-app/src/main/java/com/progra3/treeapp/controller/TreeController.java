package com.progra3.treeapp.controller;

import com.progra3.treeengine.dto.NodeDTO;
import com.progra3.treeengine.service.TreeAlgorithmStrategy; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TreeController {

    @Autowired
    private TreeAlgorithmStrategy<NodeDTO> treeStrategy;

 
    @PostMapping("/nodes/root")
    public ResponseEntity<NodeDTO> createRoot(@RequestBody NodeDTO data) {
        treeStrategy.createRoot(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

   
    @PostMapping("/nodes/{parentId}/children")
    public ResponseEntity<NodeDTO> insertNode(@PathVariable("parentId") String parentId, @RequestBody NodeDTO data) {
        treeStrategy.insert(parentId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

  
    @GetMapping("/tree")
    public ResponseEntity<List<NodeDTO>> getTree() {
        return ResponseEntity.ok(treeStrategy.getTree());
    }

   
    @GetMapping("/tree/{nodeId}")
    public ResponseEntity<List<NodeDTO>> getSubtree(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeStrategy.getSubtree(nodeId));
    }

  
    @GetMapping("/nodes/{nodeId}/path")
    public ResponseEntity<List<NodeDTO>> getPath(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeStrategy.getPath(nodeId));
    }

  
    @GetMapping("/tree/traversal")
    public ResponseEntity<List<NodeDTO>> getTraversal(@RequestParam("type") String type) {
        if ("BFS".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(treeStrategy.getBFS());
        }
 
        return ResponseEntity.ok(treeStrategy.getDFS());
    }

   
    @GetMapping("/tree/height")
    public ResponseEntity<Integer> getHeight() {
        return ResponseEntity.ok(treeStrategy.getHeight());
    }

   
    @GetMapping("/nodes/{nodeId}/depth")
    public ResponseEntity<Integer> getDepth(@PathVariable("nodeId") String nodeId) {
        // Ejecuta de forma correcta el método getLevel de tu interfaz bajo la ruta exigida
        return ResponseEntity.ok(treeStrategy.getLevel(nodeId));
    }


    @GetMapping("/nodes/{nodeId}/ancestors")
    public ResponseEntity<List<NodeDTO>> getAncestors(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeStrategy.getAncestors(nodeId));
    }

    
   
    @GetMapping("/tree/validate")
    public ResponseEntity<Boolean> validateTree() {
        return ResponseEntity.ok(treeStrategy.hasCycles());
    }

 
    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable("id") String id) {
        treeStrategy.deleteNode(id);
        return ResponseEntity.noContent().build();
    }
}