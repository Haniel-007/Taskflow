package com.taskflow.taskflow.comment.service;

import com.taskflow.taskflow.core.exception.ResourceNotFoundException;
import com.taskflow.taskflow.comment.dto.CommentRequest;
import com.taskflow.taskflow.comment.dto.CommentResponse;
import com.taskflow.taskflow.comment.model.Comment;
import com.taskflow.taskflow.comment.repository.CommentRepository;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public CommentResponse createComment(UUID taskId, CommentRequest request, UserPrincipal currentUser) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        UUID projectId = task.getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        var author = usuarioRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        Comment newComment = new Comment();
        newComment.setContent(request.content());
        newComment.setTask(task);
        newComment.setAuthor(author);

        Comment savedComment = commentRepository.save(newComment);

        return CommentResponse.fromEntity(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(UUID taskId, UUID commentId, UserPrincipal currentUser) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment not found for the given task");
        }
        
        UUID projectId = comment.getTask().getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        return CommentResponse.fromEntity(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTaskId(UUID taskId, UserPrincipal currentUser) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        UUID projectId = task.getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        List<Comment> comments = commentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(UUID taskId, UUID commentId, CommentRequest request, UserPrincipal currentUser) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment not found for the given task");
        }

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User is not the author of the comment");
        }

        comment.setContent(request.content());
        Comment updatedComment = commentRepository.save(comment);

        return CommentResponse.fromEntity(updatedComment);
    }

    @Transactional
    public void deleteComment(UUID taskId, UUID commentId, UserPrincipal currentUser) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment not found for the given task");
        }

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User is not the author of the comment");
        }

        commentRepository.delete(comment);
    }
}
