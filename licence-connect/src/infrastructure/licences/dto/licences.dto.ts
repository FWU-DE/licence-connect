import { ApiProperty } from '@nestjs/swagger';
import { AvailableLicences } from 'domain/licence';
import { licenceSchema } from '../../schema/licence-schema';

/**
 * Dto for providing the licences to VIDIS Kern
 */
export class LicencesDto {
  constructor(licences: AvailableLicences) {
    this.hasLicence = licences.length !== 0;
    this.licences = licences;
  }

  hasLicence: boolean;
  @ApiProperty({
    type: 'array',
    items: licenceSchema,
  })
  licences: AvailableLicences;
}
