# ADR 2: Default Threading Behavior of Licence Retrieval Clients

Date: 2025-02-12

## Status

Accepted

## Context

For every Licence server, we will provide a licence retrieval client. 

The internal licence connector will ues the retrival clients to retrieve licences information from the licence holding servers.

The question is whether the default behavior of licence retrieval clients is multithreading (i.e., returning Monos)
or sequential (i.e., returning standard non-Mono datatypes).

## Considered Options

### Standard Behavior: Multithreading (returning Mono)

**Pros**:

- Default behavior of retrieval clients is time-efficient. Slower sequential usage of various retrieval clients must be explicitly enforced.

**Cons**:

- Higher code complexity when calling licence clients, as Monos must be dealt with.
- Higher Testing complexity, as multithreading needs be covered in testing (additionally to individual client behavior)

### Standard Behavior: Singlethreading (returning Mono)

Pros and Cons are the inverse of the above.

## Decision

We switch to Multithreading as standard behavior for the following reasons:
1. We expect that licence connect will eventually include a multitude of licence providers, making time efficiency an important factor.
2. We expect that a focus on time efficiency from the start is selling point for licence connect.

## Consequences

- Retrieval clients should return Monos by default.