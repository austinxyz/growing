---
name: "OPSX: Apply"
description: Implement tasks from an OpenSpec change with TDD + per-group code review (Experimental)
category: Workflow
tags: [workflow, artifacts, experimental, superpowers]
---

Implement tasks from an OpenSpec change. Wraps superpowers TDD discipline
and per-group code-review checkpoints around the apply loop.

**Input**: Optionally specify a change name (e.g., `/opsx:apply add-auth`). If omitted, check if it can be inferred from conversation context. If vague or ambiguous you MUST prompt for available changes.

**Steps**

1. **Select the change**

   If a name is provided, use it. Otherwise:
   - Infer from conversation context if the user mentioned a change
   - Auto-select if only one active change exists
   - If ambiguous, run `openspec list --json` to get available changes and use the **AskUserQuestion tool** to let the user select

   Always announce: "Using change: <name>" and how to override (e.g., `/opsx:apply <other>`).

2. **Check status to understand the schema**
   ```bash
   openspec status --change "<name>" --json
   ```
   Parse the JSON to understand:
   - `schemaName`: The workflow being used (e.g., "spec-driven")
   - Which artifact contains the tasks (typically "tasks" for spec-driven, check status for others)

3. **Get apply instructions**

   ```bash
   openspec instructions apply --change "<name>" --json
   ```

   This returns:
   - Context file paths (varies by schema)
   - Progress (total, complete, remaining)
   - Task list with status
   - Dynamic instruction based on current state

   **Handle states:**
   - If `state: "blocked"` (missing artifacts): show message, suggest using `/opsx:continue`
   - If `state: "all_done"`: congratulate, suggest archive
   - Otherwise: proceed to implementation

4. **Read context files**

   Read the files listed in `contextFiles` from the apply instructions output.
   The files depend on the schema being used:
   - **spec-driven**: proposal, specs, design, tasks
   - Other schemas: follow the contextFiles from CLI output

   **Also read the brainstorming spec** if proposal.md references one
   under `docs/superpowers/specs/` — it carries decisions and visual context
   not duplicated in the OpenSpec artifacts.

5. **Load TDD discipline (NEW)**

   Invoke the **superpowers:test-driven-development** skill ONCE at session
   start. This loads the iron-law TDD checklist into your working set:
     RED (failing test first) → verify failure → GREEN (minimal code)
     → verify pass → REFACTOR.

   Apply this discipline to every task that introduces new behavior or
   fixes a bug. Tasks marked "RED — write failing test" are the first
   half of a TDD pair; the next task is the GREEN implementation.

6. **Show current progress**

   Display:
   - Schema being used
   - Progress: "N/M tasks complete"
   - Remaining tasks overview
   - Dynamic instruction from CLI

7. **Implement tasks (loop until done or blocked, with per-group review)**

   For each pending task:
   - Show which task is being worked on
   - Make the code changes required (TDD discipline applies — see step 5)
   - Keep changes minimal and focused
   - Mark task complete in the tasks file: `- [ ]` → `- [x]`
   - Continue to next task

   **At each task-group boundary** (when transitioning from `## N` to `## N+1`):
   - Invoke **superpowers:requesting-code-review** on the diff for group N
     (`git diff` since the start of the group)
   - Surface CRITICAL/HIGH findings to the user
   - Address findings inline OR record them as new tasks before moving on
   - Do NOT skip this checkpoint — it's the quality gate

   **Pause if:**
   - Task is unclear → ask for clarification
   - Implementation reveals a design issue → suggest updating artifacts
   - Error or blocker encountered → report and wait for guidance
   - Code review surfaces a CRITICAL issue → fix before proceeding
   - User interrupts

8. **Final verification before completion (NEW)**

   When the last `## N` group is done, invoke
   **superpowers:verification-before-completion** to:
   - Run `mvn test` (backend) and `npm test` (frontend), confirm all green
   - Grep for `console.log` and any leftover debug statements
   - Diff review for stale TODOs / commented-out code

9. **On completion or pause, show status**

   Display:
   - Tasks completed this session
   - Overall progress: "N/M tasks complete"
   - If all done: suggest `/opsx:archive`
   - If paused: explain why and wait for guidance

**Output During Implementation**

```
## Implementing: <change-name> (schema: <schema-name>)
TDD discipline loaded. Per-group review checkpoints active.

Working on task 3/7 (Group 2): RED — write failing test for X
[...test written, run, RED confirmed...]
✓ Task complete

Working on task 4/7 (Group 2): GREEN — implement X
[...minimal impl, run test, GREEN...]
✓ Task complete

— end of Group 2 —
[invoke superpowers:requesting-code-review on diff]
✓ No CRITICAL findings; 1 MEDIUM noted as task 5.4 (deferred)
```

**Output On Completion**

```
## Implementation Complete

**Change:** <change-name>
**Schema:** <schema-name>
**Progress:** 7/7 tasks complete ✓
**Reviews:** N groups reviewed; CRITICAL=0, HIGH=0, MEDIUM=2 (addressed)
**Verification:** mvn test ✅ · npm test ✅ · no console.log ✅

### Completed This Session
- [x] Task 1
- [x] Task 2
...

All tasks complete! You can archive this change with `/opsx:archive`.
```

**Output On Pause (Issue Encountered)**

```
## Implementation Paused

**Change:** <change-name>
**Schema:** <schema-name>
**Progress:** 4/7 tasks complete

### Issue Encountered
<description of the issue>

**Options:**
1. <option 1>
2. <option 2>
3. Other approach

What would you like to do?
```

**Guardrails**
- Keep going through tasks until done or blocked
- Always read context files before starting (from the apply instructions output)
- **TDD is mandatory** for new behavior and bug fixes — invoke superpowers:test-driven-development at session start (step 5) and follow the iron law
- **Per-group review is mandatory** — invoke superpowers:requesting-code-review at every `## N` boundary (step 7)
- **Final verification is mandatory** — invoke superpowers:verification-before-completion before declaring all done (step 8)
- If task is ambiguous, pause and ask before implementing
- If implementation reveals issues, pause and suggest artifact updates
- Keep code changes minimal and scoped to each task
- Update task checkbox immediately after completing each task
- Pause on errors, blockers, or unclear requirements - don't guess
- Use contextFiles from CLI output, don't assume specific file names

**Fluid Workflow Integration**

This skill supports the "actions on a change" model:

- **Can be invoked anytime**: Before all artifacts are done (if tasks exist), after partial implementation, interleaved with other actions
- **Allows artifact updates**: If implementation reveals design issues, suggest updating artifacts - not phase-locked, work fluidly
- **Skill composition**: The TDD + review skills run as nested invocations within this command, not as separate slash commands
