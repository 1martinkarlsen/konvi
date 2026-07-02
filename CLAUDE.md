# Konvi

A Kotlin web framework for safe and productive backend development.

## Module map

- `core/` — the framework itself. Wraps Ktor (HTTP), Exposed (database), and kotlin-inject (DI).
- `cli/` — the `konvi` command-line tool (`konvi new`, ...). Deliberately Clikt-only: no kotlin-inject/KSP here, deps are injected manually via constructor defaults. This is a narrow, module-specific constraint, not a project-wide philosophy — everywhere else, Konvi prefers adopting mature libraries over building mechanisms in-house.
- `gradle-plugin/` — the Gradle plugin applied by generated projects.
- `ksp/` — the annotation processor (`KonviProcessor`) that powers classpath scanning.
- `sample/` — an in-repo example app. Uses `project(":core")` directly, unlike generated projects which consume the published `com.konvi` artifacts.

## Architectural conventions

**Classpath scanning over manual registration.** `@Route`, `@Middleware`, `@Authenticator`, and `Lifecycle` implementations are all auto-discovered via KSP (`KonviProcessor`), never manually registered by the user. When adding a new discoverable concept to the framework, follow this same pattern rather than introducing a different registration mechanism.

**Dependency stance.** Konvi does not generally avoid third-party libraries — it adopts mature ones (Ktor, Exposed, kotlin-inject) rather than reinventing them. Default to "pull in the established library" unless there's a specific, module-scoped reason not to (like the CLI's minimal-deps constraint).

## Design priority: fail fast

Avoiding runtime issues is a top priority for this framework. When there's a trade-off, prefer surfacing errors earlier over staying lenient: build-time > startup-time > first-request-time > deep-runtime. That said, get the failure *scope* right — don't let over-eager failure itself become a reliability problem (e.g. a rolling deploy shouldn't crash on a benign, in-progress schema difference).
