package com.example.TaskPro.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class Report {
    private String stage;
    private List tasks;
//    public List assignees;
}
