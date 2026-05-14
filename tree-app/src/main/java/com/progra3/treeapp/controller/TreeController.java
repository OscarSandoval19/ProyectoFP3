package com.progra3.treeapp.controller;

import com.progra3.treeapp.service.TreeOrchestratorService;
import com.progra3.treeengine.dto.NodeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nodes")
@CrossOrigin(origins = "*")
public class TreeController {

    private final TreeOrchestratorService treeOrchestratorService;

    public TreeController(TreeOrchestratorService treeOrchestratorService) {
        this.treeOrchestratorService = treeOrchestratorService;
    }

    @GetMapping
    public List<NodeDTO> findAll() {
        return treeOrchestratorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeDTO> findById(@PathVariable String id) {
        return treeOrchestratorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NodeDTO> createNode(@RequestBody CreateNodeRequest request) {
        NodeDTO created = treeOrchestratorService.createNode(
                request.name(),
                request.type(),
                request.parentId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/root")
    public ResponseEntity<NodeDTO> createRoot(@RequestBody CreateRootRequest request) {
        NodeDTO created = treeOrchestratorService.createRoot(
                request.name(),
                request.type()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNode(@PathVariable String id) {
        treeOrchestratorService.deleteNode(id);
        return ResponseEntity.ok(Map.of("message", "Nodo eliminado correctamente"));
    }

    public record CreateNodeRequest(String name, String type, String parentId) {
    }

    public record CreateRootRequest(String name, String type) {
    }
}