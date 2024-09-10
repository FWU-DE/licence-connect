import {
  allBundeslandAbbreviations,
  BundeslandAbbreviation,
} from '@vidis/domain/federal-state-id';
import { ClientId } from '@vidis/domain/request-from-vidis-core';
import { IsIn, IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { UcsStudentId } from '@ucs/domain/ucs-types';
import { UcsStudentContextId } from './ucs-type.dto';
import { UcsRequestFromVidisCore } from '@ucs/domain/ucs-request-from-vidis-core';

export class UcsRequestDto implements UcsRequestFromVidisCore {
  @IsNotEmpty()
  @IsString()
  public userId!: UcsStudentId;

  @IsNotEmpty()
  @IsString()
  public clientId!: ClientId;

  @IsOptional()
  @IsString()
  public schulkennung?: UcsStudentContextId;

  @IsIn(allBundeslandAbbreviations)
  @IsNotEmpty()
  public bundesland!: BundeslandAbbreviation;
}
