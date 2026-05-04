# Agent guidance (BMAD-aligned layout)

Guidance lives under `src/main/resources/guidance/` and is appended to the system prompt when MCP tools match a regex (`application.yml` → `app.chat.agent-guidance.mappings`).

We follow **BMAD Method** conventions for **skills** (`SKILL.md`, YAML frontmatter). **Rules** live in the **same folder** as `RULE.md` without skill-style `name`/`description` frontmatter (BMAD WF-01 / WF-02).

## Directory layout (one folder per domain)

```
guidance/
  bmad-mcp-onboarding/
    SKILL.md
    RULE.md
  bmad-mcp-data-fetch/
    SKILL.md
    RULE.md
  bmad-mcp-patient-context/
    SKILL.md
    RULE.md
```

`base-path` defaults to `classpath:guidance/`. Mapping entries use paths like `bmad-mcp-data-fetch/SKILL.md` and `bmad-mcp-data-fetch/RULE.md`.

### Skills (`SKILL.md`)

| Convention | Detail |
|-------------|--------|
| **Filename** | Exactly `SKILL.md` (case-sensitive). |
| **Parent folder** | Kebab-case; must match the `name` in frontmatter (BMAD SKILL-05). |
| **Frontmatter** | YAML with **`name`** and **`description`** (BMAD SKILL-02 / SKILL-03). |
| **`name`** | `bmad-` prefix: `^bmad-[a-z0-9]+(-[a-z0-9]+)*$` (BMAD SKILL-04). |
| **`description`** | What + when to use; aim under 1024 chars (BMAD SKILL-06). |
| **Body** | Non-empty markdown after frontmatter (BMAD SKILL-07). |

### Rules (`RULE.md`)

| Convention | Detail |
|-------------|--------|
| **Location** | Same folder as `SKILL.md` for that domain. |
| **Filename** | `RULE.md`. |
| **Frontmatter** | Do **not** use `name` / `description` (BMAD WF-01 / WF-02). |

## Wiring

See `AgentGuidanceProperties` (`tool-pattern`, `skills-file`, `rules-file`) and `MarkdownToolGuidance`.

## External reference

[BMAD skill validator](https://github.com/bmad-code-org/BMAD-METHOD/blob/main/tools/skill-validator.md).
