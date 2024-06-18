export type BundeslandIdentificationString = string;

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export type RequestFromVidisCore = {
  exp: TimeForTokenExpiration;
  iat: TimeWhenTheTokenWasIssuedAt;
  auth_time: TimeWhenTheAuthenticationOccured;
  jti: JWTTokenIdentifier;
  iss: JWTTokenIssuer;
  aud: JWTTokenAudience;
  sub: JWTTokenSubject;
  typ: TypType;
  azp: AuthorizedParty;
  session_state: SessionState;
  email: Email;
  sid: SessionId;
  schulkennung: Schulkennung;
  bundesland: BundeslandIdentificationString;
  vorname: Firstname;
  nachname: Surname;
};

type TimeForTokenExpiration = number;
type TimeWhenTheTokenWasIssuedAt = number;
type TimeWhenTheAuthenticationOccured = number;
type JWTTokenIdentifier = string;
type JWTTokenIssuer = string;
type JWTTokenAudience = string;
type JWTTokenSubject = string;
type Email = string;
type SessionId = string;
type Schulkennung = string;
type Firstname = string;
type Surname = string;

type SessionState = unknown;
type AuthorizedParty = string; // Authorized party - the party to which the ID Token was issued
type TypType = unknown;
