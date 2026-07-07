# Contributing

Thank you for contributing to the Cheqi Java SDK.

## Development Requirements

- Java 11 or newer.
- Maven 3.6 or newer, or Gradle.
- No production API keys, OAuth access tokens, customer data, or merchant data in
  commits, tests, logs, or screenshots.

## Getting Started

Clone the repository and build the project:

```sh
git clone https://github.com/cheqi-io/cheqi-sdk-java.git
cd cheqi-sdk-java
./gradlew build
```

You can also run the Maven build:

```sh
mvn test
```

## Making Changes

- Create a branch from `main`.
- Keep changes focused and include tests for behavior changes.
- Preserve Java 11 compatibility.
- Follow the existing package structure and coding style.
- Use sandbox or mocked API responses for tests. Do not depend on production
  Cheqi services in automated tests.

## Generated Models

Some model classes are generated from `openapi.yaml`. When changing generated
API models, update the OpenAPI specification and regenerate the affected code
instead of hand-editing generated output where possible.

## Pull Requests

Before opening a pull request:

```sh
./gradlew test
mvn test
```

In the pull request description, include:

- A summary of the change.
- Any behavior or API compatibility impact.
- The tests you ran.
- Any follow-up work or known limitations.

## Security Issues

Do not open public issues for security vulnerabilities. Follow
`SECURITY.md` instead.
