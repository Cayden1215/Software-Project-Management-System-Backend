# Multi-Member Task Assignment - Quick Reference

## Files Modified

### Core Changes (5 files)

| File | Change Type | Key Update |
|------|-------------|-----------|
| **TaskAssignment.java** | Entity | `assignedMember` → `assignedMembers` (Set), @ManyToMany relationship |
| **TaskAssignmentDto.java** | DTO | Single member fields → Lists, added `requiredMemberNum` |
| **GeneticAlgorithmSchedulerImpl.java** | Service | Gene class updated, multi-member selection & scheduling logic |
| **ProjectServiceImpl.java** | Service | Updated DTO conversion to handle member lists |
| **TaskAssignmentMapper.java** | New Mapper | Clean entity-to-DTO conversion utilities |

## Core Concepts

### Multi-Member Assignment Flow

```
Task.requiredMemberNum (e.g., 2)
        ↓
Genetic Algorithm selects N compatible members
        ↓
Gene stores Set<ProjectMember>
        ↓
Fitness evaluation checks ALL members' availability
        ↓
TaskAssignment.assignedMembers persists to DB
        ↓
API returns TaskAssignmentDto with member lists
```

### Key Methods

| Method | Purpose |
|--------|---------|
| `findCompatibleMembers()` | Find all members with required skills |
| `selectRandomMembers(set, n)` | Randomly select N members from pool |
| `evaluateFitness()` | Ensure ALL members free before task start |
| `isValidMemberCount()` | Validate assigned count matches requirement |

## Database Change

**New Join Table:**
```
task_assignment_members
├── assignment_id (FK → TaskAssignment)
└── project_member_id (FK → ProjectMember)
```

## API Change Example

### Before
```json
{
  "assignedMemberId": 101,
  "assignedMemberName": "John Doe"
}
```

### After
```json
{
  "assignedMemberIds": [101, 102],
  "assignedMemberNames": ["John Doe", "Jane Smith"],
  "requiredMemberNum": 2
}
```

## Validation

- ✓ Entity-level: `TaskAssignment.isValidMemberCount()`
- ✓ Service-level: Persists with validation checks
- ✓ DTO-level: Client-side validation support

## Next Steps

1. Generate database migration for `task_assignment_members` table
2. Run migration in development database
3. Execute unit tests for GA operations
4. Test API endpoints with new DTO structure
5. Update frontend to consume member lists instead of single member
