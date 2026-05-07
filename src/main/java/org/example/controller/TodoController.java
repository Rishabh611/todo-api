package org.example.controller;

import org.example.dto.TodoRequest;
import org.example.entity.Todo;
import org.example.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/todos")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping()
    public Page<Todo> getTodos() {
        return todoService.getTodos();
    }

    @PostMapping()
    public void createTodo(@RequestBody TodoRequest request) {
        todoService.createTodo(request);
    }
}
