package com.example.TaskPro.Service;

import com.example.TaskPro.DTO.AddMemberToProject;
import com.example.TaskPro.DTO.ProjectDTO;
import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Stage;
import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Models.UserEntity;
import com.example.TaskPro.Repository.ProjectRepository;
import com.example.TaskPro.Repository.StageRepository;
import com.example.TaskPro.Repository.TaskRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.example.TaskPro.Services.ProjectService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTests {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    TaskRepository taskRepository;
    @Mock
    StageRepository stageRepository;
    @InjectMocks
    private ProjectService projectService;



//    @Test
//    public void ProjectService_createProject_ReturnsProject() {
//
//        int userId = 1;
//        Project savedProject= Project.builder()
//                .title("New Project")
//                .description("New Project Description")
//                .createdBy(userId)
//                .build();
//
//
//        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
//        int[] assigneeUserIds = {2};
//        UserEntity user = UserEntity.builder().id(2).build();
//        when(userRepository.findById(2)).thenReturn(user);
//
//        AddMemberToProject newAssignee = new AddMemberToProject();
//        newAssignee.setProjectId(1);
//        newAssignee.setAssigneeUserId(assigneeUserIds);
//
//       // projectService.addMembers(newAssignee);
//
//        // Call the method under test
//        Project createdProject = projectService.createProject(savedProject, userId);
//
//        // Verify interactions and results
//        verify(projectRepository).save(any(Project.class));
//        //verify(addMembers(any(AddMemberToProject.class))); // Verify addMembers is called
//        Assertions.assertThat(createdProject).isEqualTo(savedProject);
//        Assertions.assertThat(createdProject.getMembers()).containsExactly(user);
//    }

    @Test
    public void ProjectService_addMembers_ReturnsProject() {
        int projectId = 1;
        int[] assigneeUserIds = {2, 3, 4};


        Project project = Project.builder()
                .id(projectId)
                .members(new ArrayList<>())
                .build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));


        UserEntity user1 = UserEntity.builder().id(2).build();
        UserEntity user2 = UserEntity.builder().id(3).build();
        UserEntity user3 = UserEntity.builder().id(4).build();
        when(userRepository.findById(2)).thenReturn(user1);
        when(userRepository.findById(3)).thenReturn(user2);
        when(userRepository.findById(4)).thenReturn(user3);


        AddMemberToProject newAssignee = new AddMemberToProject();
        newAssignee.setProjectId(projectId);
        newAssignee.setAssigneeUserId(assigneeUserIds);

        projectService.addMembers(newAssignee);


        verify(projectRepository, times(3)).save(project);

        Assertions.assertThat(project.getMembers()).contains(user1, user2, user3);
    }

    @Test
    public void ProjectService_deleteMembers_ReturnsProject() {
        int projectId = 1;
        int[] assigneeUserIds = {2};

        Project project = Project.builder()
                .id(projectId)
                .members(new ArrayList<>())
                .build();

        UserEntity user1 = UserEntity.builder().id(2).build();
        AddMemberToProject deleteAssignee = new AddMemberToProject();
        deleteAssignee.setProjectId(projectId);
        deleteAssignee.setAssigneeUserId(assigneeUserIds);
        project.getMembers().add(user1);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(user1);



        projectService.deleteMembers(deleteAssignee);

        verify(projectRepository).save(argThat(savedProject -> savedProject.getMembers().isEmpty()));

        Assertions.assertThat(project.getMembers()).isEmpty();

    }

    @Test
    public void ProjectService_updateProject_ReturnsProject() {
        int projectId = 1;
        Project project = Project.builder()
                .id(1)
                .title("test project")
                .description("test project description")
                .build();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project updatedProject = Project.builder()
                .title("updated test project")
                .description("updated test project description")
                .build();
        when(projectRepository.save(Mockito.any(Project.class))).thenReturn(updatedProject);

        Project result = projectService.updateProject(updatedProject, projectId );

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo(updatedProject.getTitle());

    }

    @Test
    public void ProjectService_getAllProjects_ReturnsProjects() {        // Mock data
        int userId = 1;

        Project createdProject = Project.builder()
                .id(1)
                .title("Created Project")
                .description("Description")
                .build();
        Project memberProject = Project.builder()
                .id(2)
                .title("Member Project")
                .description("Description")
                .build();
        Task assignedTask = Task.builder()
                .id(1)
                .title("Task 1")
                .projectId(memberProject)
                .build();


        when(projectRepository.findByCreatedBy(userId)).thenReturn(List.of(createdProject));
        when(taskRepository.findByAssignedUserId(userId)).thenReturn(List.of(assignedTask));


        List<ProjectDTO> allProjects = projectService.getAllProjects(userId);


        verify(projectRepository).findByCreatedBy(userId);
        verify(taskRepository).findByAssignedUserId(userId);

        Assertions.assertThat(allProjects).containsExactlyInAnyOrder(
                new ProjectDTO(1, "Created Project", "Description"),
                new ProjectDTO(2, "Member Project", "Description")
        );

    }

    @Test
    public void deleteProject_deletesProjectAndRelatedData() {
        int projectId = 1;

        // Mock project, stages, and tasks using builders
        Project project = Project.builder()
                .id(projectId)
                .title("Project to Delete")
                .description("Description")
                .stages(new ArrayList<>())
                .build();
        Stage stage1 = Stage.builder()
                .id(1)
                .name("Stage 1")
                .projectId(project)
                .tasks(new ArrayList<>())
                .build();
        Task task1 = Task.builder()
                .id(1)
                .title("Task 1")
                .assignedUser(new ArrayList<>())
                .build();
        UserEntity user1 = UserEntity.builder().id(2).build();
        project.getStages().add(stage1);
        stage1.getTasks().add(task1);
        task1.getAssignedUser().add(user1);




        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(stageRepository.findByProjectId(Optional.of(project))).thenReturn(List.of(stage1));


        projectService.deleteProject(projectId);


        verify(projectRepository).findById(projectId);
        verify(stageRepository).findByProjectId(Optional.of(project));
        verify(projectRepository).delete(project);
    }



}
