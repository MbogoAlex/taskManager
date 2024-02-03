package com.example.TaskPro.Repository;

import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Stage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class StageRepositoryTests {

    ProjectRepository projectRepository;


    StageRepository stageRepository;


    @Test
    public void StageRepository_findByName_returnStage(){
      Stage stage =  Stage.builder()
              .name("TODO")
              .createdBy(1).build();
      stageRepository.save(stage);

      Stage stage1 = stageRepository.findByName(stage.getName());
      Assertions.assertThat(stage1).isNotNull();
    }

    @Test
    public void StageRepository_findByIdAndProjectId_returnStage(){
        Project project = Project.builder()
                .title("test project")
                .description("test project description")
                .createdBy(1).build();
        projectRepository.save(project);

        Stage stage =  Stage.builder()
                .name("TODO")
                .createdBy(1)
                .projectId(project)
                .build();
        stageRepository.save(stage);

        Stage stage1 = stageRepository.findByIdAndProjectId(stage.getId(), project);
        Assertions.assertThat(stage1).isNotNull();
    }

    @Test
    public void StageRepository_existsById_returnTrue(){
        Stage stage =  Stage.builder()
                .name("TODO")
                .createdBy(1).build();
        stageRepository.save(stage);

        boolean exists = stageRepository.existsById(stage.getId());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void StageRepository_findById_returnStage(){
        Stage stage =  Stage.builder()
                .name("TODO")
                .createdBy(1).build();
        stageRepository.save(stage);

        Stage stage1 = stageRepository.findById(stage.getId()).get();
        Assertions.assertThat(stage1).isNotNull();
    }

    @Test
    public void StageRepository_findByProjectId_returnTue(){
        Project project = Project.builder()
                .title("test project")
                .description("test project description")
                .createdBy(1).build();
        projectRepository.save(project);

        Stage stage =  Stage.builder()
                .name("TODO")
                .createdBy(1)
                .projectId(project)
                .build();
        stageRepository.save(stage);

        Optional<Project> pproject = projectRepository.findById(project.getId());
        List<Stage> stage1 = stageRepository.findByProjectId(pproject);
        Assertions.assertThat(stage1).isNotNull();
    }

//    @Test
//    public void StageRepository_findAllWithTasks_returnTue(){
//        Stage stage =  Stage.builder()
//                .name("TODO")
//                .createdBy(1).build();
//        stageRepository.save(stage);
//
//        Stage stage1 = stageRepository.findByName(stage.getName());
//        Assertions.assertThat(stage1).isNotNull();
//    }







}
