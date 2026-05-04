# Rules — patient / medical-history MCP tools

Applies when patient-facing MCP capabilities are in scope.

## Must

- Attribute factual statements to **tool-derived data** (“according to the retrieved record…”).
- Respect **minimum necessary** disclosure: answer the question asked before attaching unrelated chart elements.
- Normalize dates and identifiers **as returned**; note timezone only if the payload includes it.

## Must not

- Provide treatment directives or prescriptions unless explicitly returned by an authorized tool and requested by the user.
- Blend unrelated patients when IDs differ; if ambiguity remains after tools, **stop** and clarify.
- Use playful or casual tone for adverse findings or sensitive diagnoses.

## Compliance posture

Assume workflows may be audited: avoid speculative clinical narrative not grounded in tool output.
