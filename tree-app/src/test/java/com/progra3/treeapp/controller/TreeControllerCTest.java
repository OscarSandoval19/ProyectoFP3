package com.progra3.treeapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TreeControllerCTest {

    @Autowired
    private MockMvc mockMvc;
   
  
    @Test
    public void test1_getDFS_ShouldReturnTraversalList() throws Exception {
        mockMvc.perform(get("/tree/traversal")
                .param("type", "DFS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

   
    @Test
    public void test2_getBFS_ShouldReturnTraversalList() throws Exception {
        mockMvc.perform(get("/tree/traversal")
                .param("type", "BFS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    
    @Test
    public void test3_getHeight_ShouldReturnInteger() throws Exception {
        mockMvc.perform(get("/tree/height"))
                .andExpect(status().isOk());
    }

 
    @Test
    public void test4_validateTree_ShouldReturnBoolean() throws Exception {
        mockMvc.perform(get("/tree/validate"))
                .andExpect(status().isOk());
    }
}