package com.softwareprojectmanagement.backend.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProjectManager") 
public class ProjectManager extends User {
    @OneToMany(mappedBy = "projectManager")
    private List<Project> projects;
    
}
