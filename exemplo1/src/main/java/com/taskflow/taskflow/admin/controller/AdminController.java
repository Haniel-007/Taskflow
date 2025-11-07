package com.taskflow.taskflow.admin.controller;

import com.taskflow.taskflow.admin.dto.UpdateUserRoleRequest;
import com.taskflow.taskflow.admin.dto.UpdateUserStatusRequest;
import com.taskflow.taskflow.admin.service.AdminService;
import com.taskflow.taskflow.user.model.Usuario;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    AdminService adminService;

    @PatchMapping("/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UpdateUserStatusRequest request) {
        adminService.updateUserStatus(userId, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRoleRequest request) {
        adminService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }
}
