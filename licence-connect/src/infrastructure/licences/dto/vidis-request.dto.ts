import { IsIn } from 'class-validator';
import {
  BundeslandIdentificationString,
  ClientId,
  Schulkennung,
  bundeslandIdentificationString,
} from '../../../domain/request-from-vidis-core';
import { UCSStudentId } from '../../../domain/ucs/ucs-types';

export class VidisRequestDto {
  public userId: UCSStudentId;
  public clientId: ClientId;
  public schulkennung: Schulkennung;

  @IsIn(bundeslandIdentificationString)
  public bundesland: BundeslandIdentificationString;
}
