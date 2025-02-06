# ADR 2: Authentication for MVP API

Date: 2024-05-29

## Status

Accepted

## Context

Our API is designed to be consumed solely by one other service (_VIDIS Kern_), requiring a secure authentication mechanism suitable for machine-to-machine (M2M) communications. The need for a simple, secure, and fast-to-implement authentication method is key to providing a MVP on time and within budget while taking the quality requirement for a secure application into account. We aim for an authentication method that can easily be adopted by _VIDIS Kern_ without extensive overhead or complexity while adhering to security best practices.

## Considered Options

Considering the need for M2M authentication, the following options were evaluated:

### Option 1: API Keys

A straightforward approach where a unique secret key is issued to the consuming service, which must be included in all API requests.

**Pros**:

- Effortless to implement and integrate, making it suitable for an MVP.
- Low latency overhead as it involves simple request header checks.

**Cons**:

- Less secure due to the static nature of the API key and the potential of key leakage.
- Lack of built-in mechanisms for key invalidation, rotation, or detailed permissions.

### Option 2: Mutual TLS (mTLS)

Both _VIDIS Kern_ and _LC_ mutually authenticate each other using digital certificates.

**Pros**:

- High level of security by ensuring authenticity on both ends.
- Avoids the need to manage authorization tokens or session states.

**Cons**:

- More complex to set up and may require infrastructure changes for certificate management.
- Potentially overcomplicated for an MVP that could be iterated with more advanced solutions in time.

### Option 3: OAuth 2.0 Client Credentials Grant

A subset of OAuth 2.0 that provides a method for clients to obtain a token representing their authorization to access resources.

**Pros**:

- Standardized and well-supported method for M2M authentication with the potential for future scaling.
- Provides a clear path for access token rotation and revocation.

**Cons**:

- Requires an initial setup of a new Client for _LC_ in _VIDIS Kern_.
- Increases the complexity of the MVP compared to API keys.

## Decision

Given the context and the need for an MVP, we have decided to go with API Keys for our M2M communications authentication mechanism. This method provides the simplicity and speed of implementation we require for our MVP, allowing us to implement _LC_ while still providing an adequate level of security for initial phase M2M interactions.

## Consequences

- We must generate and securely store unique API keys for each stage.
- Our API needs to handle the validation of API keys on each request.
- Although simple, we accept the potential risk of key leakage, which we will mitigate through strict controls and guidelines around key usage and transmission.
- We recognize the need for planning on a more robust authentication system in subsequent iterations to improve security as our service scales and evolves.
