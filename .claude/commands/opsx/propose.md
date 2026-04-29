---
name: "OPSX: Propose"
description: Propose a new change - brainstorm first, then create OpenSpec change with all artifacts in one step
category: Workflow
tags: [workflow, artifacts, experimental, superpowers]
---

Propose a new change — runs `superpowers:brainstorming` to refine the idea
into a design spec, then turns that spec into proposal.md / design.md / specs / tasks.md.

I'll create a change with artifacts:
- proposal.md (what & why)
- design.md (how)
- specs/<capability>/spec.md (requirements)
- tasks.md (implementation steps, with TDD + review checkpoints baked in)

When ready to implement, run `/opsx:apply`.

---

**Input**: The argument after `/opsx:propose` is the change name (kebab-case), OR a description of what the user wants to build.

**Steps**

1. **If no input provided, ask what they want to build**

   Use the **AskUserQuestion tool** (open-ended, no preset options) to ask:
   > "What change do you want to work on? Describe what you want to build or fix."

   From their description, derive a kebab-case name (e.g., "add user authentication" → `add-user-auth`).

   **IMPORTANT**: Do NOT proceed without understanding what the user wants to build.

2. **Brainstorm to refine the idea (NEW)**

   Invoke the **superpowers:brainstorming** skill with the user's intent
   as input. This will:
   - Explore project context (read existing specs, files, recent commits)
   - Ask clarifying questions one at a time
   - Propose 2-3 approaches with tradeoffs
   - Present a final design and write the spec to
     `docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md`
   - Wait for user approval of the spec

   **Do not skip this step** — the brainstorming spec is the primary input
   to proposal/design generation. If the user resists ("just make the change"),
   you may run an abbreviated brainstorm (one clarifying question + one design
   proposal), but still produce a written spec.

   When the brainstorming skill completes, **note the path of the design
   spec doc** — you'll feed it as context into every artifact below.

3. **Create the change directory**
   ```bash
   openspec new change "<name>"
   ```
   This creates a scaffolded change at `openspec/changes/<name>/` with `.openspec.yaml`.

4. **Get the artifact build order**
   ```bash
   openspec status --change "<name>" --json
   ```
   Parse the JSON to get:
   - `applyRequires`: array of artifact IDs needed before implementation (e.g., `["tasks"]`)
   - `artifacts`: list of all artifacts with their status and dependencies

5. **Create artifacts in sequence until apply-ready**

   Use the **TodoWrite tool** to track progress through the artifacts.

   Loop through artifacts in dependency order (artifacts with no pending dependencies first):

   a. **For each artifact that is `ready` (dependencies satisfied)**:
      - Get instructions:
        ```bash
        openspec instructions <artifact-id> --change "<name>" --json
        ```
      - The instructions JSON includes:
        - `context`: Project background (constraints for you - do NOT include in output)
        - `rules`: Artifact-specific rules (constraints for you - do NOT include in output)
        - `template`: The structure to use for your output file
        - `instruction`: Schema-specific guidance for this artifact type
        - `outputPath`: Where to write the artifact
        - `dependencies`: Completed artifacts to read for context
      - **Read the brainstorming spec doc** (from step 2) and any completed dependency files for context
      - Create the artifact file using `template` as the structure, drawing primarily from the brainstorming spec
      - Apply `context` and `rules` as constraints — but do NOT copy them into the file
      - Show brief progress: "Created <artifact-id>"

   b. **Continue until all `applyRequires` artifacts are complete**
      - After creating each artifact, re-run `openspec status --change "<name>" --json`
      - Check if every artifact ID in `applyRequires` has `status: "done"` in the artifacts array
      - Stop when all `applyRequires` artifacts are done

   c. **If an artifact requires user input** (unclear context):
      - Use **AskUserQuestion tool** to clarify
      - Then continue with creation

6. **Cross-link the spec doc and the OpenSpec change**

   - Add a "References" line to the OpenSpec proposal.md pointing back to
     the brainstorming spec at `docs/superpowers/specs/...-design.md`
   - Add a "References" line to the brainstorming spec pointing forward
     to `openspec/changes/<name>/`

7. **Show final status**
   ```bash
   openspec status --change "<name>"
   ```

**Output**

After completing all artifacts, summarize:
- Brainstorming spec doc path (the high-level design)
- OpenSpec change name and location
- List of artifacts created with brief descriptions
- What's ready: "All artifacts created! Ready for implementation."
- Prompt: "Run `/opsx:apply` to start implementing — TDD + review checkpoints are baked into tasks.md."

**Artifact Creation Guidelines**

- Follow the `instruction` field from `openspec instructions` for each artifact type
- The schema defines what each artifact should contain - follow it
- Read the brainstorming spec (step 2) AND dependency artifacts before creating new ones
- Use `template` as the structure for your output file - fill in its sections
- **IMPORTANT**: `context` and `rules` are constraints for YOU, not content for the file
  - Do NOT copy `<context>`, `<rules>`, `<project_context>` blocks into the artifact
  - These guide what you write, but should never appear in the output

**Guardrails**
- ALWAYS run `superpowers:brainstorming` first — even when the user describes the change in detail. The skill produces a written spec that is the contract for proposal/design.
- Create ALL artifacts needed for implementation (as defined by schema's `apply.requires`)
- Always read dependency artifacts before creating a new one
- If context is critically unclear, ask the user — but prefer making reasonable decisions to keep momentum
- If a change with that name already exists, ask if user wants to continue it or create a new one
- Verify each artifact file exists after writing before proceeding to next
- The project's `openspec/config.yaml` rules MUST be honored — particularly the `tasks` rules that mandate TDD pairs (RED → GREEN) and per-group review checkpoints
