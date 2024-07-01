import {
  bundeslandIdentificationString,
  BundeslandIdentificationString,
} from '@licences/domain/ferderal-state-id';
import { ClientId, Schulkennung } from '@vidis/domain/request-from-vidis-core';
import { IsIn } from 'class-validator';
import { UCSStudentId } from '@ucs/domain/ucs-types';

export class VidisRequestDto {
  public userId: UCSStudentId;
  public clientId: ClientId;
  public schulkennung: Schulkennung;

  @IsIn(bundeslandIdentificationString)
  public bundesland: BundeslandIdentificationString;
}
