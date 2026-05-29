package com.progra3.treeapp.controller;

import com.progra3.treeapp.service.TreeOrchestratorService;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/tree")
public class TreeController {

    @Autowired
    private TreeOrchestratorService treeService; 

    @PostMapping("/root")
    public ResponseEntity<NodeDTO> createRoot(@RequestBody NodeDTO dto) {
        return ResponseEntity.status(201).body(treeService.createRoot(dto));
    }

    @PostMapping("/{parentId}/children")
    public ResponseEntity<NodeDTO> addChild(@PathVariable("parentId") String parentId, @RequestBody NodeDTO dto) {
        return ResponseEntity.status(201).body(treeService.addChild(parentId, dto));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<NodeDTO>> getTree() {
        return ResponseEntity.ok(treeService.getTree());
    }

    @GetMapping("/tree/{nodeId}")
    public ResponseEntity<List<NodeDTO>> getSubtree(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeService.getSubtree(nodeId));
    }

    @GetMapping("/nodes/{nodeId}/path")
    public ResponseEntity<List<NodeDTO>> getPath(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeService.getPath(nodeId));
    }

    @GetMapping("/tree/traversal")
    public ResponseEntity<List<NodeDTO>> getTraversal(@RequestParam("type") String type) {
        if ("BFS".equalsIgnoreCase(type)) {
            return ResponseEntity.ok(treeService.getBFS());
        }
        return ResponseEntity.ok(treeService.getDFS());
    }

    @GetMapping("/tree/height")
    public ResponseEntity<Integer> getHeight() {
        return ResponseEntity.ok(treeService.getHeight());
    }

    @GetMapping("/nodes/{nodeId}/depth")
    public ResponseEntity<Integer> getDepth(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeService.getLevel(nodeId));
    }

    @GetMapping("/nodes/{nodeId}/ancestors")
    public ResponseEntity<List<NodeDTO>> getAncestors(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(treeService.getAncestors(nodeId));
    }

    @GetMapping("/tree/validate")
    public ResponseEntity<Boolean> validateTree() {
        return ResponseEntity.ok(treeService.hasCycles());
    }


    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable("id") String id) {
        treeService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }
}