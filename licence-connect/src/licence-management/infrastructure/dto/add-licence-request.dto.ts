import { IsArray, IsNotEmpty, IsString, ValidateNested } from 'class-validator';
import { StudentId } from '@licences/domain/student';
import { LicenceDto } from './licence.dto';
import { ApiExtraModels } from '@nestjs/swagger';
import { Type } from 'class-transformer';

@ApiExtraModels(LicenceDto)
export class AddLicenceRequestDto {
  @IsNotEmpty()
  @IsString()
  studentId!: StudentId;

  @ValidateNested({ each: true })
  @IsArray()
  @IsNotEmpty()
  @Type(() => LicenceDto)
  licencesToAdd!: LicenceDto[];
}
