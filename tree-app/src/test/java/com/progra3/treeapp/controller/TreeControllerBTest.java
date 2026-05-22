package com.progra3.treeapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TreeControllerBTest {

    @Autowired
    private MockMvc mockMvc;

    
    @Test
    public void test1_createRoot_ShouldReturnCreated() throws Exception {
        String rootJson = "{\"id\":\"root_test\",\"name\":\"Raiz Test\",\"parentId\":null,\"type\":\"FOLDER\"}";

        mockMvc.perform(post("/nodes/root")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rootJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("root_test"))
                .andExpect(jsonPath("$.name").value("Raiz Test"));
    }

   
    @Test
    public void test2_insertNode_ShouldAttachToParent() throws Exception {
        String childJson = "{\"id\":\"child_test\",\"name\":\"Hijo Test\",\"parentId\":\"root_test\",\"type\":\"FILE\"}";

        
        mockMvc.perform(post("/nodes/{parentId}/children", "root_test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(childJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("child_test"))
                .andExpect(jsonPath("$.parentId").value("root_test"));
    }


    @Test
    public void test3_getTree_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/tree"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

 
    @Test
    public void test4_deleteNode_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/nodes/child_test"))
                .andExpect(status().isNoContent());
    }
}