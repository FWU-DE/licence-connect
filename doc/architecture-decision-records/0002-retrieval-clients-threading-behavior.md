# ADR 2: Default Synchronicity of Licence Retrieval Clients

Date: 2025-02-12

## Status

Accepted

## Context

For every Licence server, we will provide a licence retrieval client. 

The internal licence connector will use the retrieval clients to retrieve licences information from the licence holding servers.

The question is whether the default behavior of licence retrieval clients is synchronous (i.e., returning Monos)
or asynchronous (i.e., returning standard non-Mono datatypes).

## Considered Options

### Default Behavior: Synchronous (returning Mono)

**Pros**:

- Default behavior of retrieval clients is synchronous, and as such more time-efficient. Slower sequential asynchronous usage must be explicitly enforced.

**Cons**:

- Higher code complexity when calling licence clients, as Monos must be dealt with.
- Higher testing complexity, as effects of synchronous client usage needs to be covered in testing (additionally to testing sound individual client behavior).

### Standard Behavior: Asynchronous (returning conventional data types)

Pros and Cons are the inverse of the above.

## Decision

We switch to Synchronous as standard behavior for the following reasons:
1. We expect that licence connect will eventually include a multitude of licence providers, making time efficiency an important factor.
2. We expect that a focus on time efficiency from the start is selling point for licence connect.

## Consequences

- Retrieval clients should return Monos by default.