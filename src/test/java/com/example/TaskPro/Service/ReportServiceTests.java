package com.example.TaskPro.Service;

import com.example.TaskPro.DTO.TaskReportDTO;
import com.example.TaskPro.Repository.StageRepository;
import com.example.TaskPro.Repository.TaskRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.example.TaskPro.Services.ProjectService;
import com.example.TaskPro.Services.ReportService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTests {
    @Mock
    StageRepository stageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    private ReportService reportService;

    @Test
    public void ReportService_generateTaskReport_ReturnsReports() {

        int projectId = 1;

        when(taskRepository.getTotalTasks(projectId)).thenReturn(10L);
        when(taskRepository.getTasksAddedLastSevenDays(projectId)).thenReturn(5L);
        when(taskRepository.getTotalTasksNotDone(projectId)).thenReturn(7L);
        when(taskRepository.getTasksNotDoneUpdatedToDoneLastSevenDays(projectId)).thenReturn(3L);
        when(taskRepository.getTotalTasksWithDoneStage(projectId)).thenReturn(8L);
        when(taskRepository.getTasksUpdatedToDoneLastSevenDays(projectId)).thenReturn(2L);
        when(taskRepository.findTasksAddedLastSevenDays(projectId)).thenReturn(Collections.emptyList());
        when(taskRepository.getPriorityOfTasksAddedToday(projectId)).thenReturn(Collections.emptyList());

        when(userRepository.getTotalUsersAssignedToTasks(projectId)).thenReturn(15L);
        when(userRepository.getUsersAssignedToTasksLastSevenDays(projectId)).thenReturn(6L);


        TaskReportDTO taskReportDTO = reportService.generateTaskReport(projectId);

        Assertions.assertThat(taskReportDTO).isNotNull();
        Assertions.assertThat(taskReportDTO.getAllTasks()).isEqualTo(10L);

    }

}
