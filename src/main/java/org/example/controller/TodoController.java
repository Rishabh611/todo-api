package org.example.controller;

import jakarta.validation.Valid;
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
    public Todo createTodo(@RequestBody @Valid TodoRequest request) {
        return todoService.createTodo(request);
    }

    @PatchMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody @Valid TodoRequest request) {
        return todoService.updateTodo(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }
}
