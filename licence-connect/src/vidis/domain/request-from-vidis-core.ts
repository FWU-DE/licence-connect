import { SimpleBundeslandAbbreviationString } from '@vidis/domain/federal-state-id';
import { StudentId } from './student';

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export interface RequestFromVidisCore {
  userId: StudentId;
  clientId: ClientId;
  schulkennung: SchoolIdentifier;
  bundesland: SimpleBundeslandAbbreviationString;
}

export type ClientId = string;
export type SchoolIdentifier = string;
