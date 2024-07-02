import {
  bundeslandIdentificationString,
  BundeslandIdentificationString,
} from '@licences/domain/ferderal-state-id';
import { ClientId, Schulkennung } from '@vidis/domain/request-from-vidis-core';
import { IsIn, IsNotEmpty } from 'class-validator';
import { UCSStudentId } from '@ucs/domain/ucs-types';

export class VidisRequestDto {
  @IsNotEmpty()
  public userId!: UCSStudentId;
  @IsNotEmpty()
  public clientId!: ClientId;
  @IsNotEmpty()
  public schulkennung!: Schulkennung;

  @IsIn(bundeslandIdentificationString)
  @IsNotEmpty()
  public bundesland!: BundeslandIdentificationString;
}
