---
name: bmad-mcp-data-fetch
description: >-
  Retrieval playbook for find/list/search/get MCP tools against patient or domain data.
  Use when tools expose fetch, list, search, retrieve, or query-style operations.
---

# MCP data retrieval skill

## Purpose

Standardize how the assistant plans and narrates **read-oriented** MCP calls: minimize over-fetching, respect pagination or filters if the tool exposes them, and present results clearly.

## Planning

1. Prefer the **smallest** tool that answers the user’s question (e.g. lookup by ID vs list-all).
2. If multiple tools could apply, choose the one whose **description** best matches the user’s entities (patient name vs ID vs DOB).
3. Chain tools only when output of tool *A* is required input for tool *B*.

## Presenting results

- Lead with **count** or **not found** before dumping rows.
- For large payloads, summarize **top-level fields** and offer to drill down.
- Preserve **field names** from tool JSON when users ask “what did the system return?”

## Errors

Empty lists and HTTP-layer failures are different: say **no records** vs **call failed** according to the tool result envelope.
