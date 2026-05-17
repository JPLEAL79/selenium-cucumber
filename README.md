# Selenium Cucumber Automation Framework

Web automation framework for end-to-end testing of web applications, built with Selenium WebDriver, Cucumber, JUnit 5, Maven, Docker, Jenkins, and Allure. The project supports local execution and CI/CD execution on AWS EC2 with Selenium Grid.

## Overview

This framework is designed to support local execution, containerized browser execution through Selenium Grid, and CI/CD execution through Jenkins pipelines. It follows common automation engineering practices such as Page Object Model, externalized test data, browser configuration by runtime parameters, structured logging, and CI reporting.

## Key Capabilities

- BDD scenarios with Cucumber feature files.
- Page Object Model for reusable UI interactions.
- Chrome and Firefox execution.
- Headless execution by default for CI environments.
- Optional visual debugging with noVNC.
- Selenium Grid orchestration with Docker Compose.
- Jenkins multibranch pipeline support.
- Parallel browser execution in AWS mode.
- Allure report generation after pipeline execution.
- Cloud-ready configuration without storing infrastructure secrets in the repository.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Automation | Selenium WebDriver |
| BDD | Cucumber |
| Test Engine | JUnit 5 |
| Build Tool | Maven |
| Browser Grid | Selenium Grid + Docker |
| CI/CD | Jenkins Pipeline |
| Reporting | Allure |
| Logging | SLF4J + Logback |
| Cloud Runtime | AWS EC2 |

## Project Structure

```text
selenium-cucumber/
|-- infra/
|   |-- debug/
|   |   |-- docker-compose-noVNC.yml
|   |-- jenkins/
|       |-- Dockerfile
|       |-- docker-compose-jenkins.aws.yml
|       |-- docker-compose-jenkins.yml
|-- src/test/java/
|   |-- commons/                 Test runner and shared utilities
|   |-- data/                    Test data resolvers
|   |-- definitions/             Cucumber step definitions and hooks
|   |-- framework/               Framework extensions
|   |-- pages/                   Page Object Model classes
|-- src/test/resources/
|   |-- features/                Cucumber feature files
|   |-- junit-platform.properties
|   |-- logback.xml
|   |-- allure.properties
|-- docker-compose.yaml          Selenium Grid
|-- Jenkinsfile                  CI/CD pipeline
|-- pom.xml
|-- README.md
```

## Prerequisites

- Java 17
- Maven 3.9+
- Docker
- Docker Compose

## Local Execution

Start Selenium Grid:

```bash
docker compose up -d
```

Run tests:

```bash
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
```

Run tests by tag:

```bash
mvn test -Dcucumber.filter.tags=@login
mvn test -Dcucumber.filter.tags=@purchase
```

Stop containers:

```bash
docker compose down
```

## Visual Debugging

Start Selenium Grid with noVNC enabled:

```bash
docker compose -f docker-compose.yaml -f infra/debug/docker-compose-noVNC.yml up -d
```

Browser sessions:

```text
Chrome:  http://localhost:7900
Firefox: http://localhost:7901
```

Run tests in visible mode:

```bash
mvn test -Dbrowser=chrome -Dheadless=false
mvn test -Dbrowser=firefox -Dheadless=false
```

## Jenkins Pipeline

The Jenkins pipeline supports two execution targets:

| Target | Description |
| --- | --- |
| `local` | Runs tests against a local Selenium Grid |
| `aws` | Runs tests against Selenium Grid running in AWS |

Start Jenkins locally:

```bash
docker compose -f infra/jenkins/docker-compose-jenkins.yml up -d
```

The pipeline cleans previous report artifacts, executes the selected browser strategy, and publishes the latest Allure report.

## AWS Execution

The framework is prepared for EC2-based execution with Jenkins and Selenium Grid running in Docker. In AWS mode, Jenkins connects to Selenium Grid through the internal Docker network and runs Chrome and Firefox in parallel.

Pipeline parameter:

```text
GRID_TARGET = aws
```

Infrastructure-specific values such as public IPs, SSH keys, AWS account identifiers, S3 bucket names, and Jenkins credentials are intentionally externalized.

## Reporting

Allure is used as the CI reporting layer. Test results are generated under:

```text
target/allure-results
```

## Security

The repository avoids storing operational secrets or environment-specific infrastructure values. Credentials, private keys, cloud identifiers, and runtime configuration are expected to be managed through external systems such as Jenkins Credentials, AWS IAM, or private operational runbooks.
