package com.example.TaskPro.Controllers;

import com.example.TaskPro.DTO.TaskReportDTO;
import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Report;
import com.example.TaskPro.Services.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@CrossOrigin(origins = {"http://localhost:4200", "https://taskpro-2mq8.onrender.com"})
@RestController
@RequestMapping("/api")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping("/report/projectId={projectId}")
    public TaskReportDTO getReport(@PathVariable("projectId") int projectId){

        return reportService.generateTaskReport(projectId);
    }

    @GetMapping("/getAllProjects")
    public List<Project> getAllProjects() {
        return reportService.getAllProjects();
    }

    @GetMapping("/getSpecificProjects/{projectId}")
    public Project getSpecificProject(@PathVariable int projectId) {
        Project project = reportService.getSpecificProject(projectId);
        return project;
    }

    @GetMapping("pdf/generate/{projectId}/{email}")
    public void generatePdf(@PathVariable int projectId, @PathVariable String email, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateAndTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateAndTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        this.reportService.export(projectId, email, response);
    }
    
}
