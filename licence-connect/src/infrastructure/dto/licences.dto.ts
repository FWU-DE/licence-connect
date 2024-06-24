import { ApiProperty } from '@nestjs/swagger';
import { AvailableLicences } from 'domain/licence';
import { licenceSchema } from 'infrastructure/schema/LicenceSchema';

/**
 * Dto for providing the licences to VIDIS Kern
 */
export class LicencesDto {
  constructor(licences: AvailableLicences) {
    this.hasLicence = !!licences;
    this.licences = licences;
  }

  hasLicence: boolean;
  @ApiProperty({
    type: 'array',
    items: licenceSchema,
  })
  licences: AvailableLicences;
}
