# Contributing

Thanks for helping build the chat application. This guide covers issue selection, implementation, verification, and pull request review.

## Choose and track work

- Check the issue and its acceptance criteria before making changes. If the issue is unclear or conflicts with the architecture decisions in `chat-app-issue-plan.md`, clarify it before implementing.
- Work only on the scope described by the issue. If you find adjacent work, create or identify a separate issue rather than quietly expanding the change.
- Keep issues open while work is in progress. Once an issue is fully complete, reference it in the PR body with `Closes #<number>`; GitHub will close it automatically when that PR is merged. Do not close an issue manually before its completed work has been reviewed and merged.
- If a PR completes several related issues, include a separate `Closes #<number>` line for each issue whose acceptance criteria are fully met. Leave incomplete issues out of the closing references and describe any remaining work in the PR.

## Branches and commits

- Create a branch from the current default branch for the issue or tightly related issue batch.
- Use `issue-<number>-<short-slug>` for a single issue (for example, `issue-18-jwt-utility`). For an agreed related batch, identify the range or primary issue in the branch name.
- Use the existing commit prefixes: `feat:`, `fix:`, `chore:`, `docs:`, and `test:`.
- Keep commits focused and write the subject as a short description of the change. Include issue numbers when useful.
- Do not commit secrets, `.env` files, build output, `node_modules`, or other generated artifacts.

## Implementation guidelines

- Preserve the architecture decisions in `chat-app-issue-plan.md`: REST for login/history/room management; STOMP for live chat; stateless JWT authentication; persist messages before broadcasting; derive the sender from the authenticated principal; cursor-based history pagination; and schema changes through Flyway.
- Keep credentials and environment-specific values out of source code. Update `.env.example` files when adding configuration.
- Keep error responses consistent and avoid returning stack traces or sensitive data.
- Update the README or relevant docs when setup or behavior changes.
- Avoid adding features that are not covered by an issue.

## Verification

Run the checks that cover the files you changed, and report the commands and results in the PR description.

```sh
# Backend
(cd backend && ./mvnw test)

# Frontend
(cd frontend && npm run test:run && npm run build)
```

The backend test profile uses an in-memory database. For database migration changes, also verify the migrations against a clean PostgreSQL database when available.

Before opening the PR, check:

```sh
git diff --check
git status --short
```

## Pull request

- Open a PR from the issue branch to the default branch.
- Describe the change, scope, verification, and any limitations reviewers should know.
- Add `Closes #<number>` only for issues whose full acceptance criteria are satisfied by the PR. A linked issue without a `Closes` keyword will not be automatically closed.
- Complete the repository PR checklist and respond to review comments. Push follow-up commits to the same branch so the review stays together.
- Merge only after review and required checks are complete. The closing references then close their issues automatically.
