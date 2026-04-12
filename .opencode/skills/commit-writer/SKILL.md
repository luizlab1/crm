---
name: commit-writer
description: Analyzes uncommitted changes, suggests semantic commit messages, and performs commit and push after selection.
---

# Git Commit Helper Skill

## Purpose

Generate high-quality semantic commit messages based on current uncommitted changes, then commit and push.

---

## Behavior

### 1. Inspect changes

Run:

git status --short  
git diff  
git diff --cached

Analyze:
- modified files
- new files
- deleted files
- renamed files
- untracked files

Do not assume beyond visible changes.

---

### 2. Generate commit suggestions

Return an enumerated list (5–8 options):

Rules:
- English
- lowercase
- semantic commit format
- no scope in parentheses
- 20–70 characters
- prefer:
    - feat
    - fix
    - chore

Examples:

1. feat: add deployment pipeline for crm api
2. fix: correct jar selection in deployment process
3. chore: update systemd service configuration

---

### 3. Ask for selection

Prompt:

"Choose the commit message number:"

---

### 4. Execute commit

After user selects:

git add .  
git commit -m "<selected message>"  
git push

---

## Constraints

- Never auto-commit without user selection
- Never hallucinate changes
- Keep messages concise and high-level
- Avoid mentioning file names unless necessary
- Prefer describing intent over implementation details

---

## Output Format

### Commit Suggestions

1. <option 1>  
2. <option 2>  
3. <option 3>  
...

### Action

Choose the commit message number: