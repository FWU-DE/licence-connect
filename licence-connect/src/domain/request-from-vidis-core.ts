import { UCSStudentId } from './ucs/ucs-types';

export const bundeslandIdentificationString = ['DE-MV', 'DE-RP'];
export type BundeslandIdentificationString =
  (typeof bundeslandIdentificationString)[number];

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export interface RequestFromVidisCore {
  userId: UCSStudentId;
  clientId: ClientId;
  schulkennung: Schulkennung;
  bundesland: BundeslandIdentificationString;
}

export type ClientId = string;
export type Schulkennung = string;
