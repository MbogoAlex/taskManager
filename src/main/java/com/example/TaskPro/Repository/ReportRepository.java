package com.example.TaskPro.Repository;

import com.example.TaskPro.Models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Project, Integer> {
}
