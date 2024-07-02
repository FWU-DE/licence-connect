import { StudentId } from '@licences/domain/student';
import { LicenceDto } from './licence.dto';
import { Type } from 'class-transformer';
import { IsNotEmpty, ValidateNested } from 'class-validator';
import { EducationalOfferId } from '@licences/domain/educational-offer';
import { ApiProperty } from '@nestjs/swagger';

export class RemoveLicenceRequestDto {
  @IsNotEmpty()
  studentId!: StudentId;

  @ValidateNested({ each: true })
  @Type(() => LicenceDto)
  @ApiProperty({
    type: 'object',
    additionalProperties: {
      type: 'object',
    },
  })
  licencesToRemove?: Record<EducationalOfferId, LicenceDto>;
}
