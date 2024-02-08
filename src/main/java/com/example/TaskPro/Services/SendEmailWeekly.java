//package com.example.TaskPro.Services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class SendEmailWeekly {
//    @Autowired
//    private ReportService reportService;
//
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//    public void startScheduleTask() {
//        final Runnable task = new Runnable() {
//            public void run() {
//                try {
//                    reportService.generateAndSendDocument();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        final int delay = 10;
//        scheduler.scheduleAtFixedRate(task, delay, delay, TimeUnit.SECONDS);
//    }
//}
