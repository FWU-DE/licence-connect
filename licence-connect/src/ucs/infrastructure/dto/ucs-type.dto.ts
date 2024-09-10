import { ApiExtraModels, ApiProperty, getSchemaPath } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import {
  IsArray,
  IsNotEmpty,
  IsOptional,
  IsString,
  ValidateNested,
} from 'class-validator';

export type UcsLicence = string;
export type AvailableUcsLicences = UcsLicence[];

export type UcsStudentContextId = string;

export class UcsClassDto {
  @IsNotEmpty()
  @IsString()
  name!: string;

  @IsNotEmpty()
  @IsString()
  id!: string;

  @IsOptional()
  @IsArray()
  licenses?: AvailableUcsLicences;
}

export class UcsWorkgroupDto {
  @IsNotEmpty()
  @IsString()
  name!: string;

  @IsNotEmpty()
  @IsString()
  id!: string;

  @IsOptional()
  @IsArray()
  licenses?: AvailableUcsLicences;
}

export type UcsRoles = string;

export type UcsStudentId = string;

export class UcsStudentContext {
  @IsOptional()
  @IsArray()
  licenses?: AvailableUcsLicences;

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => UcsClassDto)
  classes?: UcsClassDto[];

  @IsOptional()
  @ValidateNested({ each: true })
  @Type(() => UcsWorkgroupDto)
  workgroups?: UcsWorkgroupDto[];

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
  roles!: UcsRoles[];
}

@ApiExtraModels(UcsStudentContext)
export class UcsStudentDto {
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
  licenses?: AvailableUcsLicences;

  @IsNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => UcsStudentContext)
  @ApiProperty({
    type: 'object',
    additionalProperties: { $ref: getSchemaPath(UcsStudentContext) },
  })
  context!: {
    [key: UcsStudentContextId]: UcsStudentContext;
  };
}
