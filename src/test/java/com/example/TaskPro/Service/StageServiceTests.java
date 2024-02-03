package com.example.TaskPro.Service;

import com.example.TaskPro.DTO.UpdateStageName;
import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Stage;
import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Models.UserEntity;
import com.example.TaskPro.Repository.ProjectRepository;
import com.example.TaskPro.Repository.StageRepository;
import com.example.TaskPro.Repository.TaskRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.example.TaskPro.Services.ReportService;
import com.example.TaskPro.Services.StageService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageServiceTests {
    @Mock
    StageRepository stageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private StageService stageService;

    @Test
    public void StageService_createStage_returnStage(){
        int projectId = 1;

        Project project = Project.builder()
                .id(projectId)
                .title("Project to Delete")
                .description("Description")
                .build();

        Stage stage1 = Stage.builder()
                .id(1)
                .name("Stage 1")
                .projectId(project)
                .build();

        UserEntity user1 = UserEntity.builder().id(2).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.save(Mockito.any(Stage.class))).thenReturn(stage1);

        Stage savedStage = stageService.createStage(stage1, projectId);

        Assertions.assertThat(savedStage).isNotNull();
        Assertions.assertThat(savedStage .getProjectId()).isEqualTo(project);

    }

    @Test
    public void StageService_deleteStage_deletesStageAndRelatedTasks() {
        int stageId = 1;
        Stage stage1 = Stage.builder()
                .id(stageId)
                .name("Stage 1")
                .tasks(new ArrayList<>())
                .build();
        Task task1 = Task.builder()
                .id(1)
                .title("Task 1")
                .assignedUser(new ArrayList<>())
                .build();
        UserEntity user1 = UserEntity.builder().id(2).build();
        stage1.getTasks().add(task1);
        task1.getAssignedUser().add(user1);

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage1));
        when(taskRepository.findByStageId(Optional.of(stage1))).thenReturn(List.of(task1));
        doNothing().when(stageRepository).deleteById(stageId);


        assertAll(() ->  stageService.deleteStage(stageId));



    }

    @Test
    public void StageService_updateStage_updateStageAndRelatedTasks() {
        Stage stage1 = Stage.builder()
                .id(1)
                .name("Stage 1")
                .tasks(new ArrayList<>())
                .build();
        UpdateStageName up = new UpdateStageName();
        up.setStageId(stage1.getId());
        up.setName("updated stage name");

        Stage updatedStage = Stage.builder()
                .id(stage1.getId())
                .name(up.getName())
                .build();

        when(stageRepository.existsById(up.getStageId())).thenReturn(true);
        when(stageRepository.findById(up.getStageId())).thenReturn(Optional.of(stage1));
        when(stageRepository.save(Mockito.any(Stage.class))).thenReturn(updatedStage);

        Stage savedStage = stageService.update(up);

        Assertions.assertThat(savedStage).isNotNull();
        Assertions.assertThat(savedStage .getName()).isEqualTo(updatedStage.getName());
    }

}
