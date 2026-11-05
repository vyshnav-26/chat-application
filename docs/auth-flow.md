# Authentication flow

```mermaid
sequenceDiagram
    participant Browser
    participant API
    participant DB
    Browser->>API: POST /auth/register (username, password)
    API->>API: Validate input and BCrypt-hash password
    API->>DB: Insert user and password_hash
    DB-->>API: User id and username
    API-->>Browser: 201 {id, username}
    Browser->>API: POST /auth/login (username, password)
    API->>DB: Load user and verify BCrypt hash
    API-->>Browser: 200 {token, tokenType: "Bearer"}
    Browser->>API: Protected request (Authorization: Bearer token)
    API->>API: Validate signature and expiry, load user principal
    API-->>Browser: Protected response
```

The browser keeps the token in local storage and sends it in the `Authorization` header. Invalid or expired tokens are rejected. The planned STOMP connection will carry the token in its `CONNECT` headers; token validation for STOMP is a later chat milestone.
