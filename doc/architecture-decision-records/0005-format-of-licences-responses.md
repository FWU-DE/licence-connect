# ADR 5: Licence responses format

Date: 2025-03-20

## Status

Accepted

## Context

The goal of Licence Connect is to provide a unified interface for various licence providers which all respond with different formats. 
We are looking for a solution which provides a unified, easy to understand and future-proof interface for all licence providers.

## Considered Options

### ODRL

**Pros**:
- [ODRL](https://www.w3.org/TR/odrl-model/) is a well-established standard for expressing rights and obligations in digital content.
- The client advocated for ODRL as a standard
- It is used by [schulconnex](https://schulconnex.de/)

**Cons**:
- Many fields provided by ODRL are interesting but we cannot fill them with data because the licence holders do not provide them.

## Decision

We choose ODRL because of the following reasons:
1. The client advocated for ODRL as a standard

## Consequences
