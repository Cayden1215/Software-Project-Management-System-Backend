package com.softwareprojectmanagement.backend.services;

import org.springframework.stereotype.Service;

@Service
public interface GeneticAlgorithmScheduler {

    public void scheduleProject(Long projectID);

}
