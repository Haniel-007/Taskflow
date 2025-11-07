package com.taskflow.taskflow.comment.controller;

import com.taskflow.taskflow.comment.dto.CommentRequest;
import com.taskflow.taskflow.comment.dto.CommentResponse;
import com.taskflow.taskflow.comment.service.CommentService;
import com.taskflow.taskflow.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID taskId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CommentResponse createdComment = commentService.createComment(taskId, request, currentUser);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CommentResponse comment = commentService.getCommentById(taskId, commentId, currentUser);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<CommentResponse> comments = commentService.getCommentsByTaskId(taskId, currentUser);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CommentResponse updatedComment = commentService.updateComment(taskId, commentId, request, currentUser);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        commentService.deleteComment(taskId, commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
