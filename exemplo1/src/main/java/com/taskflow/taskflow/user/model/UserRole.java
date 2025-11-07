package com.taskflow.taskflow.user.model;

public enum UserRole {
    
    ADMIN("admin"), 
    MANAGER("manager"),
    COLLABORATOR("collaborator");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}