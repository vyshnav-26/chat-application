# Chat App: Issue Plan (Spring Boot, React, Postgres)

82 issues across 8 milestones. Each issue is small (under 2 hours) and mergeable on its own. Each has labels and "done when" criteria.

## Project brief (read first)

**Goal:** A real-time chat application with auth, rooms, direct messages, and persisted history.

**Stack (adjust versions if yours differ):**
- Backend: Java 21, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA, Spring WebSocket (STOMP), Flyway
- Frontend: React with Vite, React Router, Axios, `@stomp/stompjs`, plain Context or Zustand for state
- Database: PostgreSQL (via Docker Compose locally)

**Key decisions (do not change without asking):**
- REST for login, history, and room management. WebSocket (STOMP) only for live messages and events.
- Stateless JWT auth. The token is also validated on the STOMP `CONNECT` frame.
- Persist a message to the database first, then broadcast it.
- The sender ID always comes from the authenticated principal on the server, never from the client payload.
- Cursor-based pagination for history, not offset.
- Schema changes only through Flyway migrations. Never use `ddl-auto=update`.
- No secrets in the repo. Use environment variables and keep `.env.example` current.

**Working rules for the agent:**
1. Work on one issue at a time, in numeric order unless a dependency note says otherwise.
2. One branch and one PR per issue. Branch name: `issue-<number>-<short-slug>`.
3. Do not start the next issue until the current one meets its "done when" list.
4. If an issue is ambiguous or conflicts with a key decision, stop and ask instead of guessing.
5. Do not add features that are not in an issue.
6. Keep the README and docs accurate as code changes.

## Labels

| Label | Use for |
|---|---|
| `backend` | Spring Boot code |
| `frontend` | React code |
| `db` | Migrations, schema, indexes |
| `infra` | Docker, config, tooling, deployment |
| `security` | Auth, CORS, validation that protects data |
| `test` | Unit, integration, component tests |
| `docs` | READMEs, diagrams, API and design docs |
| `ux` | Loading states, banners, usability polish |

Create one GitHub milestone per section below (M1 to M8).

---

## M1: Setup

### #1 Init Spring Boot project
Labels: `backend` `infra`
- Includes Web, JPA, Security, Validation, WebSocket, and the Postgres driver.
- Done when: the app starts with `./mvnw spring-boot:run` (or Gradle equivalent) and a basic context-load test passes.

### #2 Docker Compose file for Postgres
Labels: `infra` `db`
- Done when: `docker compose up -d` starts Postgres with a named volume, and credentials come from env variables.

### #3 Connect app to Postgres via env-based `application.yml`
Labels: `backend` `infra`
- Done when: datasource URL, username, and password are read from environment variables, and the app connects on startup.
- Done when: no credentials are hard-coded in the repo.

### #4 Add Flyway with a baseline migration
Labels: `db` `backend`
- Done when: Flyway runs on startup, `V1__baseline.sql` exists, and `ddl-auto` is set to `validate` or `none`.

### #5 Global exception handler and standard error format
Labels: `backend`
- Done when: validation errors, not-found, and unexpected errors all return the same JSON shape (timestamp, status, message, optional field errors).
- Done when: stack traces are never exposed in responses.

### #6 Init React app with Vite and folder structure
Labels: `frontend` `infra`
- Done when: `npm run dev` serves the app, and folders exist for `pages`, `components`, `api`, `context`, `hooks`.

### #7 Add React Router with placeholder pages
Labels: `frontend`
- Done when: `/login`, `/register`, and `/chat` render placeholder pages, and unknown routes show a not-found page.

### #8 Axios instance with base URL from env
Labels: `frontend`
- Done when: a single Axios instance reads its base URL from a Vite env variable, and all API calls use it.

### #9 CORS config on the backend
Labels: `backend` `security`
- Done when: only the configured frontend origin is allowed (from env), with the needed methods and headers.
- Done when: the same allowed origin is used later for the WebSocket endpoint.

### #10 `.gitignore` and `.env.example` for both apps
Labels: `infra` `docs`
- Done when: build output, `node_modules`, IDE files, and `.env` files are ignored.
- Done when: each app has a `.env.example` listing every required variable with placeholder values.

### #11 README skeleton
Labels: `docs`
- Done when: the README has the project overview, tech stack, prerequisites, and quick-start steps that work on a clean machine.

### #12 CONTRIBUTING.md and templates
Labels: `docs` `infra`
- Done when: branch naming, commit style, and the PR checklist are documented.
- Done when: a PR template and an issue template exist in `.github/`.

---

## M2: Auth backend

### #13 Migration: `users` table
Labels: `db`
- Done when: the table has id, unique username, password_hash, created_at, and the migration applies cleanly on an empty database.

### #14 `User` entity and repository
Labels: `backend`
- Done when: the entity maps to the table, and the repository has `findByUsername` and `existsByUsername`.

### #15 Register request DTO and validation rules
Labels: `backend` `security`
- Done when: username length and allowed characters and password minimum length are validated, with clear error messages.

### #16 `POST /auth/register`
Labels: `backend` `security`
- Done when: passwords are hashed with BCrypt and never returned in any response.
- Done when: a duplicate username returns a 409 with the standard error format.

### #17 Security config
Labels: `backend` `security`
- Done when: sessions are stateless, CSRF is disabled for the API, and `/auth/**` is public while everything else requires authentication.

### #18 JWT utility
Labels: `backend` `security`
- Done when: it can generate, parse, and validate tokens, with the secret and expiry read from env variables.
- Done when: expired or tampered tokens are rejected, and unit tests cover both cases.

### #19 `POST /auth/login`
Labels: `backend` `security`
- Done when: valid credentials return a JWT, and invalid credentials return 401 with a generic message that does not reveal which field was wrong.

### #20 JWT filter for REST requests
Labels: `backend` `security`
- Done when: a valid `Authorization: Bearer` token authenticates the request, and missing or invalid tokens on protected routes return 401.

### #21 `GET /users/me`
Labels: `backend`
- Done when: it returns the current user's id and username, and never returns the password hash.

### #22 Unit tests for the auth service
Labels: `test` `backend`
- Done when: tests cover successful register, duplicate username, successful login, and wrong password.

### #23 Integration test: register, login, protected call
Labels: `test` `backend`
- Done when: one test registers a user, logs in, and calls `/users/me` with the token, and a second call without the token returns 401.

### #24 OpenAPI/Swagger UI
Labels: `docs` `backend`
- Done when: Swagger UI is available in the dev profile, and the auth endpoints are described with request and response examples.

### #25 Doc: auth flow
Labels: `docs`
- Done when: a doc in `docs/` has a sequence diagram of register, login, and authenticated requests, plus how the token is used for WebSocket.

---

## M3: Auth frontend

### #26 Register page
Labels: `frontend`
- Done when: the form validates inputs client-side, shows server errors, and redirects to login on success.

### #27 Login page
Labels: `frontend`
- Done when: a successful login stores the token and redirects to `/chat`, and failures show an error message.

### #28 Auth context
Labels: `frontend` `security`
- Done when: the context exposes the current user, token, login, and logout, and restores the session on page refresh.

### #29 Axios interceptor
Labels: `frontend` `security`
- Done when: the token is attached to every request, and a 401 response clears auth state and redirects to `/login`.

### #30 Protected route wrapper
Labels: `frontend` `security`
- Done when: unauthenticated users are redirected to `/login` when they open `/chat`.

### #31 Logout action
Labels: `frontend`
- Done when: logout clears the token and user state and returns the user to `/login`.

### #32 Component tests for login and register forms
Labels: `test` `frontend`
- Done when: tests cover validation messages, successful submit, and a server error display.

---

## M4: Core chat backend

### #33 Migration: `rooms`, `room_members`, `messages`
Labels: `db`
- Done when: foreign keys, a unique constraint on (room_id, user_id) for members, and a `type` column (direct or group) on rooms exist.

### #34 Entities and repositories
Labels: `backend`
- Done when: all three entities and repositories exist, and a repository test saves and reads a message.

### #35 Migration: seed one global room
Labels: `db`
- Done when: a room named "General" exists after migration, and new users are added to it on registration (or this is documented as a follow-up decision).

### #36 STOMP config
Labels: `backend`
- Done when: the `/ws` endpoint exists with the allowed origin from env, the application prefix is `/app`, and the broker prefix is `/topic`.

### #37 JWT auth on STOMP `CONNECT`
Labels: `backend` `security`
- Done when: a `ChannelInterceptor` validates the token from the CONNECT headers and sets the user principal.
- Done when: a connection without a valid token is rejected.

### #38 Authorize `SUBSCRIBE`
Labels: `backend` `security`
- Done when: a user can only subscribe to rooms they are a member of, and other attempts are rejected.

### #39 `MessageDTO` and `@MessageMapping` handler
Labels: `backend` `security`
- Done when: the handler takes the sender from the principal and ignores any sender field in the payload.
- Done when: sending to a room the user is not a member of is rejected.

### #40 Persist the message, then broadcast
Labels: `backend`
- Done when: the message is saved first, and the broadcast payload includes the saved id and timestamp.
- Done when: if saving fails, nothing is broadcast.

### #41 Message validation
Labels: `backend` `security`
- Done when: empty or whitespace-only messages and messages over the max length are rejected, and the limit is configurable.

### #42 Doc: WebSocket protocol
Labels: `docs`
- Done when: the doc lists the endpoint, auth handshake, destinations, payload shapes, and error behavior with an example frame for each.

---

## M5: Core chat frontend

### #43 Install `@stomp/stompjs` and STOMP client service
Labels: `frontend`
- Done when: a service creates a client with the JWT in connect headers and the URL from env.

### #44 WebSocket context
Labels: `frontend`
- Done when: the context connects after login, disconnects on logout, and exposes connection state.
- Done when: there is only one client instance for the whole app.

### #45 `useRoomSubscription` hook
Labels: `frontend`
- Done when: it subscribes to a room on mount, unsubscribes on unmount or room change, and passes incoming messages to a callback.

### #46 `sendMessage` function
Labels: `frontend`
- Done when: it publishes to the correct destination and does nothing if the client is disconnected.

### #47 Chat page layout
Labels: `frontend` `ux`
- Done when: the layout has a sidebar and a main area, and works on desktop and mobile widths.

### #48 `MessageList` component
Labels: `frontend`
- Done when: it renders a list of messages with stable keys and handles an empty list.

### #49 `MessageInput` component
Labels: `frontend`
- Done when: Enter sends, Shift+Enter adds a newline, empty input is blocked, and the field clears after sending.

### #50 Message bubble
Labels: `frontend` `ux`
- Done when: own and other users' messages look different, and each shows the sender name and a formatted timestamp.

### #51 Auto-scroll to latest message
Labels: `frontend` `ux`
- Done when: the list scrolls to the bottom on new messages only if the user was already near the bottom.

### #52 Doc: frontend architecture
Labels: `docs` `frontend`
- Done when: the doc explains folder structure, state approach, the WebSocket context, and the main hooks.

---

## M6: History

### #53 Migration: index on messages
Labels: `db`
- Done when: an index on `messages(room_id, created_at DESC)` exists and the migration applies cleanly.

### #54 `GET /rooms/{id}/messages?before=&limit=`
Labels: `backend`
- Done when: results are newest first, limited by `limit` (with a maximum), and filtered by the `before` cursor.
- Done when: non-members get 403, and the response includes what the client needs to request the next page.

### #55 Load initial history on room open
Labels: `frontend`
- Done when: opening a room loads the latest page and displays it in chronological order, with live messages appended after.

### #56 Infinite scroll upward
Labels: `frontend` `ux`
- Done when: scrolling to the top loads older messages, the scroll position is preserved, and loading stops when there are no more messages.

### #57 Doc: pagination decision record
Labels: `docs`
- Done when: the doc explains why cursor-based pagination was chosen over offset, and describes the cursor format.

---

## M7: Rooms and DMs

### #58 `POST /rooms`
Labels: `backend`
- Done when: it creates a group room, adds the creator as a member, and validates the name.

### #59 `GET /rooms`
Labels: `backend`
- Done when: it returns only rooms the current user belongs to.

### #60 Join and leave room endpoints
Labels: `backend`
- Done when: joining is idempotent, leaving removes membership, and direct rooms cannot be left or joined this way.

### #61 Room sidebar list UI
Labels: `frontend`
- Done when: the sidebar lists the user's rooms and highlights the active one.

### #62 Create-room modal
Labels: `frontend`
- Done when: the modal validates the name, creates the room, and the new room appears in the sidebar.

### #63 Room switching
Labels: `frontend`
- Done when: switching unsubscribes from the old room, subscribes to the new one, and loads its history without showing the previous room's messages.

### #64 `GET /users/search?q=`
Labels: `backend`
- Done when: it returns matching usernames (limited count) and excludes the current user and sensitive fields.

### #65 `POST /dm/{userId}`
Labels: `backend`
- Done when: it returns the existing direct room if one exists, creates one otherwise, and never creates duplicates for the same pair.

### #66 Start-DM UI
Labels: `frontend`
- Done when: the user can search for someone, start a DM, and the DM appears in the sidebar and opens.

### #67 Doc: database schema and ER diagram
Labels: `docs` `db`
- Done when: the doc has an ER diagram and a short description of each table and its constraints.

---

## M8: Polish and ship

### #68 Reconnect with exponential backoff
Labels: `frontend`
- Done when: after a dropped connection the client retries with increasing delays, resubscribes to the active room, and stops retrying after logout.

### #69 Connection status banner
Labels: `frontend` `ux`
- Done when: a banner shows while disconnected or reconnecting and disappears when connected.

### #70 Presence tracking (backend)
Labels: `backend`
- Done when: connect and disconnect events update an online set, and presence changes are broadcast.
- Done when: multiple tabs for one user do not mark them offline prematurely.

### #71 Presence UI
Labels: `frontend` `ux`
- Done when: online users show a dot in the room and DM lists, updating live.

### #72 Typing indicator: backend event
Labels: `backend`
- Done when: typing events are broadcast to the room (excluding the sender) and are never persisted.

### #73 Typing indicator: UI
Labels: `frontend` `ux`
- Done when: "X is typing..." shows and clears after a short timeout, and typing events are throttled.

### #74 Unread counts per room
Labels: `backend` `frontend`
- Done when: each room shows an unread count that updates live and resets when the room is opened.

### #75 Loading, empty, and error states
Labels: `frontend` `ux`
- Done when: every page and list has a loading state, an empty state, and an error state with a retry where it makes sense.

### #76 Integration test for WebSocket send and receive
Labels: `test` `backend`
- Done when: a test connects two authenticated clients to a room, sends a message from one, and verifies the other receives it and that it was persisted.

### #77 Dockerfile for backend
Labels: `infra` `backend`
- Done when: a multi-stage build produces a small image that runs with env variables only.

### #78 Dockerfile for frontend
Labels: `infra` `frontend`
- Done when: the image serves the production build, and the API and WebSocket URLs are configurable.

### #79 Deploy
Labels: `infra`
- Done when: backend, frontend, and managed Postgres are running online, HTTPS and secure WebSocket (wss) work, and no secrets are committed.

### #80 Doc: deployment guide
Labels: `docs` `infra`
- Done when: the doc lists every environment variable, build and deploy steps, and how to roll back.

### #81 Final README
Labels: `docs`
- Done when: the README covers features, screenshots, setup, running tests, and a link to the API docs.

### #82 CHANGELOG and v1.0 release notes
Labels: `docs`
- Done when: the changelog lists what is in v1.0 and a tagged release exists.

---

## Parallel work

- Frontend issues 26 to 31 can start once issue 19 is merged, or against a mocked API.
- Issues 43 to 46 can be done before the backend handler is finished if the message payload shape (issue 42) is agreed first.
- Doc issues can be picked up right after the code they describe, or batched at the end of each milestone.
- Issues 64 to 66 (DMs) don't block anything else, so they're safe to defer.

## Scope rule

Do not create issues for features that may never be built (read receipts, file sharing, reactions). Add them only when they are actually next.
