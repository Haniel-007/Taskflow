package com.taskflow.taskflow.dashboard.service;

import com.taskflow.taskflow.dashboard.dto.DashboardResponse;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardSummary() {
        UserPrincipal currentUserPrincipal = new UserPrincipal(new Usuario());
        Usuario currentUser = new Usuario();
        when(userService.validateAndGetUserByUsername(currentUserPrincipal.getUsername())).thenReturn(currentUser);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.TODO, currentUser)).thenReturn(5L);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.IN_PROGRESS, currentUser)).thenReturn(3L);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.DONE, currentUser)).thenReturn(2L);

        DashboardResponse summary = dashboardService.getDashboardSummary(currentUserPrincipal);

        assertEquals(5L, summary.getTodoCount());
        assertEquals(3L, summary.getInProgressCount());
        assertEquals(2L, summary.getDoneCount());
    }
}
