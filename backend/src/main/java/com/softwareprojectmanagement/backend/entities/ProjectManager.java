package com.softwareprojectmanagement.backend.entities;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();
    
}
