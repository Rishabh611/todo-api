package org.example.service;

import org.example.dto.TodoRequest;
import org.example.entity.Todo;
import org.example.entity.User;
import org.example.repository.TodoRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    void setupSecurityContext() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test@example.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldCreateTodoSuccessfully() {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("title");
        todoRequest.setDescription("description");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("test user");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        todoService.createTodo(todoRequest);

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(captor.capture());

        Todo savedTodo = captor.getValue();

        assertAll(
                () -> assertEquals("title", savedTodo.getTitle()),
                () -> assertEquals("description", savedTodo.getDescription()),
                () -> assertEquals(user, savedTodo.getUser())
        );
    }

    @Test
    public void shouldReturnTodosForLoggedInUser() {

        User user = new User();
        user.setId(1L);
        user.setName("test user");
        user.setPassword("password123");
        user.setRole("USER");
        user.setEmail("test@example.com");

        Todo todo = new Todo();
        todo.setUser(user);
        todo.setTitle("title");
        todo.setDescription("description");

        Page<Todo> todos = new PageImpl<>(List.of(todo));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(todoRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(todos);

        Page<Todo> expected = todoService.getTodos();

        assertAll(
                () -> assertEquals(1, expected.getTotalElements()),
                () -> assertEquals("title", expected.getContent().get(0).getTitle())
        );
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingTodoNotOwnedByUser() {
        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setTitle("updated title");
        todoRequest.setDescription("description");

        User user = new User();
        user.setName("test user");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(todoRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> todoService.updateTodo(1L, todoRequest));
    }

    @Test
    public void shouldThrowExceptionWhenDeletingTodoNotOwnedByUser() {
        User user = new User();
        user.setId(1L);
        user.setName("test user");
        user.setPassword("password123");
        user.setRole("USER");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(todoRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> todoService.deleteTodo(1L));
    }
}
