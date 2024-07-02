import { Type } from 'class-transformer';
import { IsNotEmpty, ValidateNested } from 'class-validator';
import { StudentId } from '@licences/domain/student';
import { LicenceDto } from './licence.dto';
import { EducationalOfferId } from '@licences/domain/educational-offer';

export class AddLicenceRequestDto {
  @IsNotEmpty()
  studentId: StudentId;

  @ValidateNested({ each: true })
  @Type(() => LicenceDto)
  licencesToAdd: Record<EducationalOfferId, LicenceDto>;
}
