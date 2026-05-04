# Rules — data fetch / list / search MCP tools

Applies when retrieval MCP tools (find, list, search, get, fetch) are relevant.

## Must

- Treat tool output as **authoritative** for factual answers in this turn.
- Redact or aggregate **sensitive fields** if the user did not ask for them (show minimum necessary).
- When repeating structured data, prefer **tables or bullets** over long prose.

## Must not

- Infer records that are not in the payload (no hallucinated rows).
- Call broad listing tools when a **narrower** filter tool exists and the user gave specific identifiers.
- Expose internal debugging identifiers unless the user explicitly needs them.

## Ordering

If two tools overlap in capability, prefer the tool whose **name and description** explicitly match the user’s requested operation.
