package com.progra3.treeapp.controller;

import com.progra3.treeapp.service.TreeOrchestratorService;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TreeController {
 private final TreeAlgorithmStrategy<NodeDTO> treeStrategy;

    public TreeController(TreeAlgorithmStrategy<NodeDTO> treeStrategy) {
        this.treeStrategy = treeStrategy;
    }
    
    
 @PostMapping("/nodes/root")
    public ResponseEntity<NodeDTO> createRoot(@RequestBody NodeDTO data) {
        treeStrategy.createRoot(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
@PostMapping("/nodes")
    public ResponseEntity<NodeDTO> insertNode(@RequestParam String parentId, @RequestBody NodeDTO data) {
        treeStrategy.insert(parentId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

 @DeleteMapping("/nodes/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteNode(@PathVariable String id) {
    treeStrategy.deleteNode(id);
}
 
@GetMapping("/tree")
public ResponseEntity<List<NodeDTO>> getTree() {
    return ResponseEntity.ok(treeStrategy.getTree());
}


@GetMapping("/tree/subtree/{id}")
public ResponseEntity<List<NodeDTO>> getSubtree(@PathVariable String id) {
    return ResponseEntity.ok(treeStrategy.getSubtree(id));
}

@GetMapping("/tree/path/{id}")
public ResponseEntity<List<NodeDTO>> getPath(@PathVariable String id) {
    return ResponseEntity.ok(treeStrategy.getPath(id));
}
@GetMapping("/tree/traversal/dfs")
public ResponseEntity<List<NodeDTO>> getDFS() {
    return ResponseEntity.ok(treeStrategy.dfs());
}



@GetMapping("/tree/traversal/bfs")
public ResponseEntity<List<NodeDTO>> getBFS() {
    return ResponseEntity.ok(treeStrategy.bfs());
}
@GetMapping("/tree/height")
public ResponseEntity<Integer> getHeight() {
    return ResponseEntity.ok(treeStrategy.height());
}


@GetMapping("/tree/level/{id}")
public ResponseEntity<Integer> getLevel(@PathVariable String id) {
    return ResponseEntity.ok(treeStrategy.level(id));
}
@GetMapping("/tree/ancestors/{id}")
public ResponseEntity<List<NodeDTO>> getAncestors(@PathVariable String id) {
    return ResponseEntity.ok(treeStrategy.getAncestors(id));
}


@GetMapping("/tree/validate")
public ResponseEntity<Boolean> validateTree() {
 
    return ResponseEntity.ok(treeStrategy.hasCycles());
}


}

