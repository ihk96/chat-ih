AGENTS NOTES

Purpose
- Keep key decisions and context so future sessions can continue without re-discovery.

Project
- Kotlin + Spring Boot 3.5.x, JPA, Flyway, Spring Security.
- Base package: com.inhyuk.chat
- Admin APIs live under /api/v1/admin/** and require ADMIN role.

Provider domain
- Entity: LlmProviderEntity (table ai_providers).
- Columns: id (string), name (unique, not null), provider (string enum),
  status (ACTIVE/INACTIVE), base_url, api_key, extra_config (TEXT JSON).
- No separate credentials object; apiKey is stored in config columns.
- Provider enums: OPENAI, ANTHROPIC, GOOGLE, OPENAI_COMPATIBLE.
- API:
  - POST/GET/GET{id}/PUT/DELETE /api/v1/admin/providers
  - PATCH /api/v1/admin/providers/{id}/status
  - GET /api/v1/admin/providers/{id}/models (live provider API call)
- Response masks apiKey (apiKeyMasked).
- JSON converter: com.inhyuk.chat.common.jpa.JsonMapConverter.

Model domain
- Entity: LlmModelEntity (table ll_models).
- Columns: id, provider_id, origin_name, public_name, status, extra_config (TEXT JSON).
- completion_url is removed (not used).
- API:
  - POST/GET/GET{id}/PUT/DELETE /api/v1/admin/models
  - PATCH /api/v1/admin/models/{id}/status
  - GET /api/v1/models (active models with active providers)

Chat domain
- API:
  - POST /api/v1/chat/completions
- Request: { modelId, message }
- Behavior: uses selected active model + active provider, sends single-turn user message to provider via LangChain4j, returns single response text.
- No chat memory/session persistence in this flow.

Live model list logic (ProviderModelCatalogService)
- OpenAI: GET https://api.openai.com/v1/models with Authorization Bearer.
- Anthropic: GET https://api.anthropic.com/v1/models with x-api-key + anthropic-version
  (default 2023-06-01, can override via extra_config.apiVersion).
- Google: GET https://generativelanguage.googleapis.com/{apiVersion}/models?key=...,
  apiVersion default v1beta, override via extra_config.apiVersion.
- OpenAI-compatible: baseUrl required; GET {baseUrl}/v1/models with Authorization Bearer.
- If baseUrl ends with /v1, the /v1 path is not duplicated.

Reminder
- Update this file when major domain rules or API shapes change.

Session preferences
- Respond in Korean.
- 자유롭게 AGENTS.md에 개발 맥락/코드 위치/스타일 선호 등을 기록해도 됨.
