package com.gg.specification.controller;

import com.gg.specification.entity.Task;
import com.gg.specification.entity.User;
import com.gg.specification.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public ResponseEntity<Page<User>> getUsers(){
    	Page<User> list =  userService.getUsers();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/getUsers1")
    public ResponseEntity<Page<User>> getUsers1(){
    	Page<User> list =  userService.getUsers1();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/getTasks")
    public ResponseEntity<Page<Task>> getTasks(){
    	Page<Task> list =  userService.getTasks();
        return ResponseEntity.ok(list);
    }
}
