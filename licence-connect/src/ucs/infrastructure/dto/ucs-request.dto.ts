import {
  allFederalStateAbbreviations,
  FederalStateAbbreviation,
} from '@vidis/domain/federal-state-id';
import { ClientId } from '@vidis/domain/request-from-vidis-core';
import { IsIn, IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { UcsStudentId } from '@ucs/domain/ucs-types';
import { UcsStudentContextId } from './ucs-type.dto';

export class UcsRequestDto {
  @IsNotEmpty()
  public userId!: UcsStudentId;

  @IsNotEmpty()
  public clientId!: ClientId;

  @IsOptional()
  @IsString()
  public ucsContextIdentifier?: UcsStudentContextId;

  @IsIn(allFederalStateAbbreviations)
  @IsNotEmpty()
  public federalState!: FederalStateAbbreviation;
}
