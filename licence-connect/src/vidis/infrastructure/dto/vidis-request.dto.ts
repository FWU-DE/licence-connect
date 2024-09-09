import {
  allFederalStateAbbreviations,
  FederalStateAbbreviation,
} from '@vidis/domain/federal-state-id';
import {
  ClientId,
  SchoolIdentifier,
} from '@vidis/domain/request-from-vidis-core';
import { StudentId } from '@vidis/domain/student';
import { IsIn, IsNotEmpty } from 'class-validator';

export class VidisRequestDto {
  @IsNotEmpty()
  public userId!: StudentId;
  @IsNotEmpty()
  public clientId!: ClientId;
  @IsNotEmpty()
  public schulkennung!: SchoolIdentifier;

  @IsIn(allFederalStateAbbreviations)
  @IsNotEmpty()
  public bundesland!: FederalStateAbbreviation;
}
