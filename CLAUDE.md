# CLAUDE.md - AI Agent Guidelines

## Task List Management

You work in collaboration with other AI agents (e.g., Claude Code). To ensure there is no confusion and to keep all agents always up-to-date, please make sure to always check TASK_LIST.md to see what has been done since the last time, and update it with any new tasks before working on those tasks.

### Task Structure
- Each task should either have a checked or unchecked box
- A task can have subtasks if desired
- Notes can be assigned to both finished and unfinished tasks (e.g., future considerations, why a task needs to be revisited)
- If a long explanation/detail is warranted, create a new .md file and reference it as a note in the associated task to keep TASK_LIST.md from getting bloated
- You may remove/delete ONLY unfinished tasks if those tasks no longer need to be completed or are no longer applicable

### Task Organization
- Keep all completed items at the top, appending any new completed tasks to the bottom of the completed list
- This ordering helps track recent changes that may have induced problems
- Maintains easy reference for remaining tasks
- If a task points to another task list, there is no need to duplicate those tasks in TASK_LIST.md

### State Transitions
- Move item to "In Progress" before starting work
- When work is complete, move it to "Completed" with detailed description
- Always update TASK_LIST.md in the same commit as the completed work
- Include privacy and safety implications in task descriptions
- Note any language or UI aesthetic considerations
- Reference user experience impacts for frontend changes

## Guidelines for Changes

**IMPORTANT**: If carrying out a change would change the structure or possible function of something already existing and in-place, please verify before making the change, explaining what and how the change would impact, and what we can do to ensure whatever modifications don't break any already working functionality. This is probably one of the most important things, especially once things start getting a little more complicated.

If there is any uncertainty whatsoever, package all the required info for me to send to another AI agent (e.g., ChatGPT) for confirmation/assistance.

## Module Size Guidelines

### Target Module Size
- Aim for modules (files) to be no larger than 500-1000 lines of code
- This range is a general guideline and may vary based on the programming language and project context

### When to Split
- If a module exceeds 1000 lines, consider breaking it down into smaller, more focused modules

### How to Split
- Look for natural boundaries in the code, such as separate concerns, functionalities, or components
- Each new module should have a clear, well-defined purpose

### Signs a Module is Too Large
- **Understanding Difficulty**: The module's purpose or functionality is hard to grasp at a glance
- **Navigation Issues**: Frequent scrolling or jumping between different parts of the file is required
- **Cognitive Load**: High mental effort is needed to remember context or relationships

### Refactoring and Restructuring
- **Identify Cohesive Groups**: Extract cohesive groups of functions, classes, or components into separate modules
- **Create Folders and Regroup**: If a component gets too large with several specialized features, create a folder for that component and refactor features into individual files
- **Maintain Clear Responsibilities**: Ensure each new module has well-defined responsibility and clear interface
- **Update Documentation**: Reflect changes in module structure by updating relevant documentation

### Best Practices
- **Regular Reviews**: Periodically review and refactor code to keep module sizes manageable
- **Use Tools**: Leverage linters, code analysis tools, or IDE features to identify large or complex modules

## Git and Development Workflow

### Branching Strategy
- Work on feature or fix branches named `<type>/<descriptive-slug>` (e.g., `feat/conversation-ui`, `fix/auth-cors`)

### Commit Message Format
```
<type>: <subject line>

Body (wrap at 72 chars) explaining WHY. WHAT is visible in the diff.

Updates TASK_LIST.md: [task description if applicable]

Updates CHANGELOG.md: [changelog entry description if applicable]
```

Types: feat, fix, docs, refactor, chore, test, style

### Critical Commit Requirements
- Always commit changes whenever you update code
- Always update TASK_LIST.md in the same commit when completing work
- Always update CHANGELOG.md in the same commit when completing notable changes, tasks, or milestones
- Append CHANGELOG.md entries under [Unreleased] with references to task IDs
- **Project-Specific Safety Commits**: Any commit affecting user privacy, authentication, or core content must include clear explanation of privacy implications and confirmation that project-specific safety is maintained

### Development Standards
- **Single Feature Focus**: Only work on one feature at a time
- **Well Documented Code**: Write self-documenting or commented code
- **Explain Decisions**: Thoroughly explain your decisions to the user
- **Atomic Commits**: Each commit must compile, pass tests, and keep the app runnable

## Feature Regression Prevention

### No Feature Regression Rule
Never delete, rename, or materially alter existing classes, routes, tests, or UI elements unless:
- a) The change is explicitly listed in TASK_LIST.md under "In Progress", and
- b) A replacement of equal or greater capability ships in the same commit

### Escalation Process
If unsure whether a change is a regression or impacts existing functionality, create a `### Cursor-Next-Help-Wanted` entry and wait for feedback from another AI agent (e.g., ChatGPT)

### Protected Files
TASK_LIST.md, PROJECT_OVERVIEW.md, IMPLEMENTATION_DETAILS.md, CHANGELOG.md, and all .cursor/rules/* files must never be deleted. Modifications require explicit user approval and explanation of impact.

### Project Context Preservation
- Never modify or remove project-specific language, privacy controls, or safety features without explicit user consent
- The core aesthetic and nature of the UI must be preserved
- Never modify security rules, authentication flows, or user data schemas without explicit approval

## Documentation Synchronization

### When to Update Documentation
- After implementing new features
- When modifying existing user workflows
- When adding new UI components or interactions
- When changing tier-based features or restrictions
- When adding new error states or edge cases
- Before major releases or when preparing for testing

### Documentation Updates Required
When major features are completed, update relevant documentation:
- PROJECT_OVERVIEW.md for new user-facing features
- IMPLEMENTATION_DETAILS.md for technical architecture changes
- FIREBASE_SETUP.md (or equivalent setup file) for deployment or configuration updates
- CHANGELOG.md for notable changes, following the Keep a Changelog format

### Architecture Documentation
As components are implemented in the project architecture:
- Update IMPLEMENTATION_DETAILS.md with component specifications
- Document inter-component communication protocols
- Track privacy and safety implications of each component

### Project Feature Documentation
When adding project features:
- Document the rationale and approach
- Include privacy and safety considerations
- Reference best practices or research
- Update user-facing documentation with new capabilities
- Add entries to CHANGELOG.md under appropriate sections (e.g., Added, Changed) with task references

## Important Instruction Reminders
- Do what has been asked; nothing more, nothing less
- NEVER create files unless they're absolutely necessary for achieving your goal
- ALWAYS prefer editing an existing file to creating a new one
- NEVER proactively create documentation files (*.md) or README files. Only create documentation files if explicitly requested by the User

## General Rules

### Rules to Follow
- You must always commit your changes whenever you update code
- You must always try and write code that is well documented (self or commented is fine)
- You must only work on a single feature at a time
- You must explain your decisions thoroughly to the user

### Handshake Protocol
When the agent needs input from another AI (e.g., ChatGPT), structure the message exactly as:

```markdown
### Cursor-Summary
<concise, 3–10 sentence summary of current situation>

### Cursor-Diff-Stat
<`git diff --stat` output OR "n/a">

### Cursor-Next-Help-Wanted
<clear questions or "none">
```

- Headings must not be changed
- Omit Cursor-Diff-Stat only if no files were touched
- Attach large artefacts as zipped files and reference their path in Cursor-Next-Help-Wanted
- Always include context about which part of the project architecture or feature is being discussed
- Reference relevant TASK_LIST.md items and their current status

## Testing Guidelines

### Manual Testing Requirements
For every new route, UI component, or authentication flow:
- Test manually in the appropriate environment (e.g., browser for UI changes)
- Test API endpoints using relevant tools (e.g., docs or clients)
- Verify authentication flows work end-to-end

### Pre-commit Verification
Before every commit, verify:
- The application starts without errors
- Main entry points open without console errors
- Authentication flow works (login/signup/logout)
- No broken links or missing resources

### Project Safety Testing
For any changes affecting user data, privacy, or core content:
- Verify user data remains private and secure
- Test that core language remains appropriate
- Confirm no user data leaks in logs or error messages

### API Health Checks
Always verify health endpoints return healthy status after backend changes. Test that core connections (e.g., database) are working properly.

### Future Test Structure
When formal tests are added, create:
- tests/unit/ for individual component tests
- tests/integration/ for API endpoint tests
- tests/e2e/ for full user journey tests
- tests/security/ for privacy and authentication tests

## Documentation Standards

### Update Docs on Change
If code, API routes, or UI changes, update related documentation in the same commit:
- PROJECT_OVERVIEW.md for high-level feature changes
- IMPLEMENTATION_DETAILS.md for technical architecture updates
- Setup files (e.g., SETUP.md) for deployment or configuration changes
- API route docstrings for endpoint modifications
- CHANGELOG.md for notable changes, with entries under sections like Added, Changed, Fixed

### Project Documentation Standards
All user-facing text, error messages, and core content must:
- Use appropriate, non-judgmental language
- Maintain the established tone
- Include privacy and safety considerations
- Reference best practices where applicable

### Code Documentation
Use clear, descriptive docstrings for:
- All API endpoints with privacy implications
- Architecture components
- Authentication and security functions
- Integration methods

### Configuration Documentation
Keep configuration examples (e.g., .env.example) updated with:
- All required environment variables
- Clear descriptions of each setting
- Security notes for sensitive configurations
- Setup instructions for new developers

### Architecture Documentation
Update IMPLEMENTATION_DETAILS.md when:
- Adding new components to the architecture
- Modifying database schemas
- Changing authentication flows
- Adding new features

## Changelog Management

All notable changes to the project must be documented in CHANGELOG.md. The format is based on Keep a Changelog, and the project adheres to Semantic Versioning.

### Structure
- Maintain sections like [Unreleased] for ongoing changes
- Versioned sections like [Sprint X] - YYYY-MM-DD or [vX.Y.Z] - YYYY-MM-DD for releases
- Subsections: ### Added, ### Changed, ### Deprecated, ### Removed, ### Fixed, ### Security
- Plus custom ones like ### Technical Improvements, ### Coverage Achievements, ### Infrastructure, ### Next Steps

### When to Update CHANGELOG.md
- After completing a task: Append a brief entry under [Unreleased] with task ID reference (e.g., #### Task #123: Feature Description - Bullet points of additions)
- At sprint or milestone end: Organize [Unreleased] entries into a new versioned section, add summaries, achievements (e.g., test coverage), and next steps
- In the same commit as TASK_LIST.md updates for completed work
- Ensure entries are concise, reference task IDs, and cover code changes, technical improvements, infrastructure, and metrics

### Best Practices
- Link to external resources like Keep a Changelog in the file header
- Use markdown for readability (e.g., bullet points, bold for emphasis)
- Include dates in ISO format (YYYY-MM-DD)
- Mark completions with ✅ where appropriate
- Never remove historical entries; preserve the full change history

## Plan Synchronization

### Documentation Synchronization
When major features are completed, update relevant documentation:
- PROJECT_OVERVIEW.md for new user-facing features
- IMPLEMENTATION_DETAILS.md for technical architecture changes
- Setup files (e.g., SETUP.md) for deployment or configuration updates
- CHANGELOG.md with a summary of the milestone or sprint, including task references and achievements

### Architecture Tracking
As components are implemented in the project architecture:
- Update IMPLEMENTATION_DETAILS.md with component specifications
- Document inter-component communication protocols
- Track privacy and safety implications of each component

### Architecture Evolution Tracking
If core architecture changes beyond what's documented:
- Update IMPLEMENTATION_DETAILS.md with new patterns
- Document any breaking changes or migration requirements
- Ensure schema changes are properly documented
- Update API documentation for route changes
- Add to CHANGELOG.md under Deprecated or Removed if applicable

### Security & Privacy Documentation
For any changes affecting user data or privacy:
- Update security documentation
- Document new privacy controls or features
- Ensure safety measures are documented
- Update user consent and data handling documentation

### Development Milestone Tracking
At major development milestones:
- Update PROJECT_OVERVIEW.md with current capabilities
- Document completed vs. planned features
- Note any scope changes or architectural decisions
- Update deployment and setup instructions as needed
- Create a new versioned section in CHANGELOG.md (e.g., [Sprint X] - YYYY-MM-DD) with summaries, added/fixed items, and next steps