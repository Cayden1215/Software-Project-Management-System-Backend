# Genetic Algorithm for Multi-Skill Resource-Constrained Project Scheduling

## Overview
This implementation provides a complete Genetic Algorithm (GA) solution for the Multi-Skill Resource-Constrained Project Scheduling Problem (MSRCPSP) in your Spring Boot application. The algorithm optimizes task scheduling by respecting skill requirements, task dependencies, and resource availability constraints.

## Architecture & Components

### 1. **Internal Data Structures** (defined as inner static classes)

#### Gene
Represents a single scheduled task with its assignment details:
- `Task task`: The task being scheduled
- `ProjectMember assignedMember`: The team member assigned to execute this task
- `int startTime`: Relative time (hours/days) from project start when the task begins

#### Chromosome
Represents a complete project schedule (candidate solution):
- `List<Gene> genes`: Ordered sequence of all scheduled tasks
- `int makeSpan`: Total project duration (max completion time of any task)
- `double fitnessScore`: Quality metric (1.0 / makeSpan) - higher is better

### 2. **Algorithm Phases**

#### Phase A: Population Initialization (`initializePopulation`)
- **Purpose**: Generate 50 initial random candidate schedules
- **Key Features**:
  - Random topological ordering of tasks (respecting dependency constraints)
  - **Skill Matching Constraint**: Ensures each ProjectMember assigned to a Task possesses all required skills
  - Uses `findCompatibleMember()` to filter eligible team members
  - Makes use of topological sort to maintain valid task ordering

#### Phase B: Fitness Evaluation (`evaluateFitness`)
Calculates the quality of each candidate schedule by iterating through all tasks:

**Constraint 1 - Task Dependencies**:
- If a task has predecessors in its `dependencies` set, its `startTime` cannot be earlier than the maximum completion time of all its predecessors
- Algorithm: `startTime ≥ max(predecessor_end_time)`

**Constraint 2 - Resource Availability**:
- Tracks each ProjectMember's availability timeline
- A task's `startTime` must be assigned to the earliest possible time when the assigned member is free for the entire task duration
- Uses `memberAvailability` map to track when each team member becomes available

**Calculation**:
- **MakeSpan**: Maximum end time across all tasks
- **Fitness Score**: `1.0 / makeSpan` (lower makespan = higher fitness)

#### Phase C: Parent Selection (`selectParents`)
- **Method**: Tournament Selection (top 50% of population)
- **Rationale**: Selects the best-performing schedules to produce the next generation
- Chromosomes with higher fitness scores have greater probability of reproduction

#### Phase D: Crossover (`crossover`)
Uses two simultaneous strategies to create child schedules:

**Strategy 1 - Order Crossover (OX) for Task Sequence**:
- Selects two random crossover points in the parent chromosomes
- Child 1: Takes task segment from Parent A between crossover points
- Fills remaining tasks in Child 1 using sequence order from Parent B (avoiding duplicates)
- Child 2: Uses Parent B's segment and fills from Parent A

**Strategy 2 - Resource Crossover for Team Assignments**:
- For every task assigned in the child: 50% probability to inherit Parent A's assignment
- Otherwise (50%): Inherit Parent B's assignment or find an alternative compatible member
- Ensures new assignments still satisfy skill matching constraints

#### Phase E: Mutation (`mutate`)
Applies random variations with 8% mutation rate to escape local optima:

**Mutation Type 1 - Swap Mutation** (50% of mutations):
- Randomly selects two task positions in the chromosome's task sequence
- Swaps these tasks to discover different topological orderings
- Helps explore diverse scheduling arrangements

**Mutation Type 2 - Reassignment Mutation** (50% of mutations):
- Randomly selects a task
- Reassigns it to a **DIFFERENT** ProjectMember who possesses all required skills
- Introduces diversity in resource allocation

#### Phase F: Termination & Persistence (`scheduleProject`)
- **Evolution Loop**: Runs for 100 generations with elitism (keeping top 10% of solutions)
- **Final Selection**: Extracts the single Chromosome with the highest fitnessScore
- **Date Conversion**: Converts relative integer `startTime` into actual `LocalDate` objects
  - Calculation: `scheduled_date = project.startDate + Gene.startTime days`
- **Database Persistence**: Creates and saves `TaskAssignment` entities with:
  - Task, assigned ProjectMember, scheduled start/end dates
  - Links assignments to the Project

## How to Use

```java
// In a controller or service
@Autowired
private GeneticAlgorithmScheduler gaScheduler;

// Schedule a project (generates optimal task assignments)
gaScheduler.scheduleProject(projectID);

// Results are persisted in TaskAssignment table
```

## Algorithm Parameters

```java
POPULATION_SIZE = 50           // Number of candidate schedules per generation
MAX_GENERATIONS = 100          // Number of evolution cycles
MUTATION_RATE = 0.08           // 8% chance of mutation per chromosome
ELITISM_RATE = 0.1             // Keep top 10% elite solutions
```

## Helper Methods

### Topological Sorting
- `generateRandomTopologicalOrder()`: Creates random but valid task orderings respecting dependencies
- `topologicalSortUtil()`: DFS-based utility for topological sort

### Skill Matching
- `findCompatibleMember()`: Finds a team member with all required skills
- `hasAllRequiredSkills()`: Validates skill compatibility

## Constraints Enforced

1. ✅ **Skill Matching**: Tasks only assigned to members with required skills
2. ✅ **Task Dependencies**: Respects task prerequisite ordering
3. ✅ **Resource Availability**: No team member assigned to overlapping tasks
4. ✅ **Feasibility**: All generated schedules maintain topological validity

## Performance Characteristics

- **Time Complexity**: O(POPULATION_SIZE × MAX_GENERATIONS × TASKS × MEMBERS)
- **Space Complexity**: O(POPULATION_SIZE × TASKS)
- **Typical Runtime**: Depends on project size, usually completes in seconds for projects with <100 tasks

## Example Output

For a project with 20 tasks and 5 team members:
```
Chromosome[MakeSpan:45, Fitness:0.022222, Tasks:20]
Gene[Task:EstimateProject, Member:Alice, StartTime:0]
Gene[Task:DesignArchitecture, Member:Bob, StartTime:5]
Gene[Task:CodeBackend, Member:Alice, StartTime:10]
...
```

Each task is assigned with:
- Optimal start date (from project start + relative days)
- End date (start date + task duration)
- Qualified team member

## Dependencies

- `ProjectRepository`, `TaskRepository`, `ProjectMemberRepository`, `TaskAssignmentRepository`
- `ProjectService`, `TaskService`
- Added: `TaskAssignmentRepository` (new file)

## Future Enhancements

1. **Multi-Objective Optimization**: Minimize makespan AND balance workload
2. **Advanced Selection**: Implement Roulette Wheel or Rank-based selection
3. **Adaptive Parameters**: Dynamically adjust mutation rate based on convergence
4. **Constraint Handling**: Add resource cost, equipment availability, etc.
5. **Parallel GA**: Employ island model for distributed evolution
