# Selenium Cucumber Automation Framework

End-to-end web automation framework built with Selenium WebDriver, Java, Cucumber, JUnit 5, Maven, Docker, Jenkins, Allure, AWS EC2, and S3.

## Overview

The framework runs browser tests locally or from Jenkins through Selenium Grid. It keeps test scenarios readable with Cucumber, UI interactions maintainable with Page Object Model, and execution evidence available through logs, screenshots, and Allure reports.

## Key Capabilities

- Cucumber scenarios for login and purchase flows.
- Page Object Model for reusable UI actions.
- Chrome and Firefox execution through Selenium Grid.
- Jenkins pipeline with local and cloud-ready execution modes.
- Allure reporting for test results and evidence.

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

## Project Structure

```text
selenium-cucumber/
|-- infra/
|   |-- jenkins/
|       |-- Dockerfile
|       |-- docker-compose-jenkins.aws.yml
|       |-- docker-compose-jenkins.yml
|-- src/test/java/
|   |-- commons/
|   |-- data/
|   |-- definitions/
|   |-- framework/
|   |-- pages/
|-- src/test/resources/
|   |-- features/
|   |-- allure.properties
|   |-- junit-platform.properties
|   |-- logback.xml
|-- docker-compose.yaml
|-- Jenkinsfile
|-- pom.xml
|-- README.md
```

## Prerequisites

- Java 17
- Maven 3.9+
- Docker
- Docker Compose
- ngrok (optional, for local Jenkins webhooks)

## Local Execution

Git Bash:

```bash
docker network inspect selenium-grid >/dev/null 2>&1 || docker network create selenium-grid
docker compose -p selenium-cucumber-grid -f docker-compose.yaml up -d
```

CMD:

```cmd
docker network inspect selenium-grid >nul 2>&1 || docker network create selenium-grid
docker compose -p selenium-cucumber-grid -f docker-compose.yaml up -d
```

Selenium Grid UI:

```text
http://localhost:4444
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
Stop Selenium Grid:

```bash
docker compose -p selenium-cucumber-grid -f docker-compose.yaml down
```

If Docker reports a container name conflict, remove old Selenium containers:

```bash
docker rm -f selenium-hub chrome-node firefox-node
```

## Jenkins Execution

Start Jenkins locally:

```bash
docker compose -f infra/jenkins/docker-compose-jenkins.yml up -d
```

Open Jenkins:

```text
http://localhost:8080
```

Expose local Jenkins for multibranch webhooks:

```bash
ngrok http 8080
```
Pipeline parameter:

```text
GRID_TARGET = local
```

## Reporting

Allure results:

```text
target/allure-results
```

Open the report locally:

```bash
allure serve target/allure-results
```

## AI-Assisted Diagnostics MVP

The framework includes a supervised AI-assisted diagnostics layer for failed scenarios.
It does not change code, retry blindly, push, merge, or make business decisions.

When a scenario fails, Allure can include:

- failure category
- probable cause
- impacted Page Object or step class when detected
- root exception
- confidence level
- suggested human action
- agent review context
- supervised agent recommendation

The default packages match this demo framework, but another business project can adapt them without code changes:

```bash
mvn test \
  -Ddiagnostics.pageObjectPackage=pages \
  -Ddiagnostics.stepDefinitionPackage=definitions \
  -Ddiagnostics.testSourceRoot=src/test/java
```

For another project, replace those values with its real packages, for example:

```bash
mvn test \
  -Ddiagnostics.pageObjectPackage=com.company.app.ui.pages \
  -Ddiagnostics.stepDefinitionPackage=com.company.app.steps \
  -Ddiagnostics.testSourceRoot=src/test/java
```
