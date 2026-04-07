# Multi-Member Task Assignment Refactoring

**Date:** April 7, 2026  
**Objective:** Refactor the TaskAssignment entity and GeneticAlgorithmSchedulerImpl service to support multiple project member assignments per task.

---

## Overview

This refactoring modernizes the task scheduling system to align with the `Task.requiredMemberNum` field, enabling each task to be assigned to multiple team members based on resource requirements. The genetic algorithm now respects member count constraints during scheduling optimization.

---

## Changes Summary

### 1. TaskAssignment Entity (`TaskAssignment.java`)

#### **Key Changes:**
- **Changed field:** `assignedMember` → `assignedMembers`
- **Relationship type:** `@OneToMany` → `@ManyToMany`
- **Join table:** Created `task_assignment_members` for the many-to-many relationship
- **Added validation methods:**
  - `isValidMemberCount()`: Validates that assigned members match `task.requiredMemberNum`
  - `getValidationError()`: Returns descriptive error message if validation fails

#### **Code:**
```java
@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
@JoinTable(
    name = "task_assignment_members",
    joinColumns = @JoinColumn(name = "assignment_id"),
    inverseJoinColumns = @JoinColumn(name = "project_member_id")
)
private Set<ProjectMember> assignedMembers = new HashSet<>();

/**
 * Validates that the number of assigned members matches the required member count.
 */
public boolean isValidMemberCount() {
    if (task == null || task.getRequiredMemberNum() == null) {
        return true;
    }
    return this.assignedMembers.size() == task.getRequiredMemberNum();
}
```

#### **Benefits:**
- ✅ Supports flexible team composition for tasks
- ✅ Cascading persistence ensures member assignments are saved
- ✅ Built-in validation ensures data consistency

---

### 2. TaskAssignmentDto DTO (`TaskAssignmentDto.java`)

#### **Key Changes:**
- **Changed fields:**
  - `assignedMemberId` → `assignedMemberIds` (List<Long>)
  - `assignedMemberName` → `assignedMemberNames` (List<String>)
- **Added fields:**
  - `requiredMemberNum`: Displays the required number of members for the task
- **Added validation methods:**
  - `isValidMemberCount()`: Client-side validation support
  - `getValidationError()`: Descriptive error messages

#### **Code Snippet:**
```java
private Integer requiredMemberNum;
private List<Long> assignedMemberIds = new ArrayList<>();
private List<String> assignedMemberNames = new ArrayList<>();

public boolean isValidMemberCount() {
    if (requiredMemberNum == null) {
        return true;
    }
    return this.assignedMemberIds.size() == requiredMemberNum;
}
```

#### **Benefits:**
- ✅ Clean API for multiple member assignment representation
- ✅ Easy to consume in frontend applications
- ✅ Consistent validation across layers

---

### 3. GeneticAlgorithmSchedulerImpl (`GeneticAlgorithmSchedulerImpl.java`)

#### **3.1 Gene Class Update**

**Changed:**
```java
// Before
ProjectMember assignedMember;

// After
Set<ProjectMember> assignedMembers; // Multiple members per task
```

**Updated `copy()` and `toString()` methods** to handle member sets.

#### **3.2 New Helper Methods**

**`findCompatibleMembers(Task, List<ProjectMember>)`**
- Replaces `findCompatibleMember()` (singular)
- Returns `Set<ProjectMember>` instead of single member
- Finds all team members with required skills

**`selectRandomMembers(Set<ProjectMember>, int count)`**
- Randomly selects N members from a compatible pool
- Ensures scheduled diversity and load balancing
- Respects `task.requiredMemberNum`

```java
private Set<ProjectMember> selectRandomMembers(Set<ProjectMember> compatibleMembers, int count) {
    Set<ProjectMember> selected = new HashSet<>();
    List<ProjectMember> list = new ArrayList<>(compatibleMembers);
    Collections.shuffle(list, random);
    
    int selectCount = Math.min(count, list.size());
    for (int i = 0; i < selectCount; i++) {
        selected.add(list.get(i));
    }
    return selected;
}
```

#### **3.3 Population Initialization**

**Updated `initializePopulation()`**
- Assigns N members to each task where N = `task.requiredMemberNum()`
- Falls back to 1 member if `requiredMemberNum` is null
- Maintains skill matching constraints for all assigned members

```java
int requiredCount = task.getRequiredMemberNum() != null ? 
    task.getRequiredMemberNum() : 1;
Set<ProjectMember> assignedMembers = selectRandomMembers(compatibleMembers, requiredCount);
```

#### **3.4 Fitness Evaluation**

**Updated `evaluateFitness()`**
- Tracks availability for **each assigned member** separately
- When multiple members are assigned to a task, all must be available
- Task can start only when **all assigned members** are free
- Respects both task dependencies **and** resource constraints

**Key Logic:**
```java
// Find earliest slot where ALL assigned members are free
int latestMemberFreeTime = 0;
for (ProjectMember member : gene.assignedMembers) {
    int memberFreeTime = memberAvailability.getOrDefault(member, 0);
    latestMemberFreeTime = Math.max(latestMemberFreeTime, memberFreeTime);
}
int startTime = Math.max(earliestStart, latestMemberFreeTime);

// Update availability for all assigned members
for (ProjectMember member : gene.assignedMembers) {
    memberAvailability.put(member, endTime);
}
```

#### **3.5 Crossover Operation**

**Updated `createCrossoverChild()`**
- Handles member set inheritance from parents
- 50% probability to inherit parent's member assignment
- Otherwise finds alternative compatible members
- Respects required member count during recombination

```java
if (random.nextDouble() < 0.5) {
    assignedMembers = new HashSet<>(parentGene.assignedMembers);
} else {
    Set<ProjectMember> compatible = findCompatibleMembers(parentGene.task, projectMembers);
    int requiredCount = parentGene.task.getRequiredMemberNum() != null ? 
        parentGene.task.getRequiredMemberNum() : 1;
    assignedMembers = selectRandomMembers(compatible, requiredCount);
}
```

#### **3.6 Mutation Operation**

**Updated `mutate()`**
- Two mutation types: **Swap** and **Reassignment**
- Reassignment now selects alternative member sets instead of single members
- Filters out currently assigned members to ensure diversity

```java
if (mutationType < 0.5 && chromosome.genes.size() > 1) {
    // Swap Mutation: still swaps task positions
} else {
    // Reassignment Mutation: reassign to different compatible members
    Set<ProjectMember> alternativeMembers = compatibleMembers.stream()
        .filter(member -> !gene.assignedMembers.contains(member))
        .collect(Collectors.toSet());
    
    Set<ProjectMember> newMembers = selectRandomMembers(alternativeMembers, requiredCount);
    if (!newMembers.isEmpty()) {
        gene.assignedMembers = newMembers;
    }
}
```

#### **3.7 Schedule Persistence**

**Updated `persistSchedule()`**
- Saves all assigned members for each task assignment
- Validates member count before saving to database

```java
TaskAssignment assignment = new TaskAssignment();
assignment.setProject(project);
assignment.setTask(gene.task);
assignment.setAssignedMembers(new HashSet<>(gene.assignedMembers));
// ... set dates ...

// Validate before persistence
if (!assignment.isValidMemberCount()) {
    throw new IllegalArgumentException(assignment.getValidationError());
}

taskAssignmentRepository.save(assignment);
```

---

### 4. ProjectServiceImpl Update (`ProjectServiceImpl.java`)

**Updated `getProjectTaskAssignments()` method:**
- Converts `Set<ProjectMember>` to lists of IDs and names
- Properly initializes `requiredMemberNum` in DTO
- Handles empty member sets gracefully

```java
if (assignment.getAssignedMembers() != null && !assignment.getAssignedMembers().isEmpty()) {
    dto.setAssignedMemberIds(
        assignment.getAssignedMembers().stream()
            .map(member -> member.getTeamMember().getUserID())
            .toList()
    );
    dto.setAssignedMemberNames(
        assignment.getAssignedMembers().stream()
            .map(member -> member.getTeamMember().getName())
            .toList()
    );
}
```

---

### 5. TaskAssignmentMapper (New) (`TaskAssignmentMapper.java`)

**Created new mapper class** for clean entity-to-DTO conversion:
- Single responsibility: handles TaskAssignment entity → DTO conversion
- Encapsulates the mapping logic for reusability
- Supports future enhancement paths

```java
public static TaskAssignmentDto mapToTaskAssignmentDto(TaskAssignment taskAssignment) {
    if (taskAssignment == null) {
        return null;
    }
    // ... mapping logic ...
}
```

---

## Database Migration

### Required Migration Script

Create a migration to introduce the new join table:

```sql
CREATE TABLE task_assignment_members (
    assignment_id BIGINT NOT NULL,
    project_member_id BIGINT NOT NULL,
    PRIMARY KEY (assignment_id, project_member_id),
    FOREIGN KEY (assignment_id) REFERENCES TaskAssignment(assignmentID),
    FOREIGN KEY (project_member_id) REFERENCES ProjectMember(ID)
);
```

### Legacy Data Migration (If Applicable)

If migrating from single-member to multi-member model:

```sql
-- Populate new join table from old single member assignments
INSERT INTO task_assignment_members (assignment_id, project_member_id)
SELECT ta.assignmentID, pm.ID
FROM TaskAssignment ta
JOIN ProjectMember pm ON ta.assignedMember_id = pm.ID;
```

---

## Validation & Constraints

### Data Integrity Checks

1. **Entity-level validation:**
   - `TaskAssignment.isValidMemberCount()` ensures correct member count
   - Throws `IllegalArgumentException` if validation fails during persistence

2. **Business logic validation:**
   - All assigned members must have required skills
   - Number of assigned members must equal `task.requiredMemberNum`
   - Task start time respects all members' availability

3. **DTO-level validation:**
   - `TaskAssignmentDto.isValidMemberCount()` for client validation
   - `TaskAssignmentDto.getValidationError()` for error messaging

---

## API Response Example

### New TaskAssignmentDto Structure

```json
{
  "assignmentID": 1,
  "taskID": 5,
  "taskName": "Backend API Development",
  "requiredMemberNum": 2,
  "assignedMemberIds": [101, 102],
  "assignedMemberNames": ["John Doe", "Jane Smith"],
  "scheduledStartDate": "2026-04-15",
  "scheduledEndDate": "2026-04-22",
  "projectID": 10
}
```

---

## Testing Recommendations

### Unit Tests

1. **Gene & Chromosome:**
   - Verify member set copy operations
   - Test chromosome string representation with multiple members

2. **Population Initialization:**
   - Verify correct number of members assigned per task
   - Ensure skill matching for all assigned members

3. **Fitness Evaluation:**
   - Test multi-member availability tracking
   - Verify task scheduling respects all member schedules
   - Test dependency constraints with multi-member assignments

4. **Genetic Operations:**
   - Crossover maintains member sets correctly
   - Mutation generates diverse member combinations
   - Member count constraints preserved through generations

5. **Persistence:**
   - Verify all members saved to database
   - Test validation errors on invalid assignments

### Integration Tests

1. **End-to-end scheduling:**
   - Full project scheduling with multi-member tasks
   - Verify database population
   - Test API response DTO conversion

2. **Data consistency:**
   - Join table entries match entity relationships
   - No orphaned records in assignment table

---

## Migration Checklist

- [x] Update TaskAssignment entity with @ManyToMany relationship
- [x] Add validation methods to TaskAssignment
- [x] Update TaskAssignmentDto with list fields
- [x] Update GeneticAlgorithmSchedulerImpl Gene class
- [x] Implement `findCompatibleMembers()` method
- [x] Implement `selectRandomMembers()` helper method
- [x] Update `initializePopulation()` for multi-member assignment
- [x] Update `evaluateFitness()` with multi-member resource tracking
- [x] Update `crossover()` for member set inheritance
- [x] Update `mutate()` for member set variations
- [x] Update `persistSchedule()` with validation
- [x] Update ProjectServiceImpl DTO conversion
- [x] Create TaskAssignmentMapper utility class
- [x] Verify no compilation errors
- [ ] Create database migration scripts
- [ ] Write unit tests
- [ ] Write integration tests
- [ ] Update API documentation
- [ ] Test backward compatibility (if needed)

---

## Benefits of This Refactoring

✅ **Flexibility:** Tasks can now require multiple team members  
✅ **Constraint Handling:** Algorithm respects member availability for all assigned resources  
✅ **Scalability:** Supports complex team compositions and skill distribution  
✅ **Data Consistency:** Built-in validation ensures correct member assignments  
✅ **Clean Architecture:** Separates mapping logic into dedicated mapper class  
✅ **Maintainability:** Clear, well-documented code with explicit multi-member handling  
✅ **Type Safety:** Leverages Java generics and type system for robust code  

---

## Future Enhancements

1. **Member Specialization:** Different roles/specializations within assignments
2. **Load Balancing:** Algorithm to balance task load across team members
3. **Skill Matching Optimization:** Weight assignments by skill proficiency levels
4. **Conflict Resolution:** Handle member unavailability and scheduling conflicts
5. **Performance Metrics:** Track utilization and efficiency across different team compositions

---

## Questions or Issues?

Refer to the following files for detailed implementation:

- [TaskAssignment.java](backend/src/main/java/com/softwareprojectmanagement/backend/entities/TaskAssignment.java)
- [TaskAssignmentDto.java](backend/src/main/java/com/softwareprojectmanagement/backend/dto/TaskAssignmentDto.java)
- [GeneticAlgorithmSchedulerImpl.java](backend/src/main/java/com/softwareprojectmanagement/backend/services/GeneticAlgorithmSchedulerImpl.java)
- [TaskAssignmentMapper.java](backend/src/main/java/com/softwareprojectmanagement/backend/mappers/TaskAssignmentMapper.java)
