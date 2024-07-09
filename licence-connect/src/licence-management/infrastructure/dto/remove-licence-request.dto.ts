import { StudentId } from '@licences/domain/student';
import { LicenceDto } from './licence.dto';
import {
  IsArray,
  IsNotEmpty,
  IsOptional,
  IsString,
  ValidateNested,
} from 'class-validator';
import { ApiExtraModels } from '@nestjs/swagger';

@ApiExtraModels(LicenceDto)
export class RemoveLicenceRequestDto {
  @IsNotEmpty()
  @IsString()
  studentId!: StudentId;

  @ValidateNested({ each: true })
  @IsOptional()
  @IsArray()
  licencesToRemove?: LicenceDto[];
}
