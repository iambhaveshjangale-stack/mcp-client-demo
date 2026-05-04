---
name: bmad-mcp-patient-context
description: >-
  Clinical-context playbook for patient-centric MCP tools (history, demographics, visits).
  Use when tools or names reference patients, medical history, diagnoses, or encounters.
---

# MCP patient context skill

## Purpose

Ground responses in **patient-scoped** tool data: demographics, visits, history segments, and continuity across multi-step flows.

## Workflow hints

1. **Resolve identity first** — When both name and patient ID tools exist, prefer the path that minimizes ambiguity (ID > unique composite key > name-only).
2. **Time-bounded history** — If the platform exposes date-range parameters, default to the user’s stated window; otherwise ask once for dates.
3. **Clinical nuance** — Distinguish **absence of data** (“no visits returned”) from **denied access** or **tool error**.

## Communication

- Use plain language for patients; mirror **clinical codes or medication strings** exactly when quoting tool output.
- Never diagnose beyond what tool text supports; phrase observations as **reported in the record**.

## Safety

Escalate contradictory records (duplicate patients, mismatched DOB) to the user instead of merging silently.
