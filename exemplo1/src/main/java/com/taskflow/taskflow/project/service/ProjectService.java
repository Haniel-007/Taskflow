package com.taskflow.taskflow.project.service;

import com.taskflow.taskflow.core.exception.ResourceNotFoundException;
import com.taskflow.taskflow.project.dto.ProjectRequest;
import com.taskflow.taskflow.project.dto.ProjectResponse;
import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.model.ProjectMember;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.project.repository.ProjectRepository;
import com.taskflow.taskflow.project.dto.AddMemberRequest;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import com.taskflow.taskflow.user.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Usuario owner) {
        // Garante que a entidade do proprietário esteja no estado gerenciado
        Usuario managedOwner = usuarioRepository.getReferenceById(owner.getId());

        // Cria e salva a entidade do projeto
        Project newProject = new Project();
        newProject.setName(request.name());
        newProject.setDescription(request.description());
        newProject.setOwner(managedOwner);
        Project savedProject = projectRepository.save(newProject);

        // Adiciona o proprietário como o primeiro membro do projeto
        ProjectMember member = new ProjectMember(savedProject, managedOwner);
        projectMemberRepository.save(member);

        return ProjectResponse.fromEntity(savedProject);
    }

    @Transactional
    public List<ProjectResponse> getProjectsByUser(UserPrincipal currentUser) {
        return projectMemberRepository.findAllByUserId(currentUser.getId()).stream()
                .map(ProjectMember::getProject)
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMemberToProject(UUID projectId, AddMemberRequest request, UserPrincipal currentUser) {
        // 1. Buscar o projeto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // 2. Verificar se o usuário atual é o dono do projeto ou um ADMIN
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!project.getOwner().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new AccessDeniedException("Only the project owner or an admin can add members");
        }

        // 3. Buscar o usuário a ser adicionado
        Usuario userToAdd = usuarioRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User to add not found"));

        // 4. Verificar se o usuário já é membro
        boolean isAlreadyMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, request.userId());
        if (isAlreadyMember) {
            throw new IllegalStateException("User is already a member of this project");
        }

        // 5. Adicionar o novo membro
        ProjectMember newMember = new ProjectMember(project, userToAdd);
        projectMemberRepository.save(newMember);
    }
}
