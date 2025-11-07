package com.taskflow.taskflow.comment.service;

import com.taskflow.taskflow.comment.dto.CommentRequest;
import com.taskflow.taskflow.comment.dto.CommentResponse;
import com.taskflow.taskflow.comment.model.Comment;
import com.taskflow.taskflow.comment.repository.CommentRepository;
import com.taskflow.taskflow.core.exception.ResourceNotFoundException;
import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private CommentService commentService;

    private UserPrincipal currentUser;
    private Task task;
    private Project project;
    private Usuario user;
    private Comment comment;
    private UUID taskId;
    private UUID userId;
    private UUID projectId;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new Usuario();
        user.setId(userId);
        user.setEmail("test@test.com");
        user.setPassword("password");

        currentUser = new UserPrincipal(user);

        projectId = UUID.randomUUID();
        project = new Project();
        project.setId(projectId);

        taskId = UUID.randomUUID();
        task = new Task();
        task.setId(taskId);
        task.setProject(project);

        commentId = UUID.randomUUID();
        comment = new Comment(commentId, "Test comment", task, user, Instant.now());
    }

    @Test
    void createComment_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRequest request = new CommentRequest("Test comment");
        CommentResponse response = commentService.createComment(taskId, request, currentUser);

        assertNotNull(response);
        assertEquals(comment.getId(), response.id());
        assertEquals(comment.getContent(), response.content());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        CommentRequest request = new CommentRequest("Test comment");

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(taskId, request, currentUser);
        });
    }

    @Test
    void createComment_UserNotMember() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        CommentRequest request = new CommentRequest("Test comment");

        assertThrows(AccessDeniedException.class, () -> {
            commentService.createComment(taskId, request, currentUser);
        });
    }

    @Test
    void getCommentById_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

        CommentResponse response = commentService.getCommentById(taskId, commentId, currentUser);

        assertNotNull(response);
        assertEquals(commentId, response.id());
    }

    @Test
    void getCommentsByTaskId_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(commentRepository.findByTaskId(taskId)).thenReturn(Collections.singletonList(comment));

        List<CommentResponse> responses = commentService.getCommentsByTaskId(taskId, currentUser);

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(commentId, responses.get(0).id());
    }

    @Test
    void updateComment_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRequest request = new CommentRequest("Updated comment");
        CommentResponse response = commentService.updateComment(taskId, commentId, request, currentUser);

        assertNotNull(response);
        assertEquals("Updated comment", response.content());
    }

    @Test
    void updateComment_NotAuthor() {
        Usuario otherUserObj = new Usuario();
        otherUserObj.setId(UUID.randomUUID());
        UserPrincipal otherUser = new UserPrincipal(otherUserObj);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentRequest request = new CommentRequest("Updated comment");

        assertThrows(AccessDeniedException.class, () -> {
            commentService.updateComment(taskId, commentId, request, otherUser);
        });
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        assertDoesNotThrow(() -> {
            commentService.deleteComment(taskId, commentId, currentUser);
        });

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_NotAuthor() {
        Usuario otherUserObj = new Usuario();
        otherUserObj.setId(UUID.randomUUID());
        UserPrincipal otherUser = new UserPrincipal(otherUserObj);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(AccessDeniedException.class, () -> {
            commentService.deleteComment(taskId, commentId, otherUser);
        });
    }
}
