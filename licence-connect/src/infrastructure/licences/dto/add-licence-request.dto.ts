import { StudentId } from 'domain/student';
import { LicenceDto } from './licence.dto';
import { Type } from 'class-transformer';
import { IsNotEmpty } from 'class-validator';

export class AddLicenceRequestDto {
  @IsNotEmpty()
  studentId: StudentId;
  @Type(() => LicenceDto)
  licencesToAdd: LicenceDto[];
}
