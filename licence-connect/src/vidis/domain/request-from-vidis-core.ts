import { SimpleFederalStateAbbreviationString } from '@vidis/domain/federal-state-id';
import { UcsStudentId } from '../../ucs/domain/ucs-types';

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export interface RequestFromVidisCore {
  userId: UcsStudentId;
  clientId: ClientId;
  schulkennung: Schulkennung;
  bundesland: SimpleFederalStateAbbreviationString;
}

export type ClientId = string;
export type Schulkennung = string;
