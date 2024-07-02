import { StudentId } from '@licences/domain/student';
import { LicenceDto } from './licence.dto';
import { Type } from 'class-transformer';
import { IsNotEmpty, ValidateNested } from 'class-validator';

export class RemoveLicenceRequestDto {
  @IsNotEmpty()
  studentId: StudentId;

  @ValidateNested({ each: true })
  @Type(() => LicenceDto)
  licencesToRemove?: LicenceDto[];
}
