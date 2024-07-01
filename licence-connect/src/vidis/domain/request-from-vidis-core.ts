import { BundeslandIdentificationString } from '@licences/domain/ferderal-state-id';
import { UCSStudentId } from '../../ucs/domain/ucs-types';

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
