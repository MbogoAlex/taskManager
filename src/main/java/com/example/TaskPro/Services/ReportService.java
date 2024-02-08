package com.example.TaskPro.Services;

import com.example.TaskPro.DTO.TaskReportDTO;
import com.example.TaskPro.Models.MailStructure;
import com.example.TaskPro.Models.Project;
import com.example.TaskPro.Models.Report;
import com.example.TaskPro.Models.Task;
import com.example.TaskPro.Repository.ProjectRepository;
import com.example.TaskPro.Repository.ReportRepository;
import com.example.TaskPro.Repository.TaskRepository;
import com.example.TaskPro.Repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Service
public class ReportService {

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;

    public TaskReportDTO generateTaskReport(int projectId) {

        long totalTasks = taskRepository.getTotalTasks(projectId);
        long tasksAddedLastSevenDays = taskRepository.getTasksAddedLastSevenDays(projectId);
        long totalTasksNotDone = taskRepository.getTotalTasksNotDone(projectId);
        long tasksNotDoneUpdatedToDoneLastSevenDays = taskRepository.getTasksNotDoneUpdatedToDoneLastSevenDays(projectId);
        long totalTasksWithDoneStage = taskRepository.getTotalTasksWithDoneStage(projectId);
        long tasksUpdatedToDoneLastSevenDays = taskRepository.getTasksUpdatedToDoneLastSevenDays(projectId);
        long totalUsersAssignedToTasks = userRepository.getTotalUsersAssignedToTasks(projectId);
        long usersAssignedToTasksLastSevenDays = userRepository.getUsersAssignedToTasksLastSevenDays(projectId);
        List<Task> tasksAddedLastSevenDaysDetails = taskRepository.findTasksAddedLastSevenDays(projectId);
        List<Object[]> priorityOfTasksAddedToday = taskRepository.getPriorityOfTasksAddedToday(projectId);

        return new TaskReportDTO(
                totalTasks,
                tasksAddedLastSevenDays,
                totalTasksNotDone,
                tasksNotDoneUpdatedToDoneLastSevenDays,
                totalTasksWithDoneStage,
                tasksUpdatedToDoneLastSevenDays,
                totalUsersAssignedToTasks,
                usersAssignedToTasksLastSevenDays,
                tasksAddedLastSevenDaysDetails,
                priorityOfTasksAddedToday
        );
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getSpecificProject(int projectId) {
        Project project = reportRepository.findById(projectId).orElse(null);
//        generateReport(project);
        return project;
    }

    //GENERATE REPORT CODE
    public void export(int projectId, String email, HttpServletResponse response) throws IOException {

        List<Report> reportList1 = new ArrayList<>();
        Project project = projectRepository.findById(projectId).orElse(null);

        String projectName = project.getTitle();

        List stages = project.getStages();
        for(int i = 0; i < stages.size(); i++) {
            String stageName = project.getStages().get(i).getName();
            int numOfTasks = project.getStages().get(i).getTasks().size();
            List tasks = new ArrayList();

            for(int j = 0; j < numOfTasks; j++) {

                tasks.add(project.getStages().get(i).getTasks().get(j).getTitle());
            }


            reportList1.add(new Report(stageName, tasks));
        }

        Document document = new Document(PageSize.A4);
        String filePath = "/home/mbogo/Desktop/Report/" + projectName + "_Report.pdf";
        OutputStream outputStream = new FileOutputStream(new File(filePath));
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph(projectName, fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm");
        String currentDateAndTime = dateFormat.format(new Date());

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);

        Paragraph timeGenerated = new Paragraph("Report Generated On " + currentDateAndTime, fontParagraph);
        timeGenerated.setAlignment(Paragraph.ALIGN_LEFT);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 3});
        table.setSpacingBefore(5);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        cell.setPhrase(new Phrase("Stage", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Tasks", font));
        table.addCell(cell);

        for (Report report : reportList1) {
            table.addCell(String.valueOf(report.getStage()));

            List<String> tasks = report.getTasks();
            StringBuilder tasksStringBuilder = new StringBuilder();
            for (String task : tasks) {
                tasksStringBuilder.append("- ").append(task).append("\n");
            }
            table.addCell(tasksStringBuilder.toString().trim());
        }

        document.add(paragraph);
        document.add(timeGenerated);
        document.add(new Paragraph());
        document.add(table);
        document.close();

        outputStream.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + projectName + "_Report.pdf\"");
        Files.copy(Paths.get(filePath), response.getOutputStream());
        generateAndSendDocument(projectId, email);
    }

    public void generateAndSendDocument(int projectId, String email) throws IOException {
//        int projectId = 1;
        List<Report> reportList1 = new ArrayList<>();
        Project project = projectRepository.findById(projectId).orElse(null);

        String projectName = project.getTitle();

        List stages = project.getStages();
        for(int i = 0; i < stages.size(); i++) {
            String stageName = project.getStages().get(i).getName();
            int numOfTasks = project.getStages().get(i).getTasks().size();
            List tasks = new ArrayList();

            for(int j = 0; j < numOfTasks; j++) {
                tasks.add(project.getStages().get(i).getTasks().get(j).getTitle());
            }

            reportList1.add(new Report(stageName, tasks));
        }

        Document document = new Document(PageSize.A4);
        String filePath = "/home/mbogo/Desktop/Report/" + projectName + "_Report.pdf";
        OutputStream outputStream = new FileOutputStream(new File(filePath));
        PdfWriter.getInstance(document, outputStream);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph paragraph = new Paragraph(projectName, fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm");
        String currentDateAndTime = dateFormat.format(new Date());

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);

        Paragraph timeGenerated = new Paragraph("Report Generated On " + currentDateAndTime, fontParagraph);
        timeGenerated.setAlignment(Paragraph.ALIGN_LEFT);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 3});
        table.setSpacingBefore(5);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        cell.setPhrase(new Phrase("Stage", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Tasks", font));
        table.addCell(cell);

        for (Report report : reportList1) {
            table.addCell(String.valueOf(report.getStage()));

            // Add tasks without square brackets
            List<String> tasks = report.getTasks();
            StringBuilder tasksStringBuilder = new StringBuilder();
            for (String task : tasks) {
                tasksStringBuilder.append("- ").append(task).append("\n");
            }
            table.addCell(tasksStringBuilder.toString().trim());
        }

        document.add(paragraph);
        document.add(timeGenerated);
        document.add(new Paragraph());
        document.add(table);
        document.close();

        outputStream.close();
        sendDocument(filePath, projectName, email);
    }

    public void sendDocument(String filepath, String projectName, String email) {
        String subject = "Weekly report for "+projectName;
        String message = "Attached is the weekly email report";
        String mail = email;

        MailStructure mailStructure = new MailStructure(subject, message, filepath);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromMail);
            helper.setTo(mail);
            helper.setSubject(mailStructure.getSubject());
            helper.setText(mailStructure.getMessage());

            FileSystemResource file = new FileSystemResource(new File(mailStructure.getFilePath()));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }



}
