package com.progra3.treeapp.controller;

import com.progra3.treeapp.service.TreeOrchestratorService;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TreeController {

    private final TreeOrchestratorService service;

    public TreeController(TreeOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/nodes/root")
    public ResponseEntity<NodeDTO> createRoot(@RequestBody NodeDTO request) {
        NodeDTO created = service.createRoot(request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/nodes/{parentId}/children")
    public ResponseEntity<NodeDTO> addChild(@PathVariable String parentId,
                                             @RequestBody NodeDTO request) {
        NodeDTO created = service.addChild(parentId, request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<NodeDTO> findNode(@PathVariable String id) {
        NodeDTO node = service.findNode(id);
        if (node == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(node);
    }

    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable String id) {
        service.deleteNode(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree")
    public ResponseEntity<List<NodeDTO>> getTree() {
        return ResponseEntity.ok(service.getTree());
    }

    @GetMapping("/tree/{nodeId}")
    public ResponseEntity<List<NodeDTO>> getSubtree(@PathVariable String nodeId) {
        return ResponseEntity.ok(service.getSubtree(nodeId));
    }

    @GetMapping("/nodes/{nodeId}/path")
    public ResponseEntity<List<NodeDTO>> getPath(@PathVariable String nodeId) {
        return ResponseEntity.ok(service.getPath(nodeId));
    }

    @GetMapping("/tree/traversal")
    public ResponseEntity<List<NodeDTO>> traversal(@RequestParam String type) {
        if ("DFS".equalsIgnoreCase(type)) return ResponseEntity.ok(service.getDFS());
        if ("BFS".equalsIgnoreCase(type)) return ResponseEntity.ok(service.getBFS());
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/tree/height")
    public ResponseEntity<Integer> getHeight() {
        return ResponseEntity.ok(service.getHeight());
    }

    @GetMapping("/nodes/{nodeId}/depth")
    public ResponseEntity<Integer> getDepth(@PathVariable String nodeId) {
        return ResponseEntity.ok(service.getLevel(nodeId));
    }

    @GetMapping("/nodes/{nodeId}/ancestors")
    public ResponseEntity<List<NodeDTO>> getAncestors(@PathVariable String nodeId) {
        return ResponseEntity.ok(service.getAncestors(nodeId));
    }

    @GetMapping("/tree/validate")
    public ResponseEntity<Boolean> validate() {
        return ResponseEntity.ok(!service.hasCycles());
    }
}
