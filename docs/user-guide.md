# MCP Client User Guide

This guide explains how to operate the `mcp-client-demo` application and keep its business behavior stable while you configure or extend it.

## 1) What this app does

The app is a Quarkus MCP client that:

- Accepts chat messages over HTTP (`/api/chat`)
- Decides whether MCP tools are allowed for that message
- Calls MCP tools when appropriate
- Returns assistant replies
- Also exposes direct MCP tool discovery and invocation endpoints (`/api/mcp/tools`, `/api/mcp/invoke`)

In short: normal chat stays conversational, and patient/medical intents can trigger MCP-backed operations.

## 2) Business functionality that must remain intact

To preserve current behavior, keep these invariants:

- **Tool gating remains active**: `app.chat.mcp-tools.mode` controls whether tools are used (`ALWAYS`, `AUTO`, `NEVER`).
- **AUTO mode remains intent-driven**: the regex in `app.chat.mcp-tools.auto-trigger-pattern` should continue matching patient/medical/onboarding requests.
- **Guidance remains enabled**: `app.chat.agent-guidance.enabled` should stay `true` unless intentionally disabling guidance behavior.
- **Guidance mapping remains valid**: every `skills-file` / `rules-file` in `app.chat.agent-guidance.mappings` must exist under `classpath:guidance/`.
- **MCP client stays enabled**: `quarkus.langchain4j.mcp.enabled=true` and MCP client transport settings remain valid.

If any of these are changed, chat behavior can drift from the intended business flow.

## 3) Run the application

## Prerequisites

- Java 17
- Maven
- At least one configured LLM provider:
  - **Ollama** (default profile): local Ollama running on `http://localhost:11434`
  - **Gemini**: set `spring.ai.google.genai.api-key`
- At least one reachable MCP server (default example uses SSE server at `http://localhost:8081/sse`)

## Start command

```bash
mvn quarkus:dev
```

Default server port: `8080`.

## 4) Key configuration reference

All main runtime behavior is in `src/main/resources/application.properties`.

- `quarkus.profile`: selects provider profile (`ollama` by default)
- `quarkus.langchain4j.mcp.*`: MCP server connections
- `app.chat.system-prompt`: base assistant behavior
- `app.chat.mcp-tools.mode`: tool usage mode
- `app.chat.mcp-tools.auto-trigger-pattern`: AUTO-mode trigger regex
- `app.chat.agent-guidance.*`: skill/rule augmentation setup

## 5) API usage

## Chat endpoint

- **POST** `/api/chat`
- Request:

```json
{
  "message": "Show John Doe medical history"
}
```

- Response:

```json
{
  "reply": "..."
}
```

Behavior notes:

- Small talk/general requests should usually answer without tool calls.
- Patient/medical/onboarding-style requests should allow tool usage (in `AUTO` mode when matched by regex).

## Direct MCP endpoints

### List available tools

- **GET** `/api/mcp/tools`
- Returns tool metadata including `name`, `description`, `inputSchema`, `originalToolName`.

### Invoke a tool directly

- **POST** `/api/mcp/invoke`
- Request:

```json
{
  "toolName": "patient-http__find_patient",
  "arguments": {
    "patientId": "123"
  }
}
```

- Response:

```json
{
  "toolName": "patient-http__find_patient",
  "result": "..."
}
```

Important:

- If names are ambiguous across servers, use the prefixed tool `name` from `/api/mcp/tools`.
- If `toolName` is blank, request fails.
- If MCP callbacks are unavailable, invocation fails with configuration guidance.

## 6) Functional validation checklist (after any config change)

Run these checks to confirm business behavior is preserved:

1. **General chat check**: ask a greeting (`"Hi"`). Ensure a normal reply is returned.
2. **Patient intent check**: ask a patient data query (`"Find patient John Doe"`). Ensure MCP-capable behavior is observed.
3. **Tool inventory check**: call `GET /api/mcp/tools` and confirm expected tools are listed.
4. **Direct invoke check**: call `POST /api/mcp/invoke` with a known tool and valid arguments.
5. **Guidance check**: verify skill/rule files referenced in mappings are present and non-empty.
6. **Profile check**: ensure active profile and provider credentials are correct (`ollama` or `gemini`).

## 7) Safe change policy for ongoing maintenance

When updating this client, apply these guardrails:

- Prefer changing one concern at a time (LLM profile, MCP transport, or guidance mapping).
- Keep `AUTO` mode unless business requirements explicitly demand `ALWAYS` or `NEVER`.
- If regex changes, validate both positive and negative examples before release.
- Do not remove guidance folders without updating mappings.
- Keep logs enabled at least at `INFO` in non-local environments for operability.

## 8) Common failure patterns

- **No tools listed at `/api/mcp/tools`**
  - Check MCP server reachability and `quarkus.langchain4j.mcp.enabled=true`.
- **Tool invoke says unknown/ambiguous tool**
  - Re-fetch `/api/mcp/tools`; use exact prefixed `name`.
- **Chat never calls tools in AUTO mode**
  - Check `auto-trigger-pattern` and whether messages still match expected intent terms.
- **Gemini profile fails**
  - Ensure `quarkus.langchain4j.ai.gemini.api-key` is set and profile is active.

## 9) File map for operators

- Main runtime config: `src/main/resources/application.yml`
- Chat routing/gating logic: `src/main/java/com/mcp/demo/service/McpChatService.java`
- Tool mode logic: `src/main/java/com/mcp/demo/config/ChatAppProperties.java`
- Guidance loading: `src/main/java/com/mcp/demo/service/MarkdownToolGuidance.java`
- Chat API: `src/main/java/com/mcp/demo/controller/ChatController.java`
- Direct MCP API: `src/main/java/com/mcp/demo/controller/McpDirectController.java`

