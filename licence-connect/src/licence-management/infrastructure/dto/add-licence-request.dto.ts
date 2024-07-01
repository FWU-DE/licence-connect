import { LicenceDto } from '@licences/infrastructure/licence.dto';
import { Type } from 'class-transformer';
import { IsNotEmpty, ValidateNested } from 'class-validator';
import { StudentId } from '@licences/domain/student';

export class AddLicenceRequestDto {
  @IsNotEmpty()
  studentId: StudentId;

  @ValidateNested({ each: true })
  @Type(() => LicenceDto)
  licencesToAdd: LicenceDto[];
}
