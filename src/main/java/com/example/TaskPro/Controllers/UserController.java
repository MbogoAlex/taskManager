package com.example.TaskPro.Controllers;

import com.example.TaskPro.DTO.*;
import com.example.TaskPro.Models.Roles;
import com.example.TaskPro.Models.UserEntity;
import com.example.TaskPro.Repository.RoleRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.example.TaskPro.Security.JwtService;
import com.example.TaskPro.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.Collections;

import static java.util.Map.of;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Response> addNewUser(@RequestBody UserEntity userInfo){
        if (userRepository.existsByEmail(userInfo.getEmail())) {
            //return new ResponseEntity<>(new ErrorResponseDTO("Email is taken!"), HttpStatus.BAD_REQUEST);

            return ResponseEntity.ok(
                    Response.builder()
                            .timeStamp(LocalDateTime.now())
                            //.data(of("user", service.registerUser(user)))
                            .message("Email is taken!")
                            .status(HttpStatus.CONFLICT)
                            .statusCode(HttpStatus.CONFLICT.value())
                            .build()
            );
        }

        UserEntity user = new UserEntity();
        user.setFName(userInfo.getFName());
        user.setMName(userInfo.getMName());
        user.setLName(userInfo.getLName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(userInfo.getPassword());

        Roles roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(of("user", service.registerUser(user)))
                        .message("created successfully")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDto){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    ));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(loginDto.getEmail());
                //return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
                return ResponseEntity.ok(
                        Response.builder()
                                .timeStamp(LocalDateTime.now())
                                .data(of("token", token))
                                .message("Logged in successfully")
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build()
                );
            }
        } catch (AuthenticationException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Invalid email or password")
                            .status(HttpStatus.UNAUTHORIZED)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build()
                    );


        }

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Unknown error occurred")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build()
        );

    }
}
