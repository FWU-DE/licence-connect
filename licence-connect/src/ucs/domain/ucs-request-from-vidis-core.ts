import { SimpleBundeslandAbbreviationString } from '@vidis/domain/federal-state-id';
import { UcsStudentId } from './ucs-types';
import { UcsStudentContextId } from '@ucs/infrastructure/dto/ucs-type.dto';

/**
 * An incoming licence request for a specific user
 * This follows the JWT Token fields, see https://www.iana.org/assignments/jwt/jwt.xhtml
 */
export interface UcsRequestFromVidisCore {
  userId: UcsStudentId;
  clientId: ClientId;
  schulkennung?: UcsStudentContextId;
  bundesland: SimpleBundeslandAbbreviationString;
}

export type ClientId = string;
