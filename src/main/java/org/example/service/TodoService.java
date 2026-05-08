package org.example.service;

import org.example.dto.TodoRequest;
import org.example.entity.Todo;
import org.example.entity.User;
import org.example.repository.TodoRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<Todo> getTodos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return todoRepository.findByUserId(userId, PageRequest.of(0, 10, Sort.by("title")));
    }

    public Todo createTodo(TodoRequest todoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setUser(user);
        todoRepository.save(todo);

        return todo;
    }

    public Todo updateTodo(Long id, TodoRequest todoRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not Found"));
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todoRepository.save(todo);

        return todo;
    }

    public void deleteTodo(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not Found"));
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new RuntimeException("Todo not found"));

        todoRepository.delete(todo);
    }
}
