import { ApiExtraModels, ApiProperty, getSchemaPath } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import {
  IsArray,
  IsNotEmpty,
  IsOptional,
  IsString,
  ValidateNested,
} from 'class-validator';

export type UCSLicence = string;
export type AvailableUCSLicences = UCSLicence[];

export type UcsStudentContextId = string;

export class UCSClassDto {
  @IsNotEmpty()
  @IsString()
  name!: string;

  @IsNotEmpty()
  @IsString()
  id!: string;

  @IsOptional()
  @IsArray()
  licenses?: AvailableUCSLicences;
}

export class UCSWorkgroupDto {
  @IsNotEmpty()
  @IsString()
  name!: string;

  @IsNotEmpty()
  @IsString()
  id!: string;

  @IsOptional()
  @IsArray()
  licenses?: AvailableUCSLicences;
}

export type UCSRoles = string;

export type UCSStudentId = string;

export class UCSStudentContext {
  @IsOptional()
  @IsArray()
  licenses?: AvailableUCSLicences;

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => UCSClassDto)
  classes?: UCSClassDto[];

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => UCSWorkgroupDto)
  workgroups?: UCSWorkgroupDto[];

  @IsOptional()
  @IsString()
  school_authority?: string;

  @IsOptional()
  @IsString()
  school_identifier?: string;

  @IsOptional()
  @IsString()
  school_name?: string;

  @IsNotEmpty()
  roles!: UCSRoles[];
}

@ApiExtraModels(UCSStudentContext)
export class UCSStudentDto {
  @IsNotEmpty()
  @IsString()
  id!: string;

  @IsOptional()
  @IsString()
  first_name?: string;

  @IsOptional()
  @IsString()
  last_name?: string;

  @IsOptional()
  @IsArray()
  licenses?: AvailableUCSLicences;

  @IsNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => UCSStudentContext)
  @ApiProperty({
    type: 'object',
    additionalProperties: { $ref: getSchemaPath(UCSStudentContext) },
  })
  context!: {
    [key: UcsStudentContextId]: UCSStudentContext;
  };
}
