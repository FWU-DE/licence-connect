export type Licence = string;

export type Licences = {
  hasLicence: boolean;
  licences: Licence[];
};

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export type IncomingLicenceRequest = {
  exp: number; // "Expiration Time"
  iat: number; // "Issued At"
  auth_time: number; // "Time when the authentication occurred"
  jti: string; /// "JWT ID"
  iss: string; // "Issuer"
  aud: string; // "Audience"
  sub: string;
  typ: string;
  azp: string; // Authorized party - the party to which the ID Token was issued
  session_state: string;
  email: string;
  sid: string; // session id
  schulkennung: string;
  bundesland: string;
  vorname: string;
  nachname: string;
};
