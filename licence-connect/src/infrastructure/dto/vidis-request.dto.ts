import {
  BundeslandIdentificationString,
  ClientId,
  Schulkennung,
} from 'domain/request-from-vidis-core';
import { UCSStudentId } from 'domain/ucs/ucs-types';

export class VidisRequestRequest {
  public userId: UCSStudentId;
  public clientId: ClientId;
  public schulkennung: Schulkennung;
  public bundesland: BundeslandIdentificationString;
}
