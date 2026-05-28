# Framework Architecture Notes

## Purpose

This document complements `docs/architecture.html` with the detailed study view of the framework.
The HTML file is the visual architecture map; this file keeps the deeper explanation so the diagram stays clean.

## Main Execution Flow

```text
GitHub
-> Jenkins Multibranch
-> Maven
-> Cucumber / JUnit Platform
-> Selenium Grid
-> Chrome or Firefox
-> Application under test
-> Allure report
-> Assisted diagnostics when enabled
```

## Test Architecture

```text
Feature files
-> Step Definitions
-> Data Resolvers
-> Page Objects
-> Selenium WebDriver
-> Browser
```

## Feature Layer

Feature files describe business-readable scenarios.

Current examples:

- `login.feature`
- `purchase.feature`

The examples use logical data keys such as:

- `USER_OK`
- `PASS_OK`
- `PRODUCT_NAME_BACKPACK`

This keeps test scenarios readable and avoids hardcoding concrete data directly in the Gherkin steps.

## Data Layer

Property files keep reusable test data:

- `auth.properties`
- `purchase.properties`

Resolver classes translate logical keys into concrete values:

- `LoginDataResolver`
- `PurchaseDataResolver`

Flow:

```text
Feature example key
-> DataResolver
-> properties file
-> resolved value
-> Step Definition
```

## Step Definition Layer

Step definitions orchestrate the scenario.
They should not own Selenium locators.

Current examples:

- `LoginDefinition`
- `ProductPurchasesDefinition`

Their role is to connect Cucumber steps with Page Object behavior.

## Page Object Layer

Page Objects own UI locators and page-level behavior.

Current examples:

- `LoginPage`
- `ProductsPage`
- `CartPage`
- `CheckoutPage`

Expected responsibility:

- locate elements
- wait for UI state
- click, type, validate
- expose readable page actions

## Reusable Utilities

Reusable framework support lives outside Page Objects:

- `Hooks`: WebDriver lifecycle, browser selection, Grid URL resolution, evidence capture
- `Utils`: waits and browser helper methods
- `ScreenshotUtil`: failure screenshot attachments
- `RetryExtension`: controlled CI retry for technical failures

## Reporting and Diagnostics

Allure is the main reporting layer.

Normal failure evidence:

- screenshot
- browser evidence
- scenario status

Assisted diagnostics can add:

- failure category
- probable cause
- confidence
- Page Object or step class when detected
- method
- locator
- root exception
- suggested human action

## Assisted Diagnostics Modes

Jenkins parameter:

```text
ASSISTED_DIAGNOSTICS=off
ASSISTED_DIAGNOSTICS=diagnosis
ASSISTED_DIAGNOSTICS=agent
```

Meaning:

- `off`: normal execution
- `diagnosis`: assisted failure analysis in the report
- `agent`: prepares context for an external AI review

## Local and AWS Execution Targets

Local execution uses Docker Selenium Grid.

AWS execution is prepared in the Jenkinsfile through:

```text
GRID_TARGET=aws
```

When AWS is selected, the pipeline is prepared to run Chrome and Firefox in parallel against the AWS Grid URL.
Allure results can be uploaded to S3 when the bucket configuration is enabled.

## External AI Provider

The framework is provider-ready, not provider-locked.

Possible providers:

- OpenAI API
- Azure OpenAI
- Anthropic
- AWS Bedrock
- Google Vertex AI
- internal company model

The intended flow is:

```text
Allure failure diagnosis
-> compact AI payload
-> external provider
-> recommendation report
-> human review
```

The AI must not:

- modify code automatically
- push or merge
- hide failures
- decide business rules alone
- apply retries to real defects
