# ADR 4: Selection of ARIX as Primary Licence Provider

Date: 2025-03-20

## Status

Accepted

## Context

For the Licence Connect platform, we need to integrate with at least one licence holder system to demonstrate core functionality.
The licence holder system serves as the source of truth for available licences and needs to be reliable, well-documented, and compatible with our architecture.

We need to select an initial licence provider that can be integrated quickly while providing a foundation for adding more providers in the future.

## Considered Options

### ARIX

**Pros**:
- Already successfully used in other FWU products, providing proven integration patterns
- Established provider with stable API
- Existing organizational expertise with this system
- Documentation and support available

**Cons**:
- Not a classical licence holder with licence codes but provides URLs instead
- Requires additional mapping/transformation layer to standardize response formats
- XML-based response format requires more parsing effort
- Requires Landkreis (district) information for licence retrieval, which is not available from VIDIS

### Opal

**Pros**:
- Open source alternative
- No licensing costs

**Cons**:
- Code available on GitHub is of low quality
- Implementation did not work during tryout
- Limited documentation and support
- Would require significant effort to make production-ready

### Mkis

**Pros**:
- Modern architecture
- Promising future roadmap

**Cons**:
- Still in development and not ready for production use
- Lacks stable API
- Integration timeline uncertain

### Sesame

**Pros**:
- Alternative solution with different approach
- Could provide different perspective

**Cons**:
- Determined unsuitable by client after evaluation

## Decision

We choose ARIX as the primary licence provider for the following reasons:
1. Integration efficiency due to existing experience and documentation
2. Proven reliability in other FWU products
3. Despite not being a classical licence holder with licence codes, its API is stable and well-understood
4. The alternatives evaluated did not meet our requirements for production readiness or client approval

## Consequences

- Implementation will focus on ARIX integration first
- We will need to handle URL-based licensing rather than code-based licensing
- XML response format requires specialized parsing (already implemented with `LicencesParser`)
- Future licence providers should adapt to the integration pattern established with ARIX or require dedicated endpoints as outlined in [ADR 3: Endpoint Architecture for Licence Provider Integration](/home/frederik/projects/fwu/API/doc/architecture-decision-records/0003-endpoint-design.md)
- We may need to revisit this decision when other providers become more mature
