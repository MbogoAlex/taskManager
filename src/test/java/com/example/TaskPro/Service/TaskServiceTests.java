package com.example.TaskPro.Service;

import com.example.TaskPro.DTO.AssignTaskDTO;
import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Stage;
import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Models.UserEntity;
import com.example.TaskPro.Repository.ProjectRepository;
import com.example.TaskPro.Repository.StageRepository;
import com.example.TaskPro.Repository.TaskRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.example.TaskPro.Services.StageService;
import com.example.TaskPro.Services.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {
    @Mock
    StageRepository stageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;



    @Test
    public void StageService_assignTask_returnTask(){
        int[] assignees = {2};
        AssignTaskDTO newAssignees = new AssignTaskDTO();
        newAssignees.setTaskId(1);
        newAssignees.setAssigneeUserId(assignees);

        Project project = Project.builder()
                .id(1)
                .title("Project to Delete")
                .description("Description")
                .members(new ArrayList<>())
                .build();

        Task task1 = Task.builder()
                .id(1)
                .title("Task 1")
                .projectId(project)
                .assignedUser(new ArrayList<>())
                .build();

        Task updatedTask = Task.builder()
                .id(1)
                .title("Task 1")
                .projectId(project)
                .assignedUser(new ArrayList<>())
                .build();

        UserEntity user1 = UserEntity.builder().id(2).build();
        project.getMembers().add(user1);
        updatedTask.getAssignedUser().add(user1);



        when(userRepository.findById(2)).thenReturn(user1);
        when(taskRepository.findById(newAssignees.getTaskId())).thenReturn(task1);
        when(taskRepository.save(Mockito.any(Task.class))).thenReturn(updatedTask);


        taskService.assignTask(newAssignees);

        verify(taskRepository).findById(newAssignees.getTaskId());
        verify(userRepository).findById(user1.getId());
        verify(taskRepository).save(updatedTask);


    }



    @Test
    public void StageService_deleteTask_deleteTask(){

        Stage stage1 = Stage.builder()
                .id(1)
                .name("Stage 1")
                .build();

        Task task1 = Task.builder()
                .id(1)
                .title("Task 1")
                .stageId(stage1)
                .assignedUser(new ArrayList<>())
                .createdBy(2)
                .build();

        UserEntity user1 = UserEntity.builder().id(2).build();
        when(taskRepository.findById(task1.getId())).thenReturn(task1);
        //doNothing().when(stageRepository).deleteById(task1.getId());

        String result = taskService.deleteTask(task1.getId());

       Assertions.assertThat(result).isEqualTo("Task deleted successfully");

    }


}
