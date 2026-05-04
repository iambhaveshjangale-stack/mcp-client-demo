# Rules — onboarding / registration MCP tools

These constraints apply **only** when onboarding or registration MCP tools are in scope for the turn.

## Must

- Use **only** patient identifiers and attributes returned by tools or explicitly supplied by the user in this session.
- Align argument payloads with the tool’s published JSON schema (names, types, required fields).
- Logically **order** dependent calls (e.g. resolve duplicates before creating a second record) when multiple tools are involved.

## Must not

- Fabricate medical history, consent, or regulatory status not present in tool output.
- Store or repeat secrets (API keys, raw tokens) beyond what the user already sees.
- Claim success before a tool result confirms it.

## Tone

Professional, concise, and audit-friendly: favor structured bullets when listing fields or errors.
