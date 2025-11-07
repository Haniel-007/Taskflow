package com.taskflow.taskflow.project.repository;

import com.taskflow.taskflow.project.model.ProjectMember;
import com.taskflow.taskflow.project.model.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
    List<ProjectMember> findAllByUserId(UUID userId);
}
