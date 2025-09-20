package com.javatechie.spring.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies/jobs")
public class MovieJobController {

    @Autowired
    private JobLauncher jobLauncher; // 1

    @Autowired
    private Job runJob; // 2

    @PostMapping("/importMovies") // 3
    public String importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder() // 4
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(runJob, jobParameters); // 5
            return "Movie import job started successfully!";
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                 | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) { // 6
            e.printStackTrace();
            return "Movie import job failed: " + e.getMessage();
        }
    }
}
