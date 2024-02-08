package com.example.TaskPro.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class MailStructure {
    private String subject;
    private String message;
    private String filePath;
}
