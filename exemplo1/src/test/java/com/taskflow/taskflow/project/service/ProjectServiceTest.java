package com.taskflow.taskflow.project.service;

import com.taskflow.taskflow.project.dto.ProjectRequest;
import com.taskflow.taskflow.project.dto.ProjectResponse;
import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.model.ProjectMember;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.project.repository.ProjectRepository;
import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.user.model.UserStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private com.taskflow.taskflow.user.repository.UsuarioRepository usuarioRepository;

    @InjectMocks
    private ProjectService projectService;

    private Usuario projectOwner;
    private ProjectRequest projectRequest;
    private UserPrincipal currentUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        // Arrange: Configurar um usuário "owner" e uma requisição de projeto
        projectOwner = new Usuario();
        projectOwner.setId(userId);
        projectOwner.setName("Sofia Manager");
        projectOwner.setEmail("sofia@example.com");
        projectOwner.setRole(UserRole.MANAGER);
        projectOwner.setStatus(UserStatus.ACTIVE);
        projectOwner.setCreatedAt(Instant.now());

        projectRequest = new ProjectRequest("Novo Projeto de Teste", "Descrição do projeto.");
        currentUser = new UserPrincipal(projectOwner);
    }

    @Test
    @DisplayName("Deve criar um projeto e adicionar o dono como membro com sucesso")
    void createProject_ShouldCreateProjectAndAddOwnerAsMember_WhenSuccessful() {
        // Arrange
        Project projectToSave = new Project();
        projectToSave.setName(projectRequest.name());
        projectToSave.setDescription(projectRequest.description());
        projectToSave.setOwner(projectOwner);

        Project savedProject = new Project();
        savedProject.setId(UUID.randomUUID());
        savedProject.setName(projectRequest.name());
        savedProject.setDescription(projectRequest.description());
        savedProject.setOwner(projectOwner);

        // Mock: Simular o comportamento dos repositórios
        when(usuarioRepository.getReferenceById(any(UUID.class))).thenReturn(projectOwner);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Chamar o método a ser testado
        ProjectResponse response = projectService.createProject(projectRequest, projectOwner);

        // Assert: Verificar os resultados
        assertNotNull(response);
        assertEquals(savedProject.getId(), response.id());
        assertEquals(projectRequest.name(), response.name());
        assertEquals(projectOwner.getId(), response.ownerId());

        // Verificar se o método save do repositório de projetos foi chamado 1 vez
        verify(projectRepository, times(1)).save(any(Project.class));

        // Verificar se o método save do repositório de membros foi chamado 1 vez
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("Deve retornar a lista de projetos quando o usuário é membro")
    void getProjectsByUser_ShouldReturnProjects_WhenUserIsMember() {
        // Arrange
        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project Alpha");
        project1.setOwner(projectOwner);

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Project Beta");
        project2.setOwner(projectOwner);

        ProjectMember member1 = new ProjectMember(project1, projectOwner);
        ProjectMember member2 = new ProjectMember(project2, projectOwner);

        when(projectMemberRepository.findAllByUserId(userId)).thenReturn(List.of(member1, member2));

        // Act
        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser);

        // Assert
        assertNotNull(projects);
        assertEquals(2, projects.size());
        assertEquals("Project Alpha", projects.get(0).name());
        assertEquals("Project Beta", projects.get(1).name());
        verify(projectMemberRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário não é membro de nenhum projeto")
    void getProjectsByUser_ShouldReturnEmptyList_WhenUserIsNotMember() {
        // Arrange
        when(projectMemberRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        // Act
        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser);

        // Assert
        assertNotNull(projects);
        assertTrue(projects.isEmpty());
        verify(projectMemberRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN adicione um membro a um projeto de um MANAGER")
    void addMemberToProject_ShouldAllowAdminToAddMember_WhenUserIsAdmin() {
        // Arrange
        Usuario adminUser = new Usuario();
        adminUser.setId(UUID.randomUUID());
        adminUser.setName("Admin User");
        adminUser.setRole(UserRole.ADMIN);
        UserPrincipal adminPrincipal = new UserPrincipal(adminUser);

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setOwner(projectOwner); // O dono é o MANAGER

        Usuario userToAdd = new Usuario();
        userToAdd.setId(UUID.randomUUID());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(userToAdd.getId())).thenReturn(Optional.of(userToAdd));
        when(projectMemberRepository.existsByProjectIdAndUserId(project.getId(), userToAdd.getId())).thenReturn(false);

        // Act
        projectService.addMemberToProject(project.getId(), new com.taskflow.taskflow.project.dto.AddMemberRequest(userToAdd.getId()), adminPrincipal);

        // Assert
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }
}
