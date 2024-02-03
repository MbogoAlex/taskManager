package com.example.TaskPro.Repository;

import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Test
    public void UserRepository_findByEmail_returnUser(){
        UserEntity user = UserEntity.builder()
                .fName("Test")
                .lName("User")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        UserEntity user1 = userRepository.findByEmail(user.getEmail()).get();
        Assertions.assertThat(user1).isNotNull();
    }
    @Test
    public void UserRepository_findById_returnUser(){
        UserEntity user = UserEntity.builder()
                .fName("Test")
                .lName("User")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        UserEntity user1 = userRepository.findById(user.getId());
        Assertions.assertThat(user1).isNotNull();
    }
}
