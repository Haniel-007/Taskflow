package com.taskflow.taskflow.dashboard.service;

import com.taskflow.taskflow.dashboard.dto.DashboardResponse;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public DashboardResponse getDashboardSummary(UserPrincipal currentUserPrincipal) {
        Usuario currentUser = userService.validateAndGetUserByUsername(currentUserPrincipal.getUsername());
        long todoCount = taskRepository.countByStatusAndAssignee(TaskStatus.TODO, currentUser);
        long inProgressCount = taskRepository.countByStatusAndAssignee(TaskStatus.IN_PROGRESS, currentUser);
        long doneCount = taskRepository.countByStatusAndAssignee(TaskStatus.DONE, currentUser);

        return new DashboardResponse(todoCount, inProgressCount, doneCount);
    }
}
