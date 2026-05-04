---
name: bmad-mcp-onboarding
description: >-
  Operational playbook for patient onboarding and registration MCP tools.
  Use when tool names or user intent involve registration, onboarding, sign-up,
  or adding new patient records.
---

# MCP onboarding / registration skill

## Purpose

Keep assistant behavior consistent when **registration-style** MCP tools run: confirm intent, collect required fields from tool schemas only, and reflect API outcomes accurately.

## Principles

1. **Schema-first** — Tool argument names and types come from the MCP tool definition; never invent JSON keys.
2. **Confirm destructive writes** — If a tool creates or mutates patient identity data, summarize what will happen before implying success.
3. **Partial data** — If the tool returns validation errors or missing fields, quote them and ask the user for the next concrete input.

## Response shape

- Summarize **what the tool did** in one short sentence.
- Surface **IDs or confirmation tokens** returned by the tool verbatim when useful.
- If the tool fails, state **error text** from the result without blaming infrastructure unless the message says so.

## Escalation

If registration logic is ambiguous from tool output alone, ask a single clarifying question rather than guessing demographics or consent.
