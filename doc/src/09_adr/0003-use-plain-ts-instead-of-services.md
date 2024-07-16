# ADR 3: Use Use Case for Clean Architecture instead of injectable Services

Date: 2024-07-15

## Status

Draft

## Context

In our efforts to maintain a scalable and maintainable codebase, we are considering the implementation of Clean Architecture principles.
These principles emphasize the separation of concerns by layering the software so that the business logic is at the core and independent of frameworks and potential databases. 
In NestJs normally services are used in order to inject functionality into controller and other services.
NestJs normally provides a dependency injection framework which require additional annotations in injected classes.
In Clean Architecture such annotations in the source code are considered to not follow the separation of concern.
Therefore a decision is needed, whether we want to use use cases or injectable services.
Choosing clean architecture or a framework first approach leads to different consequences when it comes to maintainability and extendability.

## Considered Options

- **Option 1: Use NestJS services for use cases and use a framework first approach**
- **Option 2: Implement Clean Architecture by using plain TypeScript classes for use cases**

### Option 1: Continue using NestJS services for use cases

Handling use cases directly within NestJS services, which provides tight integration with the NestJS framework.

**Pros**:

- Easier to leverage existing NestJS module ecosystem and features.
- Reduced initial effort in refactoring the current implementation.

**Cons**:

- Tightly couples use cases with the framework, potentially causing challenges when upgrading or replacing parts of the system.
- Deviates from Clean Architecture principles, possibly leading to a codebase that is harder to test, maintain, and extend.

### Option 2: Implement Clean Architecture by using plain TypeScript classes for use cases

Adopt a strict Clean Architecture approach by implementing our use cases into plain TypeScript classes, independent of the NestJS framework.

**Pros**:

- Promotes a clear separation of concerns, aligning with the Clean Architecture model.
- Offers greater flexibility and portability of the business logic.
- Makes the business rules easier to test in isolation from the framework and infrastructure.

**Cons**:

- Requires new developers to become familiar with new patterns and potentially adapt their workflow.

## Decision

We have decided to go with **Option 2: Implement Clean Architecture by using plain TypeScript classes for use cases**. 
This decision aligns with our goals of keeping our core business logic decoupled from the framework and infrastructure, ensuring that our application remains flexible, maintainable, and extensible as it evolves.

## Consequences

The decision to adhere to Clean Architecture principles through the use of plain TypeScript classes for use cases will require developer that are familiar with NestJs to accept the slightly different approach and learn the patterns of Clean Architecture. 
It is expected to provide long-term benefits in terms of testability, maintenance, and the ability to adapt to changes in technology or business requirements.
