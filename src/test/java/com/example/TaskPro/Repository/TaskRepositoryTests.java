package com.example.TaskPro.Repository;

import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Stage;
import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTests {

    TaskRepository taskRepository;


    StageRepository stageRepository;


    ProjectRepository projectRepository;


    UserRepository userRepository;

    @Test
    public void TaskRepository_findById_returnTask(){
        Task task = Task.builder()
                .title("Unit Test ")
                .build();
        taskRepository.save(task);

        Task task1 = taskRepository.findById(task.getId());
        Assertions.assertThat(task1).isNotNull();
    }

    @Test
    public void TaskRepository_findByStageId_returnTask(){
        Stage stage =  Stage.builder()
                .name("TODO")
                .createdBy(1).build();
        stageRepository.save(stage);

        Task task = Task.builder()
                .title("Unit Test ")
                .stageId(stage)
                .build();
        taskRepository.save(task);

        Optional<Stage> sstage = stageRepository.findById(stage.getId());
        List<Task> taskList = taskRepository.findByStageId(sstage);
        Assertions.assertThat(taskList).isNotNull();
        Assertions.assertThat(taskList.size()).isEqualTo(1);
    }

    @Test
    public void TaskRepository_existsById_returnTasks(){
        Task task = Task.builder()
                .title("Unit Test ")
                .build();
        taskRepository.save(task);

        Boolean task1 = taskRepository.existsById(task.getId());
        Assertions.assertThat(task1).isTrue();
    }

    @Test
    public void TaskRepository_findByAssignedUserIdAndProjectId_returnTasks(){


        Project project = Project.builder()
                .title("test project")
                .description("test project description")
                .createdBy(1).build();
        projectRepository.save(project);

        UserEntity user = UserEntity.builder()
                .fName("Test")
                .lName("User")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        Task task = Task.builder()
                .title("Unit Test ")
                .projectId(project)
                .assignedUser(new ArrayList<>())
                .build();
        task.getAssignedUser().add(user);
        taskRepository.save(task);

        List<Task> taskList = taskRepository.findByAssignedUserIdAndProjectId(user.getId(), project);
        Assertions.assertThat(taskList).isNotNull();
        Assertions.assertThat(taskList.size()).isEqualTo(1);
    }

    @Test
    public void TaskRepository_findByAssignedUserId_returnTasks(){

        Project project = Project.builder()
                .title("test project")
                .description("test project description")
                .createdBy(1).build();
        projectRepository.save(project);

        UserEntity user = UserEntity.builder()
                .fName("Test")
                .lName("User")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        Task task = Task.builder()
                .title("Unit Test ")
                .projectId(project)
                .assignedUser(new ArrayList<>())
                .build();
        task.getAssignedUser().add(user);
        taskRepository.save(task);

        List<Task> taskList = taskRepository.findByAssignedUserId(user.getId());
        Assertions.assertThat(taskList).isNotNull();

    }




}
