# hocon-playground

## What this codebase does
- Online HOCON (Human-Optimized Config Object Notation) playground that parses and renders HOCON input in real-time.
- Provides a web interface for users to input HOCON, toggle parsing options (resolve references, JSON output, formatting, comments), and view the parsed output.
- Uses the Play Framework (Scala) for the backend, with a single-page frontend that sends HOCON input to the server for parsing and displays the result.
- Explicitly prohibits HOCON `include` directives to prevent arbitrary file reads or SSRF.

## Auth shape
- No authentication or session management. The application is entirely public.
- Uses Play Framework's `play.http.secret.key` for CSRF protection (default in Play).
- Host allowlist configured via `play.filters.hosts.allowed` to restrict Host headers.
- No user data or credentials are handled.

## Threat model
- **HOCON Parsing Risks**: Malicious HOCON input could trigger edge cases in the `typesafe-config` library (e.g., DoS via deeply nested structures, memory exhaustion).
- **SSRF/Arbitrary File Reads**: HOCON `include` directives could read local files or internal resources. Explicitly blocked via a custom `ProhibitIncluder`.
- **XSS**: User-controlled output (parsed HOCON) is rendered as text in a `<pre>` tag, but dynamic DOM updates via `innerText` mitigate this.
- **CSRF**: Play Framework's built-in CSRF protection is enabled by default, but no explicit tokens are visible in the frontend.
- **Host Header Injection**: Host allowlist is configured, but misconfigurations could allow cache poisoning or open redirects.

## Project-specific patterns to flag
- **HOCON Parsing**: Ensure the `typesafe-config` library is up-to-date and that parsing options (e.g., `ConfigParseOptions`, `ConfigResolveOptions`) are hardened.
- **Custom `ConfigIncluder`**: The `ProhibitIncluder` must fully block all `include` directives (file, URL, classpath).
- **Query Parameter Handling**: Boolean query parameters (e.g., `resolve`, `json`) are parsed directly from user input without validation.
- **Error Handling**: Errors from `ConfigFactory.parseString` are returned directly to the client, potentially leaking internal details.
- **Content Security**: No CSP headers are configured in the HTML or Play Framework filters.

## Known false-positives
- **`play.http.secret.key`**: Hardcoded in `application.conf` but overridden by `APPLICATION_SECRET` in production.
- **Error Messages**: Raw error messages from `typesafe-config` are returned to the client, but these are not sensitive (e.g., parsing errors).
- **Host Allowlist**: Includes `localhost:9000` for development, but this is not exposed in production (hosted on `hocon-playground.avelier.dev`).
